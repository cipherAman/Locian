# Changelog

All notable changes to Locian will be documented in this file.

Format: [Keep a Changelog](https://keepachangelog.com/en/1.1.0/)
Versioning: [Semantic Versioning](https://semver.org/)

---

## [Unreleased]

### Planned
- Vulkan GPU compute backend for GPU-accelerated inference
- Multi-modal GGUF support (LLaVA vision architecture)
- Background inference Android Service (keep model hot between sessions)
- Voice input via Whisper.cpp integration

---

## [1.0.0] — YYYY-MM-DD

> Initial open source release.

### Added
- 100% offline on-device LLM inference via llama.cpp NDK JNI bridge
- Dynamic CPU feature dispatcher:
  - ARMv8.4 with Dot-Product + Int8 MatMul (i8mm) — highest performance
  - ARMv8.4 with Dot-Product + FP16
  - ARMv8.2 with Dot-Product
  - ARMv8.2 with FP16
  - ARMv8 baseline
  - Universal fallback + x86_64 emulator support
- Adaptive RAM profiling — auto-adjusts context window, KV cache, and thread count
- Streaming chat UI with real-time token output
- Live telemetry pill (tok/s, TTFT ms, total tokens)
- Curated model catalog: Qwen 2.5, Llama 3.2, SmolLM2, Gemma 2, Gemma 4
- Resumable downloads with HTTP Range + SHA-256 integrity verification
- Custom GGUF file importer (internal storage + SD card)
- Persistent chat history via Room (SQLite)
- Multi-conversation management: create, rename, delete
- Inference settings: temperature, Top-P, Min-P, context size, threads, mmap, system prompt
- Material 3 dark/light theming with dynamic colors
- Markdown code block rendering with syntax highlighting (Markwon)
- One-tap code copying from chat bubbles
- Thinking / reasoning disclosure card for chain-of-thought models
- Infinite reasoning loop prevention (token budget enforcement)

### Architecture
- Multi-module Clean Architecture (12 Gradle modules)
- Hilt dependency injection throughout
- Jetpack Compose UI with state hoisting
- Kotlin Coroutines + Flow for streaming inference
- Apache 2.0 (Kotlin) + MIT (native C++) dual open-source license
