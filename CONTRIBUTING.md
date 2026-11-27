# Contributing to Locian

Thank you for your interest in contributing! Locian is fully open source (Apache 2.0 + MIT) and welcomes all contributions.

## Code of Conduct

This project follows the [Contributor Covenant v2.1](CODE_OF_CONDUCT.md). By participating, you agree to uphold this code.

---

## Ways to Contribute

- 🐛 **Report bugs** — open a [Bug Report](../../issues/new?template=bug_report.yml)
- 💡 **Request features** — open a [Feature Request](../../issues/new?template=feature_request.yml)
- 📦 **Request a model** — open a [Model Catalog Request](../../issues/new?template=model_request.yml)
- 🔀 **Submit pull requests** — see the workflow below
- 📖 **Improve documentation** — docs are in `docs/` and in-source KDoc
- 📱 **Test on new devices** — especially non-Snapdragon chipsets (MediaTek, Exynos)

---

## Development Environment Setup

### Prerequisites

| Requirement | Version |
|:---|:---|
| Android Studio | Meerkat (2024.3.1)+ |
| NDK | **r27.0.12077973** (exact) |
| CMake | **3.22.1** (exact) |
| JDK | 17 (Temurin/Adoptium) |
| RAM | 16 GB+ |
| Disk | ~8 GB free |

### Setup Steps

```bash
# 1. Fork the repo on GitHub, then clone YOUR fork with submodules
git clone --recurse-submodules https://github.com/YOUR_USERNAME/Locian.git
cd Locian

# 2. Add upstream remote
git remote add upstream https://github.com/cipherAman/Locian.git

# 3. Set up local.properties
cp local.properties.example local.properties
# Edit local.properties: set sdk.dir to your Android SDK path

# 4. Install NDK r27 via Android Studio:
#    SDK Manager → SDK Tools → NDK (Side by side) → 27.0.12077973

# 5. Verify build
./gradlew assembleDebug
```

---

## Project Structure

See [README.md](README.md#architecture-overview) for the full module map.

---

## Branch Strategy

| Branch | Purpose |
|:---|:---|
| `main` | Stable tagged releases only — do NOT PR here |
| `develop` | Active development integration — target for all PRs |
| `feature/xxx` | New features — branch from `develop` |
| `fix/xxx` | Bug fixes — branch from `develop` |
| `hotfix/xxx` | Critical patches — branch from `main`, merge to both |

---

## Making a Contribution

1. **Find or file an issue** — check [existing issues](../../issues) first
2. **Comment on the issue** to let others know you're working on it
3. **Fork the repo** and clone your fork
4. **Create a branch** from `develop`:
   ```bash
   git checkout develop
   git pull upstream develop
   git checkout -b feature/your-feature-name
   ```
5. **Write code** following the style guide below
6. **Run checks locally** before pushing:
   ```bash
   ./gradlew ktlintCheck detekt lint test
   ```
7. **Open a PR** against `develop` using the [PR template](.github/PULL_REQUEST_TEMPLATE.md)

---

## Commit Message Format

Use [Conventional Commits](https://www.conventionalcommits.org/):

```
feat: add Vulkan backend option in settings
fix: prevent OOM on 4GB devices with 7B model
docs: update NDK setup instructions
refactor: extract inference state machine
test: add unit tests for download resume logic
chore: bump Compose BOM to 2024.12.01
```

---

## Code Style Guide

### Kotlin

- **ktlint** enforced — 120 character line limit
- Official [Kotlin style guide](https://kotlinlang.org/docs/coding-conventions.html)
- State hoisting in all Composables
- Single-responsibility Composable functions
- Use `AppResult<T>` for all use-case return types

### C++ / Native

- [Google C++ Style Guide](https://google.github.io/styleguide/cppguide.html)
- All JNI functions must be exception-safe (wrap in try/catch)
- No global mutable state in native layer
- Document all JNI boundary functions with comments

---

## Testing

- Unit tests are required for all use cases and repositories
- Native changes must be tested on a **physical ARM64 device** (emulator won't exercise native paths)
- Emulator is acceptable for Kotlin-only UI changes
- Run tests: `./gradlew test`

---

## PR Review Process

- Maintainer reviews within **5 business days**
- **1 approving review** required to merge
- All CI checks must pass (lint, tests, build)
- Squash merge preferred for feature branches

---

## First-Time Contributors

Look for issues labeled [`good first issue`](../../issues?q=label%3A%22good+first+issue%22). Feel free to ask questions in issue comments — we're here to help!

---

## What We Won't Accept

- Breaking NDK ABI compatibility without a major version bump
- Adding telemetry, analytics, or any network calls home
- Cloud-dependent features (Locian is 100% offline by design)
- Changes to the license
- GPL-licensed dependencies (would force relicensing)
