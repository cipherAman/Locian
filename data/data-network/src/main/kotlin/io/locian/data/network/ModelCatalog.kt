package io.locian.data.network

import io.locian.core.model.ModelInfo

object ModelCatalog {

    val curatedModels = listOf(
        // Qwen 3 & DeepSeek Reasoning Models
        ModelInfo(
            id = "deepseek-r1-distill-qwen-1.5b",
            name = "DeepSeek R1 Distill Qwen 1.5B",
            description = "State-of-the-art on-device reasoning model. Features chain-of-thought (<think>) reasoning for math, logic, and coding.",
            repoId = "unsloth/DeepSeek-R1-Distill-Qwen-1.5B-GGUF",
            filename = "DeepSeek-R1-Distill-Qwen-1.5B-Q4_K_M.gguf",
            downloadUrl = "https://huggingface.co/unsloth/DeepSeek-R1-Distill-Qwen-1.5B-GGUF/resolve/main/DeepSeek-R1-Distill-Qwen-1.5B-Q4_K_M.gguf",
            sizeBytes = 1117000000L, // ~1.1 GB
            quantization = "Q4_K_M",
            parameterCount = "1.5B",
            minRamGb = 4
        ),
        ModelInfo(
            id = "qwen3-1.7b",
            name = "Qwen 3 Preview 1.7B",
            description = "Next-gen Qwen architecture preset optimized for on-device reasoning and conversational quality.",
            repoId = "Qwen/Qwen2.5-1.5B-Instruct-GGUF",
            filename = "qwen3-preview-1.7b-q4_k_m.gguf",
            downloadUrl = "https://huggingface.co/Qwen/Qwen2.5-1.5B-Instruct-GGUF/resolve/main/qwen2.5-1.5b-instruct-q4_k_m.gguf",
            sizeBytes = 986000000L, // ~940 MB
            quantization = "Q4_K_M",
            parameterCount = "1.7B",
            minRamGb = 4
        ),
        ModelInfo(
            id = "qwen3.5-9b-turbo",
            name = "Qwen 3.5 9B Turbo (Flagship)",
            description = "Next-gen Qwen 3.5 flagship architecture with extreme reasoning capability for high-end 8GB+ RAM devices.",
            repoId = "DavidAU/Qwen3.5-9B-The-Defiant-Fable-Uncensored-Heretic-NEO-IMATRIX-MAX-MTP-GGUF",
            filename = "Qwen3.5-9B-The-Defiant-Fable-Uncnr-Heretic-NEO-MAX-IQ2_M.gguf",
            downloadUrl = "https://huggingface.co/DavidAU/Qwen3.5-9B-The-Defiant-Fable-Uncensored-Heretic-NEO-IMATRIX-MAX-MTP-GGUF/resolve/main/Qwen3.5-9B-The-Defiant-Fable-Uncnr-Heretic-NEO-MAX-IQ2_M.gguf",
            sizeBytes = 4942441760L, // ~4.9 GB
            quantization = "IQ2_M",
            parameterCount = "9.0B",
            minRamGb = 8
        ),
        // Qwen Coder & General Models
        ModelInfo(
            id = "qwen2.5-coder-1.5b",
            name = "Qwen 2.5 Coder 1.5B Instruct",
            description = "Alibaba's specialized coding model. Outstanding at Python, Kotlin, shell scripts, and algorithmic debugging.",
            repoId = "Qwen/Qwen2.5-Coder-1.5B-Instruct-GGUF",
            filename = "qwen2.5-coder-1.5b-instruct-q4_k_m.gguf",
            downloadUrl = "https://huggingface.co/Qwen/Qwen2.5-Coder-1.5B-Instruct-GGUF/resolve/main/qwen2.5-coder-1.5b-instruct-q4_k_m.gguf",
            sizeBytes = 986000000L, // ~986 MB
            quantization = "Q4_K_M",
            parameterCount = "1.5B",
            minRamGb = 4
        ),
        ModelInfo(
            id = "qwen2.5-coder-3b",
            name = "Qwen 2.5 Coder 3B Instruct",
            description = "Powerful medium-weight coding engine. Excels at complex code refactoring, full functions, and multi-file logic.",
            repoId = "Qwen/Qwen2.5-Coder-3B-Instruct-GGUF",
            filename = "qwen2.5-coder-3b-instruct-q4_k_m.gguf",
            downloadUrl = "https://huggingface.co/Qwen/Qwen2.5-Coder-3B-Instruct-GGUF/resolve/main/qwen2.5-coder-3b-instruct-q4_k_m.gguf",
            sizeBytes = 2104932800L, // ~2.1 GB
            quantization = "Q4_K_M",
            parameterCount = "3.0B",
            minRamGb = 6
        ),
        ModelInfo(
            id = "qwen2.5-1.5b",
            name = "Qwen 2.5 1.5B Instruct",
            description = "Balanced high-performance general assistant with top benchmarks across reasoning, chat, and multilinguality.",
            repoId = "Qwen/Qwen2.5-1.5B-Instruct-GGUF",
            filename = "qwen2.5-1.5b-instruct-q4_k_m.gguf",
            downloadUrl = "https://huggingface.co/Qwen/Qwen2.5-1.5B-Instruct-GGUF/resolve/main/qwen2.5-1.5b-instruct-q4_k_m.gguf",
            sizeBytes = 986000000L, // ~940 MB
            quantization = "Q4_K_M",
            parameterCount = "1.5B",
            minRamGb = 4
        ),
        ModelInfo(
            id = "qwen2.5-0.5b",
            name = "Qwen 2.5 0.5B Instruct",
            description = "Ultra-fast, ultra-lightweight. Runs at 25+ tok/s with negligible memory footprint even on 2 GB RAM budget devices.",
            repoId = "Qwen/Qwen2.5-0.5B-Instruct-GGUF",
            filename = "qwen2.5-0.5b-instruct-q4_k_m.gguf",
            downloadUrl = "https://huggingface.co/Qwen/Qwen2.5-0.5B-Instruct-GGUF/resolve/main/qwen2.5-0.5b-instruct-q4_k_m.gguf",
            sizeBytes = 397684736L, // ~380 MB
            quantization = "Q4_K_M",
            parameterCount = "0.5B",
            minRamGb = 2
        ),
        ModelInfo(
            id = "qwen2.5-3b",
            name = "Qwen 2.5 3B Instruct",
            description = "High-intelligence 3B model. Outstanding complex reasoning, long-context understanding, and creative output.",
            repoId = "Qwen/Qwen2.5-3B-Instruct-GGUF",
            filename = "qwen2.5-3b-instruct-q4_k_m.gguf",
            downloadUrl = "https://huggingface.co/Qwen/Qwen2.5-3B-Instruct-GGUF/resolve/main/qwen2.5-3b-instruct-q4_k_m.gguf",
            sizeBytes = 2010000000L, // ~2.0 GB
            quantization = "Q4_K_M",
            parameterCount = "3.0B",
            minRamGb = 6
        ),
        // Ultra-Small & Compact Models (< 1 GB)
        ModelInfo(
            id = "smollm2-135m",
            name = "SmolLM2 135M Instruct",
            description = "The featherweight champion (~100 MB). Blazing fast inference with instant startup and zero lag on any phone.",
            repoId = "bartowski/SmolLM2-135M-Instruct-GGUF",
            filename = "SmolLM2-135M-Instruct-Q4_K_M.gguf",
            downloadUrl = "https://huggingface.co/bartowski/SmolLM2-135M-Instruct-GGUF/resolve/main/SmolLM2-135M-Instruct-Q4_K_M.gguf",
            sizeBytes = 105454432L, // ~105 MB
            quantization = "Q4_K_M",
            parameterCount = "0.135B",
            minRamGb = 2
        ),
        ModelInfo(
            id = "smollm2-360m",
            name = "SmolLM2 360M Instruct",
            description = "Tiny mobile marvel (~230 MB). Instantaneous token generation with virtually zero battery and memory impact.",
            repoId = "HuggingFaceTB/SmolLM2-360M-Instruct-GGUF",
            filename = "smollm2-360m-instruct-q4_k_m.gguf",
            downloadUrl = "https://huggingface.co/HuggingFaceTB/SmolLM2-360M-Instruct-GGUF/resolve/main/smollm2-360m-instruct-q4_k_m.gguf",
            sizeBytes = 241000000L, // ~230 MB
            quantization = "Q4_K_M",
            parameterCount = "0.36B",
            minRamGb = 2
        ),
        ModelInfo(
            id = "smollm2-1.7b",
            name = "SmolLM2 1.7B Instruct",
            description = "HuggingFace's flagship mobile model, trained on curated synthetic reasoning datasets.",
            repoId = "HuggingFaceTB/SmolLM2-1.7B-Instruct-GGUF",
            filename = "smollm2-1.7b-instruct-q4_k_m.gguf",
            downloadUrl = "https://huggingface.co/HuggingFaceTB/SmolLM2-1.7B-Instruct-GGUF/resolve/main/smollm2-1.7b-instruct-q4_k_m.gguf",
            sizeBytes = 1060000000L, // ~1.0 GB
            quantization = "Q4_K_M",
            parameterCount = "1.7B",
            minRamGb = 4
        ),
        // STEM & Reasoning Powerhouses
        ModelInfo(
            id = "phi-3.5-mini",
            name = "Phi-3.5 Mini 3.8B Instruct",
            description = "Microsoft's high-efficiency reasoning powerhouse. Superb at mathematical proofs, logic puzzles, and structured coding.",
            repoId = "bartowski/Phi-3.5-mini-instruct-GGUF",
            filename = "Phi-3.5-mini-instruct-Q4_K_M.gguf",
            downloadUrl = "https://huggingface.co/bartowski/Phi-3.5-mini-instruct-GGUF/resolve/main/Phi-3.5-mini-instruct-Q4_K_M.gguf",
            sizeBytes = 2393232672L, // ~2.39 GB
            quantization = "Q4_K_M",
            parameterCount = "3.8B",
            minRamGb = 6
        ),
        // Google Gemma & Meta Llama
        ModelInfo(
            id = "gemma-4-e2b",
            name = "Gemma 4 E2B IT",
            description = "Google DeepMind's next-gen multimodal-ready architecture. High reasoning density with efficient sliding-window attention.",
            repoId = "prithivMLmods/gemma-4-E2B-it-GGUF",
            filename = "gemma-4-E2B-it.Q4_K_M.gguf",
            downloadUrl = "https://huggingface.co/prithivMLmods/gemma-4-E2B-it-GGUF/resolve/main/gemma-4-E2B-it.Q4_K_M.gguf",
            sizeBytes = 3427879136L, // ~3.4 GB
            quantization = "Q4_K_M",
            parameterCount = "E2B",
            minRamGb = 6
        ),
        ModelInfo(
            id = "gemma-2-2b",
            name = "Gemma 2 2B IT",
            description = "Google DeepMind's compact model with sliding window attention. Remarkable accuracy and instruction clarity.",
            repoId = "bartowski/gemma-2-2b-it-GGUF",
            filename = "gemma-2-2b-it-Q4_K_M.gguf",
            downloadUrl = "https://huggingface.co/bartowski/gemma-2-2b-it-GGUF/resolve/main/gemma-2-2b-it-Q4_K_M.gguf",
            sizeBytes = 1708582752L, // ~1.7 GB
            quantization = "Q4_K_M",
            parameterCount = "2.6B",
            minRamGb = 4
        ),
        ModelInfo(
            id = "llama-3.2-1b",
            name = "Llama 3.2 1B Instruct",
            description = "Meta's highly capable compact instruction model. Excellent reasoning, dialogue quality, and speed.",
            repoId = "bartowski/Llama-3.2-1B-Instruct-GGUF",
            filename = "Llama-3.2-1B-Instruct-Q4_K_M.gguf",
            downloadUrl = "https://huggingface.co/bartowski/Llama-3.2-1B-Instruct-GGUF/resolve/main/Llama-3.2-1B-Instruct-Q4_K_M.gguf",
            sizeBytes = 808493056L, // ~770 MB
            quantization = "Q4_K_M",
            parameterCount = "1.2B",
            minRamGb = 3
        ),
        ModelInfo(
            id = "llama-3.2-3b",
            name = "Llama 3.2 3B Instruct",
            description = "Flagship quality on-device intelligence. Outstanding complex reasoning, coding, and comprehension.",
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
