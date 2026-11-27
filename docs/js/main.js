/**
 * Locian Interactive Showcase & Documentation Engine
 * Version 2.0.0
 */

document.addEventListener('DOMContentLoaded', () => {
  initTheme();
  initMobileNav();
  initShowcaseTabs();
  initSimulator();
  initRamCalculator();
  initModelFilter();
  initCopyButtons();
  initDocsSidebar();
});

/* --------------------------------------------------------------------------
   1. Theme Management (System, Dark, Light)
   -------------------------------------------------------------------------- */
function initTheme() {
  const themeToggleBtn = document.getElementById('themeToggleBtn');
  const fullLogoDark = document.querySelectorAll('.logo-dark');
  const fullLogoLight = document.querySelectorAll('.logo-light');

  const savedTheme = localStorage.getItem('locian_docs_theme');
  const prefersDark = window.matchMedia('(prefers-color-scheme: dark)').matches;
  const initialTheme = savedTheme || (prefersDark ? 'dark' : 'light');

  applyTheme(initialTheme);

  if (themeToggleBtn) {
    themeToggleBtn.addEventListener('click', () => {
      const currentTheme = document.documentElement.getAttribute('data-theme') || 'dark';
      const newTheme = currentTheme === 'dark' ? 'light' : 'dark';
      applyTheme(newTheme);
      localStorage.setItem('locian_docs_theme', newTheme);
    });
  }

  function applyTheme(theme) {
    document.documentElement.setAttribute('data-theme', theme);
    if (themeToggleBtn) {
      if (theme === 'dark') {
        themeToggleBtn.innerHTML = `
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <circle cx="12" cy="12" r="5"></circle>
            <line x1="12" y1="1" x2="12" y2="3"></line>
            <line x1="12" y1="21" x2="12" y2="23"></line>
            <line x1="4.22" y1="4.22" x2="5.64" y2="5.64"></line>
            <line x1="18.36" y1="18.36" x2="19.78" y2="19.78"></line>
            <line x1="1" y1="12" x2="3" y2="12"></line>
            <line x1="21" y1="12" x2="23" y2="12"></line>
            <line x1="4.22" y1="19.78" x2="5.64" y2="18.36"></line>
            <line x1="18.36" y1="5.64" x2="19.78" y2="4.22"></line>
          </svg>`;
        themeToggleBtn.title = 'Switch to light mode';
      } else {
        themeToggleBtn.innerHTML = `
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="M21 12.79A9 9 0 1 1 11.21 3 7 7 0 0 0 21 12.79z"></path>
          </svg>`;
        themeToggleBtn.title = 'Switch to dark mode';
      }
    }
    fullLogoDark.forEach(el => el.style.display = theme === 'dark' ? 'inline-block' : 'none');
    fullLogoLight.forEach(el => el.style.display = theme === 'light' ? 'inline-block' : 'none');
  }

  window.matchMedia('(prefers-color-scheme: dark)').addEventListener('change', e => {
    if (!localStorage.getItem('locian_docs_theme')) {
      applyTheme(e.matches ? 'dark' : 'light');
    }
  });
}

/* --------------------------------------------------------------------------
   2. Mobile Navigation Drawer
   -------------------------------------------------------------------------- */
function initMobileNav() {
  const toggleBtn = document.getElementById('mobileNavToggle');
  const drawer = document.getElementById('mobileDrawer');

  if (toggleBtn && drawer) {
    toggleBtn.addEventListener('click', () => {
      drawer.classList.toggle('active');
    });

    drawer.querySelectorAll('a').forEach(link => {
      link.addEventListener('click', () => {
        drawer.classList.remove('active');
      });
    });
  }
}

/* --------------------------------------------------------------------------
   3. Phone Showcase Screenshot Tabs
   -------------------------------------------------------------------------- */
