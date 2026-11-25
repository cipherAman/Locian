# Locian — User Guide & Technical Documentation

**Locian** is a privacy-first, 100% offline local LLM Android application built with Jetpack Compose, Clean Architecture, and `llama.cpp` via native Android NDK C++ JNI bindings.

This guide provides instructions on hardware requirements, installation, model management, sideloading custom GGUF models, parameter tuning, and troubleshooting.

---

## Table of Contents

1. [Hardware & System Requirements](#1-hardware--system-requirements)
2. [Installation](#2-installation)
3. [Quickstart Workflow](#3-quickstart-workflow)
4. [In-App Model Catalog](#4-in-app-model-catalog)
5. [Sideloading Custom GGUF Models](#5-sideloading-custom-gguf-models)
6. [Quantization Guidelines](#6-quantization-guidelines)
7. [Chat Interface & Features](#7-chat-interface--features)
8. [Live Telemetry Metrics](#8-live-telemetry-metrics)
9. [Managing Conversations](#9-managing-conversations)
10. [Inference Parameter Tuning Reference](#10-inference-parameter-tuning-reference)
11. [Thermal & Battery Optimization](#11-thermal--battery-optimization)
12. [Troubleshooting & FAQ](#12-troubleshooting--faq)

---

## 1. Hardware & System Requirements

Locian executes quantized GGUF weights directly in memory and computes tokens using device CPU cores. No external servers or internet connections are used during inference.

| Component | Minimum Specification | Recommended Specification |
|:---|:---|:---|
| **Operating System** | Android 8.0 (API level 26) | Android 13.0+ (API level 33+) |
| **CPU Architecture** | ARM64-v8a (64-bit ARM) | ARMv8.2-A / ARMv8.4-A (DotProd, FP16, i8mm) |
| **System Memory (RAM)** | 4 GB RAM (0.5B – 1B models) | 8 GB – 12 GB RAM (1.5B – 3B models) |
| **Available Storage** | 1.5 GB free internal flash | 5 GB+ free UFS 3.1 / 4.0 storage |

> **Memory Notice:** Android's Low-Memory Killer (LMK) enforces process limits. On a 4 GB RAM device, running models larger than 1.5B parameters or context windows greater than 2048 tokens may trigger an OS process termination.

---

## 2. Installation

### Method A: Install via ADB (Recommended for Developers)

Enable USB Debugging on your phone (**Settings → Developer Options → USB Debugging**) and run:

```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

### Method B: Build from Source

Requirements: Android Studio Ladybug+, JDK 17/21, Android NDK r27+, CMake 3.22.1.

```bash
git clone https://github.com/Kiphra/locian.git
cd locian
./gradlew assembleDebug
```

---

## 3. Quickstart Workflow

1. **Launch App:** On first boot, Locian detects CPU capabilities (`ARM NEON`, `FP16`, `DotProd`, `i8mm`) and selects the optimized native library.
2. **Download or Import a Model:** Open the **Models** tab and download `Qwen 2.5 0.5B` for a fast initial test.
3. **Select Model:** Tap the downloaded model card to set it as active.
4. **Chat:** Return to the **Chat** screen, type your prompt, and watch real-time token streaming with live telemetry metrics.

---

## 4. In-App Model Catalog

The built-in catalog provides tested, mobile-optimized models hosted on Hugging Face:

| Model | Parameters | Quantization | Size | Min RAM | Recommended Use |
|:---|:---:|:---:|:---:|:---:|:---|
| **Qwen 2.5 0.5B** | 0.49B | Q4_K_M | ~380 MB | 2 GB | Ultra-fast responses (35–50 tok/s), low-spec devices |
| **Llama 3.2 1B** | 1.23B | Q4_K_M | ~770 MB | 3 GB | General dialogue, summarization, instructions |
| **Qwen 2.5 1.5B** | 1.54B | Q4_K_M | ~940 MB | 4 GB | Coding, technical assistance, multilingual reasoning |
| **SmolLM2 1.7B** | 1.71B | Q4_K_M | ~1.02 GB | 4 GB | Compact synthetic reasoning benchmark model |
| **Gemma 2 2B IT** | 2.6B | Q4_K_M | ~1.71 GB | 4 GB | Google DeepMind sliding-window instruction clarity |
| **Gemma 4 E2B IT** | E2B | Q4_K_M | ~3.42 GB | 6 GB | Google DeepMind next-generation dense reasoning |
| **Llama 3.2 3B** | 3.21B | Q4_K_M | ~1.95 GB | 6 GB | Complex logical reasoning, structured generation |

**Catalog Features:**
- **HTTP Range Resume:** Resumes interrupted downloads automatically.
- **SHA-256 Checksum:** Verifies file integrity before registration.

---

## 5. Sideloading Custom GGUF Models

Locian allows importing any compatible GGUF file from your device storage:

1. Download a GGUF model file on your computer (e.g., from Hugging Face).
2. Push the file to your device's Downloads directory:
   ```bash
   adb push DeepSeek-R1-Distill-Qwen-1.5B-Q4_K_M.gguf /sdcard/Download/
   ```
3. In Locian, open the **Models** screen and tap **Import Custom Model**.
4. Select the `.gguf` file using the system file picker.
5. Tap the imported model to activate it.

---

## 6. Quantization Guidelines

| Quantization | Bit Depth | Perplexity Loss | Mobile Performance |
|:---|:---:|:---:|:---|
| `Q4_K_M` | 4.5 bpw | Minimal | **Recommended sweet spot.** Optimal speed, memory, and quality. |
| `Q4_0` | 4.0 bpw | Low | Fastest throughput on older ARM cores. |
| `Q5_K_M` | 5.5 bpw | Negligible | Higher quality; requires 6 GB+ RAM. |
| `Q8_0` | 8.0 bpw | Near zero | High memory footprint; causes rapid thermal throttling on mobile. |

---

## 7. Chat Interface & Features

- **Markdown & Code Rendering:** Formats headers, lists, and language-tagged code blocks.
- **One-Tap Code Copy:** Dedicated copy button on every code container.
- **Stop Generation:** Tap the square stop button to halt generation immediately.
- **Streaming Output:** Tokens are rendered directly as emitted by `llama.cpp`.

---

## 8. Live Telemetry Metrics

Each completed assistant message displays an execution telemetry badge:

- **Decode Speed (`tok/s`):** Generation speed during response decoding: `(tokens - 1) / decode_seconds`.
- **TTFT (`ms`):** **Time to First Token** — latency spent evaluating prompt tokens before the first response token was generated.
- **Total Tokens:** The count of generated tokens in the reply.

---

## 9. Managing Conversations

- **Navigation Drawer:** Swipe from left edge or tap the menu icon to view conversations.
- **New Conversation:** Tap **+ New Chat** to start an independent thread.
- **Rename:** Tap the edit pencil icon to rename a conversation.
- **Delete:** Tap the trash icon to permanently remove a thread and its messages.

---

## 10. Inference Parameter Tuning Reference

Configurable under **Settings**:

| Parameter | Range | Default | Recommended | Description |
|:---|:---:|:---:|:---:|:---|
| **Temperature** | 0.0 – 2.0 | 0.70 | 0.2 (Code) / 0.7 (Chat) | Randomness control. 0.0 is deterministic/greedy. |
| **Top-P (Nucleus)** | 0.0 – 1.0 | 0.90 | 0.85 – 0.95 | Restricts selection to tokens within cumulative probability P. |
| **Min-P** | 0.0 – 1.0 | 0.05 | 0.05 | Filters tokens below `(Min-P * top_token_prob)`. Improves coherence. |
| **CPU Threads** | 1 – Cores | Auto | `Perf Cores - 1` | Parallel compute threads. Setting to max cores causes thermal throttling. |
| **Context Window** | 512 – 8192 | 2048 / 4096 | 2048 (4GB) / 4096 (8GB+) | Maximum active tokens. Larger contexts consume more RAM for the KV cache. |
| **Memory Map (mmap)** | Bool | Enabled | Enabled | Maps weights directly from disk into address space, lowering RAM usage. |
| **System Prompt** | String | Default | Task-dependent | Prepended system instruction governing persona and response format. |

---

## 11. Thermal & Battery Optimization

- **Thread Allocation Rule:** On typical 8-core mobile processors (1 Prime + 3 Performance + 4 Efficiency cores), set threads to **3 or 4**. Allocating 8 threads schedules work across slow efficiency cores and triggers thermal throttling within 30 seconds.
- **Avoid Heavy Charging While Inferencing:** High ambient heat from fast-charging drops sustained CPU frequencies.
- **Use Q4_K_M:** Reduces RAM bus memory bandwidth compared to 5-bit or 8-bit quantizations.

---

## 12. Troubleshooting & FAQ

### App crashes when loading a model
- **Cause:** Out of Memory (OOM). Device RAM is insufficient for model weights + KV cache.
- **Fix:** Use a smaller model (0.5B or 1B), lower Context Window to 2048 in Settings, and close background apps.

### Generation speed drops after 20–30 seconds
- **Cause:** CPU thermal throttling.
- **Fix:** Reduce the **CPU Threads** setting by 1 or 2 threads in Settings.

### Sideloaded GGUF file is not selectable
- **Cause:** File missing `.gguf` extension or placed in a restricted Android folder.
- **Fix:** Ensure the extension is lowercase `.gguf` and place the file in `/sdcard/Download/`.

### Model outputs repetitive or incoherent text
- **Cause:** Temperature is too high or sampling parameters are misconfigured.
- **Fix:** Reset **Temperature** to `0.70` and **Top-P** to `0.90`.
