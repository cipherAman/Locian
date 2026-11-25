# Locian — On-Device LLM Android Application

[![Docs](https://img.shields.io/badge/Docs-GitHub%20Pages-FA010E?style=flat-square&logo=github)](docs/index.html)
[![Android](https://img.shields.io/badge/Platform-Android%208.0%2B-3DDC84?style=flat-square&logo=android)](app/)
[![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg?style=flat-square)](LICENSE)

**Locian** is a privacy-first, 100% offline local LLM Android application built with **Jetpack Compose**, **Clean Architecture**, and **llama.cpp** via high-performance Android NDK C++ JNI bindings. It allows running quantized GGUF language models directly on mobile hardware without requiring cloud connectivity or accounts.

> 🌐 **Interactive Documentation & Live Showcase**: Check out our [GitHub Pages Documentation](docs/index.html) for an interactive streaming simulator, curated model matrix, architecture diagrams, and quickstart guide!

---

## Key Features

- **100% Offline Inference:** Complete privacy. All tokens are computed locally on device via native `llama.cpp`.
- **Dynamic Hardware Acceleration:** Automatic CPU feature detection selecting optimized native binaries for ARM NEON, ARMv8.2 FP16, Dot-Product, and ARMv8.4 i8mm.
- **Adaptive Memory Management:** Automatic RAM profiling adjusting context windows, KV cache, and thread count to eliminate OOMs on 4GB to 12GB+ devices.
- **Streaming Chat UI:** Real-time token streaming with Markdown code block rendering, syntax formatting, and one-tap code copying.
- **Live Telemetry Pill:** Real-time generation statistics showing Decode Speed (tok/s), Time to First Token (TTFT in ms), and total tokens generated.
- **Model Catalog & Resumable Downloads:** Curated library of mobile-optimized GGUF models (Qwen 2.5, Llama 3.2, SmolLM2) with HTTP Range resume support and SHA-256 verification.
- **Custom GGUF File Importer:** Ability to load any `.gguf` file stored on internal or SD card storage.
- **Persistent Chat History:** SQLite/Room database with conversation creation, deletion, renaming, and full message persistence.
- **Customizable Inference Settings:** Live adjustment of temperature, Top-P, Min-P, context window size, thread allocation, system prompts, and memory mapping (`mmap`).

---

## Architecture Overview

Locian uses a multi-module Clean Architecture pattern with separation of concerns:

```
Locian/
├── app/                          # Application shell, navigation graph, Hilt setup
├── core/
│   ├── core-model/               # Domain data entities (ChatMessage, ModelInfo, etc.)
│   ├── core-common/              # AppResult, formatters, coroutine dispatchers
│   └── core-ui/                  # Material 3 Theme, Typography, TelemetryPill, MarkdownBubble
├── inference/                    # Native C++ JNI bridge, llama.cpp, CPU feature dispatcher
├── data/
│   ├── data-database/            # Room database, Conversation & Message DAOs
│   ├── data-datastore/           # Jetpack DataStore preferences (sampling, active model)
│   ├── data-network/             # OkHttp streaming downloader with HTTP Range resume
│   └── data-repository/          # Repository implementations (Inference, Chat, Model)
├── domain/
│   └── domain-inference/         # Use cases (StreamChatResponse, LoadModel, ManageConversation)
└── feature/
    ├── feature-chat/             # Chat UI, streaming message bubble, input bar
    ├── feature-models/           # Curated catalog, download manager, custom file loader
    ├── feature-settings/         # Inference parameters, system prompt, theme selection
    └── feature-conversations/    # Conversation drawer, rename, delete
```

---

## Curated Model Matrix

| Model | Parameter Size | Quantization | Min RAM | Default Context | Best For |
|:---|:---:|:---:|:---:|:---:|:---|
| **Qwen 2.5 0.5B** | 0.5B | Q4_K_M (~380 MB) | 2 GB | 2048 | Ultra-fast responses on low-end hardware |
| **Llama 3.2 1B** | 1.2B | Q4_K_M (~770 MB) | 3 GB | 2048 | Balanced instruction following & chat |
| **Qwen 2.5 1.5B** | 1.5B | Q4_K_M (~940 MB) | 4 GB | 4096 | General chat, coding, reasoning |
| **SmolLM2 1.7B** | 1.7B | Q4_K_M (~1.0 GB) | 4 GB | 4096 | High-quality compact synthetic reasoning |
| **Llama 3.2 3B** | 3.2B | Q4_K_M (~1.9 GB) | 6 GB | 8192 | Flagship reasoning and creative tasks |

---

## Native Architecture & Build Artifacts

The native layer cross-compiles with Android NDK r27+ using CMake 3.22.1 and produces multi-target binaries packaged into the APK:

- `liblocian_cpu_features.so`: Hardware capability detection at startup
- `liblocian_inference_v8_4_fp16_dotprod_i8mm.so`: ARMv8.4+ with Dot-Product & Int8 MatMul
- `liblocian_inference_v8_4_fp16_dotprod.so`: ARMv8.4+ with Dot-Product & FP16
- `liblocian_inference_v8_2_fp16_dotprod.so`: ARMv8.2+ with Dot-Product
- `liblocian_inference_v8_2_fp16.so`: ARMv8.2+ with FP16
- `liblocian_inference_v8.so`: ARMv8 baseline
- `liblocian_inference.so`: Universal fallback & x86_64 emulator support

### Building the Project

```bash
# Clean and assemble debug APK
./gradlew assembleDebug

# Output APK path
app/build/outputs/apk/debug/app-debug.apk
```

---

## License

Locian is licensed under the Apache License 2.0. Native inference is powered by `llama.cpp` (MIT License) and `google/cpu_features` (Apache 2.0).
