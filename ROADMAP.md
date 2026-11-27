# Roadmap

> **Vision:** Bring private, powerful AI to any Android device without cloud dependency.

---

## Current Status — v1.0.0

Locian v1.0.0 is the initial open-source release with stable offline LLM inference on ARM64 Android devices.

---

## Near-term — v1.1.x

- **Vulkan compute backend** — GPU-accelerated inference on Adreno/Mali chipsets
- **Per-conversation system prompt override** — different persona per chat
- **Export chat history** — Markdown and JSON export via Android Share Sheet
- **Share individual messages** — via Android Share Sheet
- **F-Droid release** — build reproducibility and submission to F-Droid catalog

---

## Medium-term — v1.2.x

- **Multi-modal GGUF support** — LLaVA vision architecture (image + text prompts)
- **Background Android Service** — keep model hot between sessions; faster TTFT
- **Shortcuts / widgets** — quick prompts from Android home screen
- **Model benchmarking screen** — tok/s and TTFT comparison across loaded models

---

## Long-term / Research

- **Voice input** — Whisper.cpp on-device speech-to-text
- **Text-to-speech output** — local TTS with Android TTS API or Kokoro
- **RAG with local vector store** — on-device embedding + similarity search (no cloud)
- **Android Wear OS companion** — quick prompts from wrist

---

## Never Planned

- Cloud sync or server-side inference
- Telemetry, analytics, or crash reporting that sends data off device
- Paid features or in-app purchases
- Mandatory accounts or registration
- DRM or platform lock-in of any kind

---

## How to Influence This Roadmap

1. Open a [GitHub Discussion](../../discussions) or [feature_request](.github/ISSUE_TEMPLATE/feature_request.yml)
2. Up-vote existing issues with 👍
3. Submit a PR — merged features move to the roadmap
