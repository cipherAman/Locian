#include "LLMInference.h"
#include <android/log.h>
#include <cstring>
#include <iomanip>
#include <iostream>

#define TAG "[LocianInference-Cpp]"
#define LOGi(...) __android_log_print(ANDROID_LOG_INFO, TAG, __VA_ARGS__)
#define LOGe(...) __android_log_print(ANDROID_LOG_ERROR, TAG, __VA_ARGS__)

void
LLMInference::loadModel(const char *model_path, float minP, float temperature, bool storeChats, long contextSize,
                        const char *chatTemplate, int nThreads, bool useMmap, bool useMlock) {
    LOGi("loading model from: %s (threads: %d, ctx: %ld, mmap: %d)", model_path, nThreads, contextSize, useMmap);

    ggml_backend_load_all();

    llama_model_params model_params = llama_model_default_params();
    if (useMmap && useMlock) {
        model_params.load_mode = LLAMA_LOAD_MODE_MMAP_MLOCK;
    } else if (useMmap) {
        model_params.load_mode = LLAMA_LOAD_MODE_MMAP;
    } else if (useMlock) {
        model_params.load_mode = LLAMA_LOAD_MODE_MLOCK;
    } else {
        model_params.load_mode = LLAMA_LOAD_MODE_NONE;
    }

    _model = llama_model_load_from_file(model_path, model_params);
    if (!_model) {
        LOGe("failed to load model from %s", model_path);
        throw std::runtime_error("loadModel() failed: unable to load model from file");
    }

    llama_context_params ctx_params = llama_context_default_params();
    ctx_params.n_ctx = contextSize;
    ctx_params.n_batch = contextSize;
    ctx_params.n_threads = nThreads;
    ctx_params.no_perf = true;

    _ctx = llama_init_from_model(_model, ctx_params);
    if (!_ctx) {
        LOGe("llama_init_from_model() returned null");
        throw std::runtime_error("llama_init_from_model() returned null");
    }

    llama_sampler_chain_params sampler_params = llama_sampler_chain_default_params();
    sampler_params.no_perf = true;
    _sampler = llama_sampler_chain_init(sampler_params);
    if (minP >= 0.01f) {
        llama_sampler_chain_add(_sampler, llama_sampler_init_min_p(minP, 1));
    }
    llama_sampler_chain_add(_sampler, llama_sampler_init_temp(temperature));
    llama_sampler_chain_add(_sampler, llama_sampler_init_dist(LLAMA_DEFAULT_SEED));

    _formattedMessages = std::vector<char>(llama_n_ctx(_ctx));
    _messages.clear();

    if (chatTemplate == nullptr || strlen(chatTemplate) == 0) {
        _chatTemplate = llama_model_chat_template(_model, nullptr);
    } else {
        _chatTemplate = strdup(chatTemplate);
    }
    this->_storeChats = storeChats;
}

void
LLMInference::addChatMessage(const char *message, const char *role) {
    _messages.push_back({strdup(role), strdup(message)});
}

void
LLMInference::clearMessages() {
    for (llama_chat_message& message : _messages) {
        free(const_cast<char*>(message.role));
        free(const_cast<char*>(message.content));
    }
    _messages.clear();
    _response.clear();
    _cacheResponseTokens.clear();
    _promptPrefilledThink = false;
    if (_ctx) {
        llama_memory_clear(llama_get_memory(_ctx), false);
    }
}

float
LLMInference::getResponseGenerationSpeed() const {
    if (_responseNumTokens == 0 || _responseGenerationTime <= 0) {
        return 0.0f;
    }
    return (float)_responseNumTokens / ((float)_responseGenerationTime / 1e6f);
}

float
LLMInference::getTimeToFirstToken() const {
    if (_timeToFirstToken <= 0) {
        return 0.0f;
    }
    return (float)_timeToFirstToken / 1e6f;
}

int
LLMInference::getContextSizeUsed() const {
    return _nCtxUsed;
}