function initShowcaseTabs() {
  const tabButtons = document.querySelectorAll('.showcase-tab-btn');
  const phoneImg = document.getElementById('phoneMockupImg');

  const screenshots = {
    chat: 'assets/screenshot-chat.png',
    empty: 'assets/screenshot-empty.png',
    settingsDark: 'assets/screenshot-settings-dark.png',
    settingsLight: 'assets/screenshot-settings-light.png'
  };

  tabButtons.forEach(btn => {
    btn.addEventListener('click', () => {
      const tab = btn.dataset.tab;
      tabButtons.forEach(b => b.classList.remove('active'));
      btn.classList.add('active');

      if (phoneImg && screenshots[tab]) {
        phoneImg.style.opacity = '0.3';
        setTimeout(() => {
          phoneImg.src = screenshots[tab];
          phoneImg.style.opacity = '1';
        }, 140);
      }

      document.querySelectorAll('.tab-pane').forEach(pane => {
        pane.classList.remove('active');
        if (pane.id === `tab-${tab}`) {
          pane.classList.add('active');
        }
      });
    });
  });
}

/* --------------------------------------------------------------------------
   4. Live Inference Simulator
   -------------------------------------------------------------------------- */
function initSimulator() {
  const thread = document.getElementById('simThread');
  const inputEl = document.getElementById('simInput');
  const sendBtn = document.getElementById('simSendBtn');
  const stopBtn = document.getElementById('simStopBtn');
  const resetBtn = document.getElementById('simResetBtn');
  const chips = document.querySelectorAll('.sim-chip');
  const modelSelector = document.getElementById('simModelSelector');
  const modelNameEl = document.getElementById('simSelectedModelName');

  // Dashboard elements
  const dashSpeed = document.getElementById('dashSpeed');
  const dashTtft = document.getElementById('dashTtft');
  const dashBudget = document.getElementById('dashBudget');
  const dashRam = document.getElementById('dashRam');
  const dashSpeedMeter = document.getElementById('dashSpeedMeter');
  const dashBudgetMeter = document.getElementById('dashBudgetMeter');

  let streamTimer = null;
  let isGenerating = false;
  let currentModel = 'qwen05';

  const modelsConfig = {
    qwen05: { name: 'Qwen 2.5 0.5B', speed: 38.4, ttft: 180, ram: '~380 MB', speedMeter: 92 },
    llama1b: { name: 'Llama 3.2 1B', speed: 24.2, ttft: 240, ram: '~770 MB', speedMeter: 75 },
    deepseek: { name: 'DeepSeek R1 1.5B', speed: 18.6, ttft: 310, ram: '~1.0 GB', speedMeter: 62 },
    qwen15: { name: 'Qwen 2.5 1.5B Coder', speed: 19.8, ttft: 290, ram: '~940 MB', speedMeter: 65 }
  };

  const presets = {
    python: {
      user: "Write a clean Python script demonstrating asynchronous task queues.",
      think: "Need an idiomatic asyncio queue implementation with bounded concurrency using asyncio.Semaphore. Construct clear worker and producer functions with descriptive logging.",
      answer: "Here is a clean Python script using `asyncio` to implement an in-memory asynchronous task queue with bounded worker concurrency:",
      code: `import asyncio
from typing import Any

class TaskQueue:
    def __init__(self, max_concurrency: int = 3):
        self.queue = asyncio.Queue()
        self.semaphore = asyncio.Semaphore(max_concurrency)

    async def enqueue(self, task_name: str, payload: Any):
        await self.queue.put((task_name, payload))
        print(f"📦 [Queued] {task_name}")

    async def worker(self, worker_id: int):
        while True:
            task_name, payload = await self.queue.get()
            async with self.semaphore:
                print(f"⚡ [Worker {worker_id}] Executing: {task_name}")
                await asyncio.sleep(0.4)
            self.queue.task_done()`,
      speed: 21.4,
      ttft: 280,
      tokens: 142,
      thinkTokens: 26
    },
    deepseek: {
      user: "How do transformer attention mechanisms scale with context length?",
      think: "Question: Transformer attention scaling.\n1. Standard multi-head self-attention computes Q * K^T with shape (N, d) x (d, N) -> (N, N) attention matrix.\n2. Both memory footprint and FLOPs scale as O(N^2) where N is token sequence length.\n3. Solutions for mobile on-device LLMs: FlashAttention, Sliding Window Attention (Gemma 2), grouped-query attention (GQA), and KV cache quantization (Q4/Q8).",
      answer: "Standard Scaled Dot-Product Attention scales **quadratically O(N²)** in both compute and memory with respect to context sequence length $N$:\n\n• **Compute Complexity**: Calculating the attention score matrix $Q K^T$ requires $O(N^2 \\cdot d)$ FLOPs.\n• **KV Cache Footprint**: Memory scales as $2 \\times L \\times H \\times N \\times d_h$, demanding several gigabytes on mobile devices at $N=8192$.\n• **On-Device Optimizations**: Locian utilizes **GQA (Grouped-Query Attention)** and aggressive KV cache quantization to run 4k–8k context comfortably within 4–6 GB RAM.",
      speed: 18.2,
      ttft: 310,
      tokens: 168,
      thinkTokens: 74
    },
    delhi: {
      user: "Who is the Chief Minister of Delhi?",
      think: "The user is inquiring about the current Chief Minister of Delhi. Fact check: Arvind Kejriwal resigned in September 2024. Following his resignation, Atishi Marlena was sworn in as the 8th Chief Minister of Delhi on September 21, 2024. Safe reasoning budget maintained.",
      answer: "The Chief Minister of Delhi is **Atishi Marlena**.\n\nShe assumed office on **September 21, 2024**, following the resignation of Arvind Kejriwal, becoming the third woman to serve as Chief Minister of Delhi after Sushma Swaraj and Sheila Dikshit.",
      speed: 36.8,
      ttft: 190,
      tokens: 64,
      thinkTokens: 38
    },
    quantum: {
      user: "Explain quantum computing simply.",
      think: "Target audience needs a high-level conceptual explanation focusing on qubits, superposition, and entanglement without heavy mathematical formalisms.",
      answer: "Quantum computing leverages the principles of quantum mechanics to solve problems that classical supercomputers cannot practically compute:\n\n1. **Superposition**: Unlike classical bits (0 or 1), a **qubit** can exist in a superposition of both states simultaneously.\n2. **Entanglement**: Qubits become linked such that measuring one instantly determines the state of the other.\n3. **Exponential Parallelism**: Enables quadratic or exponential speedups for specialized domains like molecular chemistry, materials science, and cryptography.",
      speed: 28.4,
      ttft: 220,
      tokens: 96,
      thinkTokens: 24
    },
    ideas: {
      user: "Give me 5 creative ideas for on-device local LLM apps.",
      think: "Brainstorm private offline-first use cases that uniquely benefit from zero cloud latency, zero API costs, and total data privacy.",
      answer: "Here are 5 high-impact on-device AI applications enabled by local inference:\n\n• **Confidential Journal & Therapist**: Analyzes private thoughts, biometric mood data, and medical notes with zero data leakage.\n• **Offline Airfield / Maritime Co-Pilot**: Answers technical manuals, diagnostics, and regex/bash commands with zero cellular connectivity.\n• **Encrypted Document Search**: Summarizes and queries scanned tax returns and bank PDFs entirely in device memory.\n• **Ultra-Low Latency Voice Assistant**: Instant bilingual translation with local Whisper STT paired with quantized GGUF.\n• **Smart Photo Album Synthesizer**: Generates natural language descriptions and semantic search for local photos.",
      speed: 26.5,
      ttft: 240,
      tokens: 132,
      thinkTokens: 28
    }
  };

  function escapeHtml(text) {
    return text.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;');
  }

  function formatText(text) {
    let html = escapeHtml(text);
    html = html.replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>');
    html = html.replace(/`([^`]+)`/g, '<code class="sim-inline-code">$1</code>');
    html = html.replace(/^• (.*)$/gm, '<li class="sim-bullet">$1</li>');
    html = html.replace(/(<li class="sim-bullet">.*<\/li>)/s, '<ul class="sim-ul">$1</ul>');
    html = html.replace(/\n\n/g, '<div style="height: 10px;"></div>');
    html = html.replace(/\n/g, '<br>');
    return html;
  }

  function highlightSyntax(code) {
    return code
      .replace(/&/g, '&amp;')
      .replace(/</g, '&lt;')
      .replace(/>/g, '&gt;')
      .replace(/\b(import|from|class|def|async|await|while|with|as|for|in|return|True|False)\b/g, '<span style="color: #f43f5e; font-weight: 600;">$1</span>')
      .replace(/\b(TaskQueue|Queue|Semaphore|Any|int|str)\b/g, '<span style="color: #38bdf8;">$1</span>')
      .replace(/("(?:[^"\\]|\\.)*")/g, '<span style="color: #a3e635;">$1</span>')
      .replace(/\b(\d+)\b/g, '<span style="color: #fb923c;">$1</span>')
      .replace(/(#.*)$/gm, '<span style="color: #64748b; font-style: italic;">$1</span>');
  }

  function scrollScreenToBottom() {
    const screen = document.getElementById('simScreen');
    if (screen) {
      screen.scrollTo({ top: screen.scrollHeight, behavior: 'smooth' });
    }
  }

  function stopGeneration() {
    if (streamTimer) {
      clearInterval(streamTimer);
      streamTimer = null;
    }
    isGenerating = false;
    if (sendBtn) sendBtn.style.display = 'inline-flex';
    if (stopBtn) stopBtn.style.display = 'none';
  }

  function updateDashboard(speed, ttft, budget, ram, meter) {
    if (dashSpeed) dashSpeed.innerHTML = `${speed} <span class="unit">tok/s</span>`;
    if (dashTtft) dashTtft.innerHTML = `${ttft} <span class="unit">ms</span>`;
    if (dashBudget) dashBudget.innerHTML = `${budget} <span class="unit">/ 384 max</span>`;
    if (dashRam) dashRam.innerHTML = `${ram}`;
    if (dashSpeedMeter) dashSpeedMeter.style.width = `${meter || 75}%`;
    if (dashBudgetMeter) dashBudgetMeter.style.width = `${Math.min(100, Math.round((budget / 384) * 100))}%`;
  }

  function runPreset(key) {
    if (isGenerating) stopGeneration();
    const data = presets[key] || presets.python;

    if (thread) {
      thread.innerHTML = `
        <div class="sim-bubble-user">${escapeHtml(data.user)}</div>
        <div class="sim-bubble-ai" id="activeAiBubble">
          ${data.think ? `
            <div class="sim-think-box" id="activeThinkBox">
              <div class="sim-think-header" onclick="this.nextElementSibling.style.display = this.nextElementSibling.style.display === 'none' ? 'block' : 'none'">
                <span>🧠 Chain-of-Thought Reasoning (${data.thinkTokens} tokens)</span>
                <span style="font-size: 0.75rem;">▼</span>
              </div>
              <div class="sim-think-content">${escapeHtml(data.think)}</div>
            </div>` : ''}
          <div id="activeStreamText"></div>
          ${data.code ? `
            <div style="background: #090d16; border: 1px solid #1e293b; border-radius: 8px; padding: 12px; margin-top: 10px; font-family: var(--font-mono); font-size: 0.8rem; overflow-x: auto;">
              <pre><code>${highlightSyntax(data.code)}</code></pre>
            </div>` : ''}
          <div style="margin-top: 12px; display: flex; gap: 8px; flex-wrap: wrap;">
            <span class="badge-tag badge-ram">⚡ ${data.speed} tok/s</span>
            <span class="badge-tag badge-q4">TTFT ${data.ttft} ms</span>
            <span class="badge-tag badge-purple">${data.tokens} tokens</span>
          </div>
        </div>
      `;
    }

    const streamEl = document.getElementById('activeStreamText');
    if (!streamEl) return;

    isGenerating = true;
    if (sendBtn) sendBtn.style.display = 'none';
    if (stopBtn) stopBtn.style.display = 'inline-flex';

    updateDashboard(data.speed, data.ttft, data.thinkTokens, modelsConfig[currentModel].ram, modelsConfig[currentModel].speedMeter);

    const fullText = data.answer;
    let charIndex = 0;
    streamEl.innerHTML = '<span class="sim-cursor" style="display:inline-block; width:6px; height:14px; background:var(--accent-red); vertical-align:middle; animation: pulseDot 0.8s infinite;"></span>';

    const intervalMs = Math.max(8, Math.round(1000 / (data.speed * 4)));

    streamTimer = setInterval(() => {
      charIndex += 4;
      if (charIndex >= fullText.length) {
        streamEl.innerHTML = formatText(fullText);
        stopGeneration();
        scrollScreenToBottom();
      } else {
        const partial = fullText.substring(0, charIndex);
        streamEl.innerHTML = formatText(partial) + '<span class="sim-cursor" style="display:inline-block; width:6px; height:14px; background:var(--accent-red); margin-left:2px; vertical-align:middle;"></span>';
        scrollScreenToBottom();
      }
    }, intervalMs);
  }

  // Model Selector
  if (modelSelector) {
    const modelKeys = Object.keys(modelsConfig);
    modelSelector.addEventListener('click', () => {
      const curIdx = modelKeys.indexOf(currentModel);
      const nextIdx = (curIdx + 1) % modelKeys.length;
      currentModel = modelKeys[nextIdx];
      const cfg = modelsConfig[currentModel];
      if (modelNameEl) modelNameEl.textContent = cfg.name;
      updateDashboard(cfg.speed, cfg.ttft, 32, cfg.ram, cfg.speedMeter);
    });
  }

  // Chips click
  chips.forEach(chip => {
    chip.addEventListener('click', () => {
      chips.forEach(c => c.classList.remove('active'));
      chip.classList.add('active');
      const promptKey = chip.dataset.prompt;
      runPreset(promptKey);
    });
  });

  // Send button
  if (sendBtn) {
    sendBtn.addEventListener('click', () => {
      const q = inputEl.value.trim();
      if (!q) return;
      inputEl.value = '';
      if (isGenerating) stopGeneration();

      if (thread) {
        thread.innerHTML += `
          <div class="sim-bubble-user">${escapeHtml(q)}</div>
          <div class="sim-bubble-ai">
            <div>Locian is processing on-device using local CPU cores. Total data isolation guaranteed: 0 bytes sent over network.</div>
            <div style="margin-top: 10px; display: flex; gap: 8px;">
              <span class="badge-tag badge-ram">⚡ 28.4 tok/s</span>
              <span class="badge-tag badge-q4">TTFT 220 ms</span>
            </div>
          </div>
        `;
        scrollScreenToBottom();
      }
    });
  }

  if (inputEl) {
    inputEl.addEventListener('keydown', e => {
      if (e.key === 'Enter') {
        sendBtn.click();
      }
    });
  }

  if (stopBtn) {
    stopBtn.addEventListener('click', stopGeneration);
  }

  if (resetBtn) {
    resetBtn.addEventListener('click', () => {
      stopGeneration();
      if (thread) {
        thread.innerHTML = `
          <div class="sim-bubble-ai" style="text-align: center; color: var(--text-muted); font-size: 0.85rem; padding: 12px;">
            Conversation cleared. Select a quick prompt chip or type below.
          </div>
        `;
      }
    });
  }

  // Initialize with python preset
  runPreset('python');
}

/* --------------------------------------------------------------------------
   5. Interactive RAM Compatibility Calculator
   -------------------------------------------------------------------------- */
function initRamCalculator() {
  const slider = document.getElementById('ramSlider');
  const displayVal = document.getElementById('ramSliderVal');
  const tierVal = document.getElementById('calcTierVal');
  const ctxVal = document.getElementById('calcCtxVal');
  const lmkVal = document.getElementById('calcLmkVal');

  if (!slider) return;

  function updateCalc(ram) {
    if (displayVal) displayVal.textContent = `${ram} GB RAM`;

    let tier = '';
    let ctx = '';
    let lmk = '';
    let color = '#10b981';

    if (ram <= 3) {
      tier = 'Compact (0.5B – 1B)';
      ctx = '2048 tokens';
      lmk = 'Safe with 0.5B; tight with 1B';
      color = '#f59e0b';
    } else if (ram <= 4) {
      tier = 'Standard (1B – 1.5B)';
      ctx = '2048 – 4096 tokens';
      lmk = 'Optimal; Low LMK risk';
      color = '#10b981';
    } else if (ram <= 6) {
      tier = 'High Perf (1.5B – 3B)';
      ctx = '4096 – 8192 tokens';
      lmk = 'Zero LMK risk; Fast decode';
      color = '#10b981';
    } else if (ram <= 8) {
      tier = 'Flagship (3B – 7B MoE)';
      ctx = '8192 tokens';
      lmk = 'Immune; High concurrency';
      color = '#0ea5e9';
    } else {
      tier = 'Workstation (7B – 14B Q4)';
      ctx = '16384 tokens';
      lmk = 'Desktop grade execution';
      color = '#a855f7';
    }

    if (tierVal) tierVal.textContent = tier;
    if (ctxVal) ctxVal.textContent = ctx;
    if (lmkVal) {
      lmkVal.textContent = lmk;
      lmkVal.style.color = color;
    }

    // Highlight matching table rows
    document.querySelectorAll('.matrix-table tbody tr').forEach(row => {
      const minRam = parseInt(row.getAttribute('data-ram') || '4', 10);
      if (minRam <= ram) {
        row.style.opacity = '1';
        row.style.background = 'transparent';
      } else {
        row.style.opacity = '0.35';
      }
    });
  }

  slider.addEventListener('input', e => {
    updateCalc(parseInt(e.target.value, 10));
  });

  updateCalc(parseInt(slider.value, 10));
}

/* --------------------------------------------------------------------------
   6. Model Matrix Filtering & Search
   -------------------------------------------------------------------------- */
function initModelFilter() {
  const filterBtns = document.querySelectorAll('.filter-btn');
  const searchInput = document.getElementById('modelSearchInput');
  const rows = document.querySelectorAll('.matrix-table tbody tr');

  function applyFilters() {
    const activeBtn = document.querySelector('.filter-btn.active');
    const filter = activeBtn ? activeBtn.dataset.filter : 'all';
    const query = searchInput ? searchInput.value.toLowerCase().trim() : '';

    rows.forEach(row => {
      const ram = parseInt(row.getAttribute('data-ram') || '4', 10);
      const text = row.innerText.toLowerCase();

      let matchFilter = true;
      if (filter === 'low') matchFilter = ram <= 3;
      else if (filter === 'balanced') matchFilter = ram >= 4 && ram <= 6;
      else if (filter === 'flagship') matchFilter = ram >= 6;

      const matchSearch = !query || text.includes(query);

      row.style.display = matchFilter && matchSearch ? '' : 'none';
    });
  }

  filterBtns.forEach(btn => {
    btn.addEventListener('click', () => {
      filterBtns.forEach(b => b.classList.remove('active'));
      btn.classList.add('active');
      applyFilters();
    });
  });

  if (searchInput) {
    searchInput.addEventListener('input', applyFilters);
  }
}

/* --------------------------------------------------------------------------
   7. Copy to Clipboard with Toast Notification
   -------------------------------------------------------------------------- */
function initCopyButtons() {
  let toast = document.querySelector('.toast-msg');
  if (!toast) {
    toast = document.createElement('div');
    toast.className = 'toast-msg';
    toast.textContent = 'Copied to clipboard!';
    document.body.appendChild(toast);
  }

  function showToast(msg) {
    toast.textContent = msg || 'Copied to clipboard!';
    toast.classList.add('show');
    setTimeout(() => {
      toast.classList.remove('show');
    }, 2200);
  }

  document.querySelectorAll('.copy-icon-btn').forEach(btn => {
    btn.addEventListener('click', () => {
      const parent = btn.closest('.code-block-display') || btn.parentElement;
      const code = parent.querySelector('code');
      const text = code ? code.innerText : parent.innerText;

      navigator.clipboard.writeText(text).then(() => {
        btn.textContent = 'Copied!';
        showToast('Copied to clipboard!');
        setTimeout(() => {
          btn.textContent = 'Copy';
        }, 2000);
      });
    });
  });
}

/* --------------------------------------------------------------------------
   8. Docs Sidebar Scrollspy
   -------------------------------------------------------------------------- */
function initDocsSidebar() {
  const links = document.querySelectorAll('.docs-nav-link');
  const sections = document.querySelectorAll('.docs-section');

  if (links.length === 0 || sections.length === 0) return;

  window.addEventListener('scroll', () => {
    let currentId = '';
    const scrollPos = window.scrollY + 120;

    sections.forEach(sec => {
      if (sec.offsetTop <= scrollPos) {
        currentId = sec.getAttribute('id');
      }
    });

    if (currentId) {
      links.forEach(l => {
        l.classList.remove('active');
        if (l.getAttribute('href') === `#${currentId}`) {
          l.classList.add('active');
        }
      });
    }
  });
}
