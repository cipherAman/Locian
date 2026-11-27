# Security Policy

## Supported Versions

| Version | Supported |
|:---|:---|
| Latest release | ✅ |
| < 1.0 (pre-release) | ❌ |

Security fixes are backported only to the most recent stable release.

---

## Reporting a Vulnerability

**Please do NOT open a public GitHub issue for security vulnerabilities.**

Use **GitHub Private Vulnerability Reporting**:
1. Go to the repository on GitHub
2. Click **Security** tab → **Advisories**
3. Click **"Report a vulnerability"**

This creates a private advisory visible only to maintainers.

### Response SLA

| Stage | Timeline |
|:---|:---|
| Acknowledge receipt | Within 72 hours |
| Triage and assess severity | Within 7 days |
| Patch + advisory (critical) | Within 14 days |
| Patch + advisory (moderate) | Within 30 days |

---

## Locian's Attack Surface

Locian has **no network servers** — all inference is 100% local. The primary risk surfaces are:

### 1. Malicious GGUF Model Files
- GGUF format supports executable metadata injection
- Locian loads user-provided GGUF files from device storage
- **Mitigation**: Validate GGUF magic bytes (`0x47 0x47 0x55 0x46`) and version header before loading

### 2. Storage Permission Abuse
- Locian requests `READ_EXTERNAL_STORAGE` for custom GGUF import
- Path traversal from a malicious filename could potentially access unintended files
- **Mitigation**: Canonicalize and validate all file paths before passing to native layer

### 3. JNI Native Crashes from Crafted Model Weights
- llama.cpp parses binary data; malformed weights could cause buffer-related issues in native code
- **Mitigation**: Rely on llama.cpp's internal validation; report critical parser issues upstream at https://github.com/ggerganov/llama.cpp

---

## Out of Scope

The following are **not** in scope for Locian's security policy:

- Bugs in upstream **llama.cpp** → report to [ggerganov/llama.cpp](https://github.com/ggerganov/llama.cpp/security)
- Bugs in upstream **google/cpu_features** → report to [google/cpu_features](https://github.com/google/cpu_features/security)
- Theoretical hardware attacks (Spectre, Meltdown, Rowhammer)
- Issues requiring physical device access
- Denial-of-service via extremely large model files (by design — user-controlled)
