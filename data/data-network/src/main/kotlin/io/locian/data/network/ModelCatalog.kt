package io.locian.data.network

import io.locian.core.model.ModelInfo

object ModelCatalog {

    val curatedModels = listOf(

        // ── Ultra-compact  (<500 MB, 2 GB RAM) ─────────────────────────────────

        ModelInfo(
            id = "smollm2-135m",
            name = "SmolLM2 135M Instruct",
            description = "105 MB on-device model by HuggingFace. Fastest cold-start and lowest memory footprint in the catalog. Suited for simple Q&A and autocomplete on heavily constrained devices.",
            repoId = "bartowski/SmolLM2-135M-Instruct-GGUF",
            filename = "SmolLM2-135M-Instruct-Q4_K_M.gguf",
            downloadUrl = "https://huggingface.co/bartowski/SmolLM2-135M-Instruct-GGUF/resolve/main/SmolLM2-135M-Instruct-Q4_K_M.gguf",
            sizeBytes = 105454432L,     // 105 MB
            quantization = "Q4_K_M",
            parameterCount = "135M",
            minRamGb = 2
        ),

        ModelInfo(
            id = "smollm2-360m",
            name = "SmolLM2 360M Instruct",
            description = "230 MB model from HuggingFace. Better instruction-following than the 135M variant with minimal memory overhead. Good entry-level option for 2 GB devices.",
            repoId = "HuggingFaceTB/SmolLM2-360M-Instruct-GGUF",
            filename = "smollm2-360m-instruct-q4_k_m.gguf",
            downloadUrl = "https://huggingface.co/HuggingFaceTB/SmolLM2-360M-Instruct-GGUF/resolve/main/smollm2-360m-instruct-q4_k_m.gguf",
            sizeBytes = 241000000L,     // 230 MB
            quantization = "Q4_K_M",
            parameterCount = "360M",
            minRamGb = 2
        ),

        ModelInfo(
            id = "qwen2.5-0.5b",
            name = "Qwen 2.5 0.5B Instruct",
            description = "380 MB model from Alibaba. Highest benchmark scores among sub-500 MB models. Multilingual support with strong instruction adherence relative to its size.",
            repoId = "Qwen/Qwen2.5-0.5B-Instruct-GGUF",
            filename = "qwen2.5-0.5b-instruct-q4_k_m.gguf",
            downloadUrl = "https://huggingface.co/Qwen/Qwen2.5-0.5B-Instruct-GGUF/resolve/main/qwen2.5-0.5b-instruct-q4_k_m.gguf",
            sizeBytes = 397684736L,     // 380 MB
            quantization = "Q4_K_M",
            parameterCount = "0.5B",
            minRamGb = 2
        ),

        // ── Compact  (1–1.2B, 3 GB RAM) ─────────────────────────────────────────

        ModelInfo(
            id = "llama-3.2-1b",
            name = "Llama 3.2 1B Instruct",
            description = "770 MB model from Meta. Well-balanced instruction model with consistent dialogue quality. Runs on 3 GB devices across reasoning and chat tasks.",
            repoId = "bartowski/Llama-3.2-1B-Instruct-GGUF",
            filename = "Llama-3.2-1B-Instruct-Q4_K_M.gguf",
            downloadUrl = "https://huggingface.co/bartowski/Llama-3.2-1B-Instruct-GGUF/resolve/main/Llama-3.2-1B-Instruct-Q4_K_M.gguf",
            sizeBytes = 808493056L,     // 770 MB
            quantization = "Q4_K_M",
            parameterCount = "1.2B",
            minRamGb = 3
        ),

        // ── Mid-range  (1.5–2B, 4 GB RAM) ───────────────────────────────────────

        ModelInfo(
            id = "smollm2-1.7b",
            name = "SmolLM2 1.7B Instruct",
            description = "1 GB model from HuggingFace, trained on curated synthetic datasets. Competitive reasoning quality for its size with low inference latency on mid-range devices.",
            repoId = "HuggingFaceTB/SmolLM2-1.7B-Instruct-GGUF",
            filename = "smollm2-1.7b-instruct-q4_k_m.gguf",
            downloadUrl = "https://huggingface.co/HuggingFaceTB/SmolLM2-1.7B-Instruct-GGUF/resolve/main/smollm2-1.7b-instruct-q4_k_m.gguf",
            sizeBytes = 1060000000L,    // 1.0 GB
            quantization = "Q4_K_M",
            parameterCount = "1.7B",
            minRamGb = 4
        ),

        ModelInfo(
            id = "qwen3-1.7b",
            name = "Qwen3 1.7B Instruct",
            description = "1.28 GB model from Alibaba's Qwen3 generation. Improved reasoning and instruction-following over Qwen2.5 at the same size class. Good all-round choice for 4 GB devices.",
            repoId = "bartowski/Qwen_Qwen3-1.7B-GGUF",
            filename = "Qwen3-1.7B-Q4_K_M.gguf",
            downloadUrl = "https://huggingface.co/bartowski/Qwen_Qwen3-1.7B-GGUF/resolve/main/Qwen3-1.7B-Q4_K_M.gguf",
            sizeBytes = 1374389248L,    // ~1.28 GB
            quantization = "Q4_K_M",
            parameterCount = "1.7B",
            minRamGb = 4
        ),

        ModelInfo(
            id = "qwen2.5-1.5b",
            name = "Qwen 2.5 1.5B Instruct",
            description = "940 MB multilingual general assistant. Top-scoring 1.5B model across math, coding, and instruction benchmarks. Strong choice for everyday tasks on 4 GB devices.",
            repoId = "Qwen/Qwen2.5-1.5B-Instruct-GGUF",
            filename = "qwen2.5-1.5b-instruct-q4_k_m.gguf",
            downloadUrl = "https://huggingface.co/Qwen/Qwen2.5-1.5B-Instruct-GGUF/resolve/main/qwen2.5-1.5b-instruct-q4_k_m.gguf",
            sizeBytes = 986000000L,     // 940 MB
            quantization = "Q4_K_M",
            parameterCount = "1.5B",
            minRamGb = 4
        ),

        ModelInfo(
            id = "qwen2.5-coder-1.5b",
            name = "Qwen 2.5 Coder 1.5B Instruct",
            description = "986 MB coding-specialist from Alibaba. Trained on code-heavy datasets for Python, Kotlin, shell, and SQL. Best-in-class code quality at the 1.5B scale.",
            repoId = "Qwen/Qwen2.5-Coder-1.5B-Instruct-GGUF",
            filename = "qwen2.5-coder-1.5b-instruct-q4_k_m.gguf",
            downloadUrl = "https://huggingface.co/Qwen/Qwen2.5-Coder-1.5B-Instruct-GGUF/resolve/main/qwen2.5-coder-1.5b-instruct-q4_k_m.gguf",
            sizeBytes = 986000000L,     // 986 MB
            quantization = "Q4_K_M",
            parameterCount = "1.5B",
            minRamGb = 4
        ),

        ModelInfo(
            id = "deepseek-r1-distill-qwen-1.5b",
            name = "DeepSeek R1 Distill 1.5B",
            description = "1.1 GB reasoning model distilled from DeepSeek R1. Uses chain-of-thought (<think>) style inference for math, logic, and step-by-step problem solving on 4 GB devices.",
            repoId = "unsloth/DeepSeek-R1-Distill-Qwen-1.5B-GGUF",
            filename = "DeepSeek-R1-Distill-Qwen-1.5B-Q4_K_M.gguf",
            downloadUrl = "https://huggingface.co/unsloth/DeepSeek-R1-Distill-Qwen-1.5B-GGUF/resolve/main/DeepSeek-R1-Distill-Qwen-1.5B-Q4_K_M.gguf",
            sizeBytes = 1117000000L,    // 1.1 GB
            quantization = "Q4_K_M",
            parameterCount = "1.5B",
            minRamGb = 4
        ),

        ModelInfo(
            id = "gemma-2-2b",
            name = "Gemma 2 2B IT",
            description = "1.7 GB model from Google DeepMind. Uses sliding-window grouped-query attention for high throughput at low memory. Strong instruction adherence and multilingual capability.",
            repoId = "bartowski/gemma-2-2b-it-GGUF",
            filename = "gemma-2-2b-it-Q4_K_M.gguf",
            downloadUrl = "https://huggingface.co/bartowski/gemma-2-2b-it-GGUF/resolve/main/gemma-2-2b-it-Q4_K_M.gguf",
            sizeBytes = 1708582752L,    // 1.7 GB
            quantization = "Q4_K_M",
            parameterCount = "2.6B",
            minRamGb = 4
        ),

        // ── Upper-mid  (3B+, 6 GB RAM) ───────────────────────────────────────────

        ModelInfo(
            id = "qwen2.5-3b",
            name = "Qwen 2.5 3B Instruct",
            description = "2.0 GB general-purpose model. Highest reasoning accuracy in the 3B class with long-context support and multilingual output. Requires 6 GB device RAM.",
            repoId = "Qwen/Qwen2.5-3B-Instruct-GGUF",
            filename = "qwen2.5-3b-instruct-q4_k_m.gguf",
            downloadUrl = "https://huggingface.co/Qwen/Qwen2.5-3B-Instruct-GGUF/resolve/main/qwen2.5-3b-instruct-q4_k_m.gguf",
            sizeBytes = 2010000000L,    // 2.0 GB
            quantization = "Q4_K_M",
            parameterCount = "3.0B",
            minRamGb = 6
        ),

        ModelInfo(
            id = "qwen2.5-coder-3b",
            name = "Qwen 2.5 Coder 3B Instruct",
            description = "2.1 GB coding model. Handles multi-function code generation, refactoring, and debugging with high accuracy. Requires 6 GB device RAM.",
            repoId = "Qwen/Qwen2.5-Coder-3B-Instruct-GGUF",
            filename = "qwen2.5-coder-3b-instruct-q4_k_m.gguf",
            downloadUrl = "https://huggingface.co/Qwen/Qwen2.5-Coder-3B-Instruct-GGUF/resolve/main/qwen2.5-coder-3b-instruct-q4_k_m.gguf",
            sizeBytes = 2104932800L,    // 2.1 GB
            quantization = "Q4_K_M",
            parameterCount = "3.0B",
            minRamGb = 6
        ),

        ModelInfo(
            id = "llama-3.2-3b",
            name = "Llama 3.2 3B Instruct",
            description = "1.9 GB model from Meta. Best dialogue coherence in the Llama 3 mobile lineup. Recommended for high-quality conversational use on 6 GB devices.",
            repoId = "bartowski/Llama-3.2-3B-Instruct-GGUF",
            filename = "Llama-3.2-3B-Instruct-Q4_K_M.gguf",
            downloadUrl = "https://huggingface.co/bartowski/Llama-3.2-3B-Instruct-GGUF/resolve/main/Llama-3.2-3B-Instruct-Q4_K_M.gguf",
            sizeBytes = 2020000000L,    // 1.9 GB
            quantization = "Q4_K_M",
            parameterCount = "3.2B",
            minRamGb = 6
        ),

        ModelInfo(
            id = "phi-4-mini",
            name = "Phi-4 Mini Instruct",
            description = "2.49 GB reasoning model from Microsoft. Architecture optimised for STEM, logical deduction, and structured output. Outperforms larger models on reasoning benchmarks. Requires 6 GB device RAM.",
            repoId = "bartowski/microsoft_Phi-4-mini-instruct-GGUF",
            filename = "Phi-4-mini-instruct-Q4_K_M.gguf",
            downloadUrl = "https://huggingface.co/bartowski/microsoft_Phi-4-mini-instruct-GGUF/resolve/main/Phi-4-mini-instruct-Q4_K_M.gguf",
            sizeBytes = 2672000000L,    // ~2.49 GB
            quantization = "Q4_K_M",
            parameterCount = "3.8B",
            minRamGb = 6
        ),

        ModelInfo(
            id = "gemma-4-e2b",
            name = "Gemma 4 E2B IT",
            description = "3.4 GB model from Google DeepMind. Efficient 2B-equivalent architecture with next-gen attention design. High reasoning density for its size. Requires 6 GB device RAM.",
            repoId = "prithivMLmods/gemma-4-E2B-it-GGUF",
            filename = "gemma-4-E2B-it.Q4_K_M.gguf",
            downloadUrl = "https://huggingface.co/prithivMLmods/gemma-4-E2B-it-GGUF/resolve/main/gemma-4-E2B-it.Q4_K_M.gguf",
            sizeBytes = 3427879136L,    // 3.4 GB
            quantization = "Q4_K_M",
            parameterCount = "E2B",
            minRamGb = 6
        )
    )
}
