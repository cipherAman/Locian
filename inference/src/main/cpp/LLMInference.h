#pragma once
#include "chat.h"
#include "common.h"
#include "llama.h"
#include <string>
#include <vector>

class LLMInference {
    llama_context* _ctx       = nullptr;
    llama_model*   _model     = nullptr;
    llama_sampler* _sampler   = nullptr;
    llama_token    _currToken = 0;
    llama_batch*   _batch     = nullptr;

    std::vector<llama_chat_message> _messages;
    std::vector<char> _formattedMessages;
    std::vector<llama_token> _promptTokens;
    const char*              _chatTemplate = nullptr;
    std::string              _systemPrompt;

    std::string _response;
    std::string _cacheResponseTokens;
    bool _storeChats = false;
    bool _promptPrefilledThink = false;
    bool _insideThink = false;
    int  _thinkingTokens = 0;
    int  _maxThinkingTokens = 384;
    std::vector<llama_token> _forceTokens;

    int64_t _responseGenerationTime = 0;
    long    _responseNumTokens      = 0;
    int64_t _timeToFirstToken       = 0;
    bool _isPrefill = false;

    int _nCtxUsed = 0;

    bool _isValidUtf8(const char* response);

public:
    void loadModel(const char* modelPath, float minP, float temperature, float topP,
                   float repeatPenalty, int repeatLastN, bool storeChats, long contextSize,
                   const char* chatTemplate, const char* systemPrompt, int nThreads,
                   bool useMmap, bool useMlock);

    void addChatMessage(const char* message, const char* role);

    void clearMessages();

    float getResponseGenerationSpeed() const;

    float getTimeToFirstToken() const;

    int getContextSizeUsed() const;

    bool startCompletion(const char* query);

    std::string completionLoop();

    void stopCompletion();

    ~LLMInference();
};