bool
LLMInference::startCompletion(const char *query) {
    llama_memory_clear(llama_get_memory(_ctx), false);
    llama_sampler_reset(_sampler);
    _cacheResponseTokens.clear();

    if (!_storeChats) {
        _formattedMessages.clear();
        _formattedMessages = std::vector<char>(llama_n_ctx(_ctx));
    }
    _responseGenerationTime = 0;
    _responseNumTokens = 0;
    _timeToFirstToken       = 0;
    _isPrefill              = true;
    addChatMessage(query, "user");

    std::vector<common_chat_msg> messages;
    for (const llama_chat_message& message : _messages) {
        common_chat_msg msg;
        msg.role    = message.role;
        msg.content = message.content;
        messages.push_back(msg);
    }
    auto templates = common_chat_templates_init(_model, _chatTemplate ? _chatTemplate : "");

    common_chat_templates_inputs inputs;
    inputs.messages = messages;
    inputs.use_jinja = true;
    inputs.chat_template_kwargs["tools"] = "[]";

    std::string prompt;
    bool usedJinja = true;
    try {
        prompt = common_chat_templates_apply(templates.get(), inputs).prompt.str();
    } catch (const std::exception &e) {
        LOGe("Jinja template failed: %s — retrying with legacy renderer", e.what());
        inputs.use_jinja = false;
        inputs.chat_template_kwargs.clear();
        prompt = common_chat_templates_apply(templates.get(), inputs).prompt.str();
        usedJinja = false;
    }
    _promptTokens = common_tokenize(llama_model_get_vocab(_model), prompt, true, true);

    _promptPrefilledThink = false;
    size_t lastThink = prompt.rfind("<think>");
    if (lastThink != std::string::npos) {
        size_t lastCloseThink = prompt.rfind("</think>");
        if (lastCloseThink == std::string::npos || lastCloseThink < lastThink) {
            _promptPrefilledThink = true;
            LOGi("Detected unclosed <think> tag prefilled in prompt");
        }
    }

    delete _batch;
    _batch = new llama_batch(llama_batch_get_one(_promptTokens.data(), _promptTokens.size()));

    return usedJinja;
}

bool
LLMInference::_isValidUtf8(const char *response) {
    if (!response) {
        return true;
    }
    const unsigned char *bytes = (const unsigned char *) response;
    int num;
    while (*bytes != 0x00) {
        if ((*bytes & 0x80) == 0x00) {
            num = 1;
        } else if ((*bytes & 0xE0) == 0xC0) {
            num = 2;
        } else if ((*bytes & 0xF0) == 0xE0) {
            num = 3;
        } else if ((*bytes & 0xF8) == 0xF0) {
            num = 4;
        } else {
            return false;
        }

        bytes += 1;
        for (int i = 1; i < num; ++i) {
            if ((*bytes & 0xC0) != 0x80) {
                return false;
            }
            bytes += 1;
        }
    }
    return true;
}

std::string
LLMInference::completionLoop() {
    if (_promptPrefilledThink) {
        _promptPrefilledThink = false;
        _response += "<think>\n";
        return "<think>\n";
    }

    uint32_t contextSize = llama_n_ctx(_ctx);
    _nCtxUsed = llama_memory_seq_pos_max(llama_get_memory(_ctx), 0) + 1;
    if (_nCtxUsed + _batch->n_tokens > contextSize) {
        throw std::runtime_error("context size reached");
    }

    auto start = ggml_time_us();
    if (llama_decode(_ctx, *_batch) < 0) {
        throw std::runtime_error("llama_decode() failed");
    }

    _currToken = llama_sampler_sample(_sampler, _ctx, -1);
    auto end   = ggml_time_us();

    if (_isPrefill) {
        _isPrefill = false;
        _timeToFirstToken = (end - start);
    } else {
        _responseGenerationTime += (end - start);
        _responseNumTokens += 1;
    }

    if (llama_vocab_is_eog(llama_model_get_vocab(_model), _currToken)) {
        addChatMessage(strdup(_response.data()), "assistant");
        _response.clear();
        return "[EOG]";
    }

    std::string piece = common_token_to_piece(_ctx, _currToken, true);
    _cacheResponseTokens += piece;

    delete _batch;
    _batch = new llama_batch(llama_batch_get_one(&_currToken, 1));

    if (_isValidUtf8(_cacheResponseTokens.c_str())) {
        _response += _cacheResponseTokens;
        std::string valid_utf8_piece = _cacheResponseTokens;
        _cacheResponseTokens.clear();
        return valid_utf8_piece;
    }

    return "";
}

void
LLMInference::stopCompletion() {
    if (_storeChats && !_response.empty()) {
        addChatMessage(_response.c_str(), "assistant");
    }
    _response.clear();
}

LLMInference::~LLMInference() {
    for (llama_chat_message &message: _messages) {
        free(const_cast<char *>(message.role));
        free(const_cast<char *>(message.content));
    }
    if (_ctx) llama_free(_ctx);
    if (_model) llama_model_free(_model);
    delete _batch;
    if (_sampler) llama_sampler_free(_sampler);
}
