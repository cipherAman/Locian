package io.locian.data.network

import io.locian.core.model.ModelInfo

object ModelCatalog {

    val curatedModels = listOf(
        ModelInfo(
            id = "qwen2.5-0.5b",
            name = "Qwen 2.5 0.5B Instruct",
            description = "Ultra-fast, ultra-lightweight assistant. Best for low-end devices and instant responses.",
            repoId = "Qwen/Qwen2.5-0.5B-Instruct-GGUF",
            filename = "qwen2.5-0.5b-instruct-q4_k_m.gguf",
            downloadUrl = "https://huggingface.co/Qwen/Qwen2.5-0.5B-Instruct-GGUF/resolve/main/qwen2.5-0.5b-instruct-q4_k_m.gguf",
            sizeBytes = 397684736L, // ~380 MB
            quantization = "Q4_K_M",
            parameterCount = "0.5B",
            minRamGb = 2
        ),
        ModelInfo(
            id = "llama-3.2-1b",
            name = "Llama 3.2 1B Instruct",
            description = "Meta's highly capable compact instruction model. Excellent reasoning and dialogue quality.",
            repoId = "bartowski/Llama-3.2-1B-Instruct-GGUF",
            filename = "Llama-3.2-1B-Instruct-Q4_K_M.gguf",
            downloadUrl = "https://huggingface.co/bartowski/Llama-3.2-1B-Instruct-GGUF/resolve/main/Llama-3.2-1B-Instruct-Q4_K_M.gguf",
            sizeBytes = 808493056L, // ~770 MB
            quantization = "Q4_K_M",
            parameterCount = "1.2B",
            minRamGb = 3
        ),
        ModelInfo(
            id = "qwen2.5-1.5b",
            name = "Qwen 2.5 1.5B Instruct",
            description = "Balanced high-performance model for general chat, coding, and factual Q&A.",
            repoId = "Qwen/Qwen2.5-1.5B-Instruct-GGUF",
            filename = "qwen2.5-1.5b-instruct-q4_k_m.gguf",
            downloadUrl = "https://huggingface.co/Qwen/Qwen2.5-1.5B-Instruct-GGUF/resolve/main/qwen2.5-1.5b-instruct-q4_k_m.gguf",
            sizeBytes = 986000000L, // ~940 MB
            quantization = "Q4_K_M",
            parameterCount = "1.5B",
            minRamGb = 4
        ),
        ModelInfo(
            id = "smollm2-1.7b",
            name = "SmolLM2 1.7B Instruct",
            description = "HuggingFace's specialized on-device model trained on high quality synthetic & web data.",
            repoId = "HuggingFaceTB/SmolLM2-1.7B-Instruct-GGUF",
            filename = "smollm2-1.7b-instruct-q4_k_m.gguf",
            downloadUrl = "https://huggingface.co/HuggingFaceTB/SmolLM2-1.7B-Instruct-GGUF/resolve/main/smollm2-1.7b-instruct-q4_k_m.gguf",
            sizeBytes = 1060000000L, // ~1.0 GB
            quantization = "Q4_K_M",
            parameterCount = "1.7B",
            minRamGb = 4
        ),
        ModelInfo(
            id = "llama-3.2-3b",
            name = "Llama 3.2 3B Instruct",
            description = "Flagship quality on-device intelligence. Outstanding complex reasoning, summary, and coding.",
            repoId = "bartowski/Llama-3.2-3B-Instruct-GGUF",
            filename = "Llama-3.2-3B-Instruct-Q4_K_M.gguf",
            downloadUrl = "https://huggingface.co/bartowski/Llama-3.2-3B-Instruct-GGUF/resolve/main/Llama-3.2-3B-Instruct-Q4_K_M.gguf",
            sizeBytes = 2020000000L, // ~1.9 GB
            quantization = "Q4_K_M",
            parameterCount = "3.2B",
            minRamGb = 6
        )
    )
}
