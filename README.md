# Locian — On-Device LLM Android Application

[![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg?style=flat-square)](LICENSE)
[![Native License](https://img.shields.io/badge/Native_License-MIT-green.svg?style=flat-square)](LICENSE-MIT)
[![Open Source](https://img.shields.io/badge/Open_Source-OSI_Approved-brightgreen.svg?style=flat-square)](https://opensource.org)
[![Android](https://img.shields.io/badge/Platform-Android_8.0%2B-3DDC84?style=flat-square&logo=android)](app/)
[![CI](https://github.com/cipherAman/Locian/actions/workflows/ci.yml/badge.svg)](https://github.com/cipherAman/Locian/actions/workflows/ci.yml)
[![Documentation](https://img.shields.io/badge/Documentation-User%20Guide-FA010E?style=flat-square&logo=github)](docs/guide.html)

> **✅ True Open Source** — Locian is licensed under **Apache 2.0** (Kotlin) and **MIT**
> (native C++ inference). Both are OSI-approved. Use, fork, modify, and distribute freely —
> including for commercial purposes. See [LICENSE](LICENSE) and [LICENSE-MIT](LICENSE-MIT).

**Locian** is a privacy-first, 100% offline local LLM Android application built with **Jetpack Compose**, **Clean Architecture**, and **llama.cpp** via high-performance Android NDK C++ JNI bindings. It allows running quantized GGUF language models directly on mobile hardware without requiring cloud connectivity or accounts.

> 📖 **Official Documentation**:
> - [**Online Documentation & User Guide**](docs/guide.html) (Setup, Sideloading GGUF, Inference Tuning, FAQs)
> - [**Raw Markdown Guide**](docs/USER_GUIDE.md)
> - [**Interactive Showcase & Simulator**](docs/index.html)

---

## Key Features

- **100% Offline Inference:** Complete privacy. All tokens are computed locally on device via native `llama.cpp`.
- **Dynamic Hardware Acceleration:** Automatic CPU feature detection selecting optimized native binaries for ARM NEON, ARMv8.2 FP16, Dot-Product, and ARMv8.4 i8mm.
- **Adaptive Memory Management:** Automatic RAM profiling adjusting context windows, KV cache, and thread count to eliminate OOMs on 4GB to 12GB+ devices.
- **Streaming Chat UI:** Real-time token streaming with Markdown code block rendering, syntax formatting, and one-tap code copying.
- **Live Telemetry Pill:** Real-time generation statistics showing Decode Speed (tok/s), Time to First Token (TTFT in ms), and total tokens generated.
- **Model Catalog & Resumable Downloads:** Curated library of mobile-optimized GGUF models with HTTP Range resume support and SHA-256 verification.
- **Custom GGUF File Importer:** Ability to load any `.gguf` file stored on internal or SD card storage.
- **Persistent Chat History:** SQLite/Room database with conversation creation, deletion, renaming, and full message persistence.
- **Customizable Inference Settings:** Live adjustment of temperature, Top-P, Min-P, context window size, thread allocation, system prompts, and memory mapping (`mmap`).

---

## Getting Started

### Prerequisites

| Requirement | Version / Notes |
|:---|:---|
| Android Studio | Meerkat (2024.3.1) or newer |
| NDK | **r27.0.12077973** — exact version required |
| CMake | **3.22.1** — matches `externalNativeBuild` |
| JDK | 17 (Temurin/Adoptium recommended) |
| RAM | 16 GB minimum (NDK compilation is memory-intensive) |
| Disk | ~8 GB free space |
| Android device | API 26+ (Android 8.0+) |

### Setup Steps

```bash
# 1. Clone with submodules (REQUIRED — llama.cpp is a submodule)
git clone --recurse-submodules https://github.com/cipherAman/Locian.git

# 2. If you already cloned without submodules:
git submodule update --init --recursive

# 3. Copy the properties template
cp local.properties.example local.properties
#    Then edit local.properties and set sdk.dir to your Android SDK path

# 4. Install NDK r27 via Android Studio:
#    SDK Manager → SDK Tools → NDK (Specific version) → 27.0.12077973

# 5. Build debug APK
./gradlew assembleDebug

# 6. Install on device
adb install app/build/outputs/apk/debug/app-debug.apk
```

> **⚠️ Submodules are mandatory.** `llama.cpp` and `cpu_features` are git submodules
> under `external/`. Without `--recurse-submodules`, the CMake build fails with
> missing source files. This is the most common first-time build error.

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
| **Gemma 2 2B IT** | 2.6B | Q4_K_M (~1.7 GB) | 4 GB | 4096 | Google sliding-window high-accuracy instruction following |
| **Gemma 4 E2B IT** | E2B | Q4_K_M (~3.4 GB) | 6 GB | 8192 | Google DeepMind next-gen dense reasoning architecture |
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

---

## Contributing

Contributions are welcome! See [CONTRIBUTING.md](CONTRIBUTING.md) for the full guide.

- 🐛 [Report a Bug](.github/ISSUE_TEMPLATE/bug_report.yml)
- 💡 [Request a Feature](.github/ISSUE_TEMPLATE/feature_request.yml)
- 📦 [Request a Model](.github/ISSUE_TEMPLATE/model_request.yml)
- 🔀 Submit a Pull Request (branch from `develop`)

---

## Code of Conduct

This project follows the [Contributor Covenant v2.1](CODE_OF_CONDUCT.md). By participating, you agree to uphold this code.

---

## Security

Found a vulnerability? Please use **[GitHub Private Vulnerability Reporting](https://docs.github.com/en/code-security/security-advisories/guidance-on-reporting-and-writing/privately-reporting-a-security-vulnerability)** — do NOT open a public issue. See [SECURITY.md](SECURITY.md) for details.

---

## Changelog

See [CHANGELOG.md](CHANGELOG.md) for the full version history.

---

## Roadmap

See [ROADMAP.md](ROADMAP.md) for planned features and future direction.

---

## Acknowledgements

- [**llama.cpp**](https://github.com/ggerganov/llama.cpp) by Georgi Gerganov — the inference engine powering Locian (MIT License)
- [**google/cpu_features**](https://github.com/google/cpu_features) — hardware capability detection (Apache 2.0)
- The Android, Jetpack Compose, and Kotlin teams at Google

---

## License

Locian uses a dual-license model:

| Component | License |
|:---|:---|
| Kotlin source (`app/`, `core/`, `data/`, `domain/`, `feature/`) | [Apache 2.0](LICENSE) |
| Native C++ inference bridge (`inference/src/main/cpp/`) | [MIT](LICENSE-MIT) |
| llama.cpp (git submodule) | [MIT](external/llama.cpp/LICENSE) |
| google/cpu_features (git submodule) | [Apache 2.0](external/cpu_features/LICENSE) |

See [LICENSE-DEPENDENCIES.md](LICENSE-DEPENDENCIES.md) for all third-party dependency licenses.

> **⚠️ Note for maintainers:** Replace `2026` and `Aman Kumar` placeholders in `LICENSE`, `LICENSE-MIT`, `NOTICE`, and all source file headers before making the repository public.
