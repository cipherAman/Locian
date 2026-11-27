# Third-Party License Registry

Locian's Kotlin source is licensed under **Apache 2.0**.
Locian's native C++ inference bridge (`inference/src/main/cpp/`) is licensed under **MIT**.

The following third-party components are included under their own licenses.
All dependencies use MIT or Apache 2.0 — both are permissive and fully compatible
with each other and with Locian's dual Apache 2.0 / MIT model. No GPL dependencies
exist in the dependency graph.

## Native / C++ (Git Submodules)

| Library | License | URL |
|:---|:---|:---|
| llama.cpp | MIT | https://github.com/ggerganov/llama.cpp |
| google/cpu_features | Apache 2.0 | https://github.com/google/cpu_features |

## Android / Kotlin Libraries

| Library | Version | License |
|:---|:---|:---|
| AndroidX Core KTX | 1.15.0 | Apache 2.0 |
| AndroidX Lifecycle Runtime KTX | 2.8.7 | Apache 2.0 |
| AndroidX Lifecycle ViewModel KTX | 2.8.7 | Apache 2.0 |
| AndroidX Lifecycle ViewModel Compose | 2.8.7 | Apache 2.0 |
| AndroidX Activity Compose | 1.10.0 | Apache 2.0 |
| Jetpack Compose BOM | 2024.12.01 | Apache 2.0 |
| Jetpack Navigation Compose | 2.8.5 | Apache 2.0 |
| Room Runtime | 2.6.1 | Apache 2.0 |
| Room KTX | 2.6.1 | Apache 2.0 |
| DataStore Preferences | 1.1.2 | Apache 2.0 |
| Hilt / Dagger | 2.52 | Apache 2.0 |
| Hilt Navigation Compose | 1.2.0 | Apache 2.0 |
| OkHttp | 4.12.0 | Apache 2.0 |
| Kotlin Coroutines Core | 1.10.1 | Apache 2.0 |
| Kotlin Coroutines Android | 1.10.1 | Apache 2.0 |
| Kotlin Serialization JSON | 1.7.3 | Apache 2.0 |
| Markwon Core | 4.6.2 | Apache 2.0 |
| Markwon Syntax Highlight | 4.6.2 | Apache 2.0 |
| Coil 3 Compose | 3.0.4 | Apache 2.0 |
| JUnit 4 | 4.13.2 | EPL 1.0 |
| AndroidX JUnit | 1.2.1 | Apache 2.0 |
| AndroidX Espresso Core | 3.6.1 | Apache 2.0 |

## License Compatibility Note

All runtime dependencies use MIT or Apache 2.0.
JUnit 4 (EPL 1.0) is used **only for testing** and is not shipped in release APKs.
No GPL, LGPL, or AGPL dependencies are present in the dependency graph.
