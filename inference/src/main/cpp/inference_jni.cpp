#include "LLMInference.h"
#include <jni.h>

extern "C" JNIEXPORT jlong JNICALL
Java_io_locian_inference_InferenceEngine_loadModel(
    JNIEnv* env, jobject thiz, jstring modelPath, jfloat minP,
    jfloat temperature, jboolean storeChats, jlong contextSize,
    jstring chatTemplate, jint nThreads, jboolean useMmap, jboolean useMlock) {

    jboolean isCopy = true;
    const char* modelPathCstr = env->GetStringUTFChars(modelPath, &isCopy);
    const char* chatTemplateCstr = chatTemplate ? env->GetStringUTFChars(chatTemplate, &isCopy) : nullptr;
    auto* llmInference = new LLMInference();

    try {
        llmInference->loadModel(
            modelPathCstr, minP, temperature, storeChats,
            contextSize, chatTemplateCstr, nThreads, useMmap, useMlock
        );
    } catch (const std::exception& error) {
        env->ReleaseStringUTFChars(modelPath, modelPathCstr);
        if (chatTemplateCstr) env->ReleaseStringUTFChars(chatTemplate, chatTemplateCstr);
        delete llmInference;
        env->ThrowNew(env->FindClass("java/lang/IllegalStateException"), error.what());
        return 0;
    }

    env->ReleaseStringUTFChars(modelPath, modelPathCstr);
    if (chatTemplateCstr) env->ReleaseStringUTFChars(chatTemplate, chatTemplateCstr);
    return reinterpret_cast<jlong>(llmInference);
}

extern "C" JNIEXPORT void JNICALL
Java_io_locian_inference_InferenceEngine_addChatMessage(
    JNIEnv* env, jobject thiz, jlong modelPtr, jstring message, jstring role) {
    jboolean isCopy = true;
    const char* messageCstr = env->GetStringUTFChars(message, &isCopy);
    const char* roleCstr = env->GetStringUTFChars(role, &isCopy);
    auto* llmInference = reinterpret_cast<LLMInference*>(modelPtr);
    if (llmInference) {
        llmInference->addChatMessage(messageCstr, roleCstr);
    }
    env->ReleaseStringUTFChars(message, messageCstr);
    env->ReleaseStringUTFChars(role, roleCstr);
}

extern "C" JNIEXPORT void JNICALL
Java_io_locian_inference_InferenceEngine_clearMessages(
    JNIEnv* env, jobject thiz, jlong modelPtr) {
    auto* llmInference = reinterpret_cast<LLMInference*>(modelPtr);
    if (llmInference) {
        llmInference->clearMessages();
    }
}

extern "C" JNIEXPORT jfloat JNICALL
Java_io_locian_inference_InferenceEngine_getResponseGenerationSpeed(
    JNIEnv* env, jobject thiz, jlong modelPtr) {
    auto* llmInference = reinterpret_cast<LLMInference*>(modelPtr);
    return llmInference ? llmInference->getResponseGenerationSpeed() : 0.0f;
}

extern "C" JNIEXPORT jfloat JNICALL
Java_io_locian_inference_InferenceEngine_getTimeToFirstToken(
    JNIEnv* env, jobject thiz, jlong modelPtr) {
    auto* llmInference = reinterpret_cast<LLMInference*>(modelPtr);
    return llmInference ? llmInference->getTimeToFirstToken() : 0.0f;
}

extern "C" JNIEXPORT jint JNICALL
Java_io_locian_inference_InferenceEngine_getContextSizeUsed(
    JNIEnv* env, jobject thiz, jlong modelPtr) {
    auto* llmInference = reinterpret_cast<LLMInference*>(modelPtr);
    return llmInference ? llmInference->getContextSizeUsed() : 0;
}

extern "C" JNIEXPORT void JNICALL
Java_io_locian_inference_InferenceEngine_close(
    JNIEnv* env, jobject thiz, jlong modelPtr) {
    auto* llmInference = reinterpret_cast<LLMInference*>(modelPtr);
    delete llmInference;
}

extern "C" JNIEXPORT jboolean JNICALL
Java_io_locian_inference_InferenceEngine_startCompletion(
    JNIEnv* env, jobject thiz, jlong modelPtr, jstring prompt) {
    jboolean isCopy = true;
    const char* promptCstr = env->GetStringUTFChars(prompt, &isCopy);
    auto* llmInference = reinterpret_cast<LLMInference*>(modelPtr);
    if (!llmInference) {
        env->ReleaseStringUTFChars(prompt, promptCstr);
        env->ThrowNew(env->FindClass("java/lang/IllegalStateException"), "Model is not loaded");
        return JNI_FALSE;
    }
    bool usedJinja = false;
    try {
        usedJinja = llmInference->startCompletion(promptCstr);
    } catch (const std::exception& error) {
        env->ReleaseStringUTFChars(prompt, promptCstr);
        env->ThrowNew(env->FindClass("java/lang/IllegalStateException"), error.what());
        return JNI_FALSE;
    }
    env->ReleaseStringUTFChars(prompt, promptCstr);
    return usedJinja ? JNI_TRUE : JNI_FALSE;
}

extern "C" JNIEXPORT jstring JNICALL
Java_io_locian_inference_InferenceEngine_completionLoop(
    JNIEnv* env, jobject thiz, jlong modelPtr) {
    auto* llmInference = reinterpret_cast<LLMInference*>(modelPtr);
    if (!llmInference) {
        env->ThrowNew(env->FindClass("java/lang/IllegalStateException"), "Model is not loaded");
        return nullptr;
    }
    try {
        std::string response = llmInference->completionLoop();
        return env->NewStringUTF(response.c_str());
    } catch (const std::exception& error) {
        env->ThrowNew(env->FindClass("java/lang/IllegalStateException"), error.what());
        return nullptr;
    }
}

extern "C" JNIEXPORT void JNICALL
Java_io_locian_inference_InferenceEngine_stopCompletion(
    JNIEnv* env, jobject thiz, jlong modelPtr) {
    auto* llmInference = reinterpret_cast<LLMInference*>(modelPtr);
    if (llmInference) {
        llmInference->stopCompletion();
    }
}
