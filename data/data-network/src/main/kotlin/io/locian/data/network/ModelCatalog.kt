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
            repoId = "bartowski/SmolLM2-360M-Instruct-GGUF",
            filename = "SmolLM2-360M-Instruct-Q4_K_M.gguf",
            downloadUrl = "https://huggingface.co/bartowski/SmolLM2-360M-Instruct-GGUF/resolve/main/SmolLM2-360M-Instruct-Q4_K_M.gguf",
            sizeBytes = 241000000L,     // 230 MB
            quantization = "Q4_K_M",
            parameterCount = "360M",
            minRamGb = 2
        ),

        ModelInfo(
            id = "qwen3.5-0.8b",
            name = "Qwen3.5 0.8B Instruct",
            description = "507 MB model from Alibaba's 2026 Qwen3.5 generation. Next-gen hybrid architecture with state-of-the-art efficiency, low memory footprint, and enhanced multilingual reasoning for 2 GB devices.",
            repoId = "unsloth/Qwen3.5-0.8B-GGUF",
            filename = "Qwen3.5-0.8B-Q4_K_M.gguf",
            downloadUrl = "https://huggingface.co/unsloth/Qwen3.5-0.8B-GGUF/resolve/main/Qwen3.5-0.8B-Q4_K_M.gguf",
            sizeBytes = 532517120L,     // 507 MB
            quantization = "Q4_K_M",
            parameterCount = "0.8B",
            minRamGb = 2
        ),

        ModelInfo(
            id = "qwen3-0.6b",
            name = "Qwen3 0.6B Instruct",
            description = "484 MB model from Alibaba's Qwen3 generation. Supports hybrid thinking/non-thinking mode. Best reasoning quality in the sub-500 MB class with multilingual capability.",
            repoId = "bartowski/Qwen_Qwen3-0.6B-GGUF",
            filename = "Qwen_Qwen3-0.6B-Q4_K_M.gguf",
            downloadUrl = "https://huggingface.co/bartowski/Qwen_Qwen3-0.6B-GGUF/resolve/main/Qwen_Qwen3-0.6B-Q4_K_M.gguf",
            sizeBytes = 507510784L,     // 484 MB
            quantization = "Q4_K_M",
            parameterCount = "0.6B",
            minRamGb = 2
        ),

        ModelInfo(
            id = "qwen2.5-0.5b",
            name = "Qwen 2.5 0.5B Instruct",
            description = "380 MB model from Alibaba. Strong benchmark scores for its size. Multilingual support with good instruction adherence. Fallback choice for 2 GB devices.",
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
            id = "gemma-3-1b",
            name = "Gemma 3 1B IT",
            description = "768 MB instruction-tuned model from Google DeepMind's 2026 Gemma 3 family. Compact, highly capable on-device model with industry-leading safety and instruction adherence.",
            repoId = "ggml-org/gemma-3-1b-it-GGUF",
            filename = "gemma-3-1b-it-Q4_K_M.gguf",
            downloadUrl = "https://huggingface.co/ggml-org/gemma-3-1b-it-GGUF/resolve/main/gemma-3-1b-it-Q4_K_M.gguf",
            sizeBytes = 806058240L,     // 768 MB
            quantization = "Q4_K_M",
            parameterCount = "1.0B",
            minRamGb = 3
        ),

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
        // High-efficiency mobile architectures & latest 2026 releases

        ModelInfo(
            id = "qwen3.5-2b",
            name = "Qwen3.5 2B Instruct",
            description = "1.19 GB model from Alibaba's 2026 Qwen3.5 generation. Powerful reasoning, logic, and coding capabilities in a lite mobile package. Excellent performance-per-watt on 4 GB devices.",
            repoId = "unsloth/Qwen3.5-2B-GGUF",
            filename = "Qwen3.5-2B-Q4_K_M.gguf",
            downloadUrl = "https://huggingface.co/unsloth/Qwen3.5-2B-GGUF/resolve/main/Qwen3.5-2B-Q4_K_M.gguf",
            sizeBytes = 1280835840L,    // 1.19 GB
            quantization = "Q4_K_M",
            parameterCount = "2.0B",
            minRamGb = 4
        ),

        ModelInfo(
            id = "lfm3-1.7b",
            name = "LFM3 1.7B Instruct",
            description = "1.02 GB Liquid Foundation Model from Liquid AI. Combines neural state-space and transformer dynamics for ultra-low latency and minimal memory bandwidth on mobile devices.",
            repoId = "LiquidAI/LFM3-1.7B-GGUF",
            filename = "LFM3-1.7B-Q4_K_M.gguf",
            downloadUrl = "https://huggingface.co/LiquidAI/LFM3-1.7B-GGUF/resolve/main/LFM3-1.7B-Q4_K_M.gguf",
            sizeBytes = 1099627808L,    // ~1.02 GB
            quantization = "Q4_K_M",
            parameterCount = "1.7B",
            minRamGb = 4
        ),

        ModelInfo(
            id = "bonsai-8b",
            name = "Bonsai 8B (1-Bit)",
            description = "1.08 GB extreme-efficiency model by Prism ML. Uses 1-bit / ternary weight compression to deliver 8B-scale reasoning and knowledge on standard 4 GB mobile hardware.",
            repoId = "prism-ml/Bonsai-8B-gguf",
            filename = "Bonsai-8B.gguf",
            downloadUrl = "https://huggingface.co/prism-ml/Bonsai-8B-gguf/resolve/main/Bonsai-8B.gguf",
            sizeBytes = 1158654496L,    // 1.08 GB
            quantization = "1-Bit Ternary",
            parameterCount = "8.0B",
            minRamGb = 4
        ),

        ModelInfo(
            id = "bonsai-2-4b",
            name = "Bonsai 2 Ternary 4B",
            description = "1.13 GB ternary-quantized model from Prism ML based on Qwen 3.5. Combines hybrid attention with extreme weight efficiency for mobile edge devices.",
            repoId = "prism-ml/Ternary-Bonsai-2-4B-gguf",
            filename = "Ternary-Bonsai-2-4B-Q2_0.gguf",
            downloadUrl = "https://huggingface.co/prism-ml/Ternary-Bonsai-2-4B-gguf/resolve/main/Ternary-Bonsai-2-4B-Q2_0.gguf",
            sizeBytes = 1212103520L,    // 1.13 GB
            quantization = "Q2_0 Ternary",
            parameterCount = "4.0B",
            minRamGb = 4
        ),

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
            filename = "Qwen_Qwen3-1.7B-Q4_K_M.gguf",
            downloadUrl = "https://huggingface.co/bartowski/Qwen_Qwen3-1.7B-GGUF/resolve/main/Qwen_Qwen3-1.7B-Q4_K_M.gguf",
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
        // Latest 2025/2026 releases shown first

        ModelInfo(
            id = "gemma-3-4b",
            name = "Gemma 3 4B IT",
            description = "2.32 GB instruction-tuned model from Google DeepMind's 2026 Gemma 3 generation. Delivers near-7B benchmark performance with high reasoning density on 6 GB devices.",
            repoId = "ggml-org/gemma-3-4b-it-GGUF",
            filename = "gemma-3-4b-it-Q4_K_M.gguf",
            downloadUrl = "https://huggingface.co/ggml-org/gemma-3-4b-it-GGUF/resolve/main/gemma-3-4b-it-Q4_K_M.gguf",
            sizeBytes = 2489757856L,    // 2.32 GB
            quantization = "Q4_K_M",
            parameterCount = "4.0B",
            minRamGb = 6
        ),

        ModelInfo(
            id = "smollm3-3b",
            name = "SmolLM3 3B Instruct",
            description = "1.92 GB model by HuggingFace — the latest generation of the SmolLM series. Trained with long-context alignment and improved reasoning. Best-in-class quality for a 3B model at this file size.",
            repoId = "bartowski/HuggingFaceTB_SmolLM3-3B-GGUF",
            filename = "HuggingFaceTB_SmolLM3-3B-Q4_K_M.gguf",
            downloadUrl = "https://huggingface.co/bartowski/HuggingFaceTB_SmolLM3-3B-GGUF/resolve/main/HuggingFaceTB_SmolLM3-3B-Q4_K_M.gguf",
            sizeBytes = 2061500416L,    // 1.92 GB
            quantization = "Q4_K_M",
            parameterCount = "3.0B",
            minRamGb = 6
        ),

        ModelInfo(
            id = "qwen3-4b",
            name = "Qwen3 4B Instruct",
            description = "2.50 GB model from Alibaba's Qwen3 generation. Hybrid thinking/non-thinking mode. Top reasoning accuracy in the 4B class, outperforming many 7B models on benchmarks. Requires 6 GB device RAM.",
            repoId = "bartowski/Qwen_Qwen3-4B-GGUF",
            filename = "Qwen_Qwen3-4B-Q4_K_M.gguf",
            downloadUrl = "https://huggingface.co/bartowski/Qwen_Qwen3-4B-GGUF/resolve/main/Qwen_Qwen3-4B-Q4_K_M.gguf",
            sizeBytes = 2684354560L,    // 2.50 GB
            quantization = "Q4_K_M",
            parameterCount = "4.0B",
            minRamGb = 6
        ),

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
            filename = "microsoft_Phi-4-mini-instruct-Q4_K_M.gguf",
            downloadUrl = "https://huggingface.co/bartowski/microsoft_Phi-4-mini-instruct-GGUF/resolve/main/microsoft_Phi-4-mini-instruct-Q4_K_M.gguf",
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
