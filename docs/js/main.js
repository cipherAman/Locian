/**
 * Locian Interactive Documentation & Showcase Engine
 */

document.addEventListener('DOMContentLoaded', () => {
  initTheme();
  initShowcaseTabs();
  initSimulator();
  initModelFilter();
  initCopyButtons();
  initDocsSidebar();
});

/* --------------------------------------------------------------------------
   1. Theme Management (System, Dark, Light)
   -------------------------------------------------------------------------- */
function initTheme() {
  const themeToggleBtn = document.getElementById('themeToggleBtn');
  const themeIcon = document.getElementById('themeIcon');
  const fullLogoDark = document.querySelectorAll('.logo-dark');
  const fullLogoLight = document.querySelectorAll('.logo-light');

  // Check saved theme or system preference
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
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
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
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="M21 12.79A9 9 0 1 1 11.21 3 7 7 0 0 0 21 12.79z"></path>
          </svg>`;
        themeToggleBtn.title = 'Switch to dark mode';
      }
    }
    // Toggle dark/light logos
    fullLogoDark.forEach(el => el.style.display = theme === 'dark' ? 'inline-block' : 'none');
    fullLogoLight.forEach(el => el.style.display = theme === 'light' ? 'inline-block' : 'none');
  }

  // Listen to OS theme changes if user hasn't explicitly set a preference
  window.matchMedia('(prefers-color-scheme: dark)').addEventListener('change', e => {
    if (!localStorage.getItem('locian_docs_theme')) {
      applyTheme(e.matches ? 'dark' : 'light');
    }
  });
}

/* --------------------------------------------------------------------------
   2. Phone Showcase Screenshot Tabs
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

      // Update phone screenshot
      if (phoneImg && screenshots[tab]) {
        phoneImg.style.opacity = '0.5';
        setTimeout(() => {
          phoneImg.src = screenshots[tab];
          phoneImg.style.opacity = '1';
        }, 120);
      }

      // Update info cards
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
   3. Live Inference Simulator
   -------------------------------------------------------------------------- */
function initSimulator() {
  const thread = document.getElementById('simThread');
  const inputEl = document.getElementById('simInput');
  const sendBtn = document.getElementById('simSendBtn');
  const stopBtn = document.getElementById('simStopBtn');
  const resetBtn = document.getElementById('simResetBtn');
  const chips = document.querySelectorAll('.sim-chip');
  const modelSelector = document.getElementById('simModelSelector');

  // Dashboard elements
  const dashSpeed = document.getElementById('dashSpeed');
  const dashTtft = document.getElementById('dashTtft');
  const dashBudget = document.getElementById('dashBudget');
  const dashSpeedMeter = document.getElementById('dashSpeedMeter');
  const dashBudgetMeter = document.getElementById('dashBudgetMeter');

  let streamTimer = null;
  let isGenerating = false;

  const presets = {
    delhi: {
      user: "Who is the Chief Minister of Delhi?",
      think: "The user is inquiring about the current Chief Minister of Delhi. Fact check: Arvind Kejriwal served until September 2024. Following his resignation, Atishi Marlena was selected by the legislature party and sworn in as the 8th Chief Minister of Delhi. Safe reasoning budget maintained. Outputting clear direct answer.",
      answer: "The Chief Minister of Delhi is **Atishi Marlena**.\n\nShe assumed office on **September 21, 2024**, following the resignation of Arvind Kejriwal, becoming the third woman to serve as Chief Minister of Delhi.",
      speed: 18.6,
      ttft: 310,
      tokens: 64,
      thinkTokens: 38
    },
    python: {
      user: "Write a clean Python script demonstrating asynchronous task queues.",
      think: "Need an idiomatic asyncio queue implementation with bounded concurrency using asyncio.Semaphore. Construct clear worker and producer functions.",
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
      speed: 18.4,
      ttft: 340,
      tokens: 142,
      thinkTokens: 26
    },
    quantum: {
      user: "Explain quantum computing simply.",
      think: "Target audience needs a high-level conceptual explanation focusing on qubits, superposition, and entanglement without heavy mathematics.",
      answer: "Quantum computing leverages the fundamental principles of quantum mechanics to solve problems that are intractable for classical computers:\n\n1. **Superposition**: Unlike classical bits that must be 0 or 1, a **qubit** can exist in a linear combination of both states simultaneously.\n2. **Entanglement**: Qubits can become correlated so that measuring one instantly dictates the state of another.\n3. **Exponential Parallelism**: Enables quadratic or exponential speedups for specialized problems like cryptographic factoring and molecular chemistry simulation.",
      speed: 21.2,
      ttft: 280,
      tokens: 88,
      thinkTokens: 22
    },
    ideas: {
      user: "Give me 5 creative ideas for on-device local LLM apps.",
      think: "Brainstorm private offline-first use cases that uniquely benefit from zero cloud latency, no subscription API costs, and total data privacy.",
      answer: "Here are 5 high-impact on-device AI applications enabled by local inference:\n\n• **Private Health & Journaling Companion**: Analyzes personal thoughts and health metrics with zero risk of cloud leaks.\n• **Offline Code & Terminal Co-Pilot**: Autocompletes bash commands, regexes, and code snippets completely on airplanes or remote sites.\n• **Local Document Search & Summarizer**: Queries PDFs and notes directly on device without uploading sensitive documents.\n• **Real-Time Voice Translator**: Instant bilingual conversations with on-device Whisper STT and compact quantized LLMs.\n• **Smart Photo Organizer**: Synthesizes natural-language search queries across local image metadata.",
      speed: 19.8,
      ttft: 320,
      tokens: 116,
      thinkTokens: 24
    }
  };

  function escapeHtml(text) {
    return text
      .replace(/&/g, '&amp;')
      .replace(/</g, '&lt;')
      .replace(/>/g, '&gt;');
  }

  function formatText(text) {
    let html = escapeHtml(text);
    // Bold
    html = html.replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>');
    // Inline code
    html = html.replace(/`([^`]+)`/g, '<code class="sim-inline-code">$1</code>');
    // Bullet points
    html = html.replace(/^• (.*)$/gm, '<li class="sim-bullet">$1</li>');
    html = html.replace(/(<li class="sim-bullet">.*<\/li>)/s, '<ul class="sim-ul">$1</ul>');
    // Line breaks
    html = html.replace(/\n\n/g, '<div class="sim-para-gap"></div>');
    html = html.replace(/\n/g, '<br>');
    return html;
  }

  function highlightSyntax(code) {
    return code
      .replace(/&/g, '&amp;')
      .replace(/</g, '&lt;')
      .replace(/>/g, '&gt;')
      .replace(/\b(import|from|class|def|async|await|while|with|as|for|in|return|True|False)\b/g, '<span class="syn-kw">$1</span>')
      .replace(/\b(TaskQueue|Queue|Semaphore|Any|int|str)\b/g, '<span class="syn-type">$1</span>')
      .replace(/("(?:[^"\\]|\\.)*")/g, '<span class="syn-str">$1</span>')
      .replace(/\b(\d+)\b/g, '<span class="syn-num">$1</span>')
      .replace(/(#.*)$/gm, '<span class="syn-com">$1</span>');
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

  function runPreset(key) {
    if (isGenerating) stopGeneration();
    const data = presets[key] || presets.python;

    // Reset thread
    if (!thread) return;
    thread.innerHTML = '';

    // Render User Bubble
    const userWrap = document.createElement('div');
    userWrap.className = 'sim-msg sim-msg-user';
    userWrap.innerHTML = `<div class="sim-bubble sim-bubble-user">${escapeHtml(data.user)}</div>`;
    thread.appendChild(userWrap);

    // Prepare Bot Container
    const botWrap = document.createElement('div');
    botWrap.className = 'sim-msg sim-msg-bot';

    const botAvatar = document.createElement('img');
    botAvatar.src = 'assets/logo.png';
    botAvatar.alt = 'Locian';
    botAvatar.className = 'sim-avatar';
    botWrap.appendChild(botAvatar);

    const botContent = document.createElement('div');
    botContent.className = 'sim-bot-body';
    botWrap.appendChild(botContent);
    thread.appendChild(botWrap);

    // Update Telemetry Panel
    if (dashSpeed) dashSpeed.innerHTML = `${data.speed.toFixed(1)} <span class="unit">tok/s</span>`;
    if (dashTtft) dashTtft.innerHTML = `${data.ttft} <span class="unit">ms</span>`;
    if (dashBudget) dashBudget.innerHTML = `${data.thinkTokens} <span class="unit">/ 384 max</span>`;
    if (dashSpeedMeter) dashSpeedMeter.style.width = `${Math.min(100, (data.speed / 25) * 100)}%`;
    if (dashBudgetMeter) dashBudgetMeter.style.width = `${Math.min(100, (data.thinkTokens / 384) * 100)}%`;

    // Mode: Generating
    isGenerating = true;
    if (sendBtn) sendBtn.style.display = 'none';
    if (stopBtn) stopBtn.style.display = 'inline-flex';

    // Step 1: Thinking block (collapsible)
    let thinkBlock = null;
    let thinkTextSpan = null;
    if (data.think) {
      thinkBlock = document.createElement('div');
      thinkBlock.className = 'sim-think-block';

      const thinkHeader = document.createElement('div');
      thinkHeader.className = 'sim-think-header';
      thinkHeader.innerHTML = `
        <div class="sim-think-title">
          <span class="sim-think-sparkle">✨</span>
          <span class="sim-think-label">Thinking...</span>
        </div>
        <span class="sim-think-chevron">▾</span>
      `;

      const thinkBody = document.createElement('div');
      thinkBody.className = 'sim-think-body';
      thinkTextSpan = document.createElement('span');
      thinkBody.appendChild(thinkTextSpan);

      thinkBlock.appendChild(thinkHeader);
      thinkBlock.appendChild(thinkBody);
      botContent.appendChild(thinkBlock);

      // Accordion click toggle
      thinkHeader.addEventListener('click', () => {
        thinkBlock.classList.toggle('collapsed');
      });
    }

    const answerDiv = document.createElement('div');
    answerDiv.className = 'sim-bubble-bot';
    botContent.appendChild(answerDiv);

    // Stream tokens
    const thinkWords = (data.think || '').split(' ');
    const answerWords = data.answer.split(' ');
    let thinkIdx = 0;
    let answerIdx = 0;

    scrollScreenToBottom();

    streamTimer = setInterval(() => {
      // Stream thinking first
      if (thinkBlock && thinkIdx < thinkWords.length) {
        thinkTextSpan.textContent += (thinkIdx > 0 ? ' ' : '') + thinkWords[thinkIdx];
        thinkIdx++;
        scrollScreenToBottom();
        return;
      }

      // Finish thinking -> collapse and update label
      if (thinkBlock && thinkIdx >= thinkWords.length && !thinkBlock.dataset.done) {
        thinkBlock.dataset.done = 'true';
        const label = thinkBlock.querySelector('.sim-think-label');
        if (label) label.textContent = `Thought for ${(data.thinkTokens * 0.045).toFixed(1)}s`;
        thinkBlock.classList.add('collapsed');
      }

      // Stream answer words
      if (answerIdx < answerWords.length) {
        answerIdx += 2; // Stream 2 words per tick for snappy local speed
        const currentSlice = answerWords.slice(0, answerIdx).join(' ');
        answerDiv.innerHTML = formatText(currentSlice);
        scrollScreenToBottom();
        return;
      }

      // Streaming complete: Add code block if present
      stopGeneration();

      if (data.code) {
        const codeCard = document.createElement('div');
        codeCard.className = 'sim-code-card';
        codeCard.innerHTML = `
          <div class="sim-code-header">
            <div class="sim-code-dots">
              <span class="sim-code-dot" style="background: #ef4444;"></span>
              <span class="sim-code-dot" style="background: #f59e0b;"></span>
              <span class="sim-code-dot" style="background: #10b981;"></span>
              <span class="sim-code-lang">PYTHON</span>
            </div>
            <button class="sim-code-copy-btn" title="Copy code">
              <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="9" y="9" width="13" height="13" rx="2" ry="2"></rect><path d="M5 15H4a2 2 0 0 1-2-2V4a2 2 0 0 1 2-2h9a2 2 0 0 1 2 2v1"></path></svg>
              <span>Copy</span>
            </button>
          </div>
          <pre class="sim-code-pre"><code>${highlightSyntax(data.code)}</code></pre>
        `;

        const copyBtn = codeCard.querySelector('.sim-code-copy-btn');
        if (copyBtn) {
          copyBtn.addEventListener('click', () => {
            navigator.clipboard.writeText(data.code).then(() => {
              copyBtn.querySelector('span').textContent = 'Copied!';
              setTimeout(() => {
                copyBtn.querySelector('span').textContent = 'Copy';
              }, 2000);
            });
          });
        }
        botContent.appendChild(codeCard);
      }

      // Append Telemetry pill
      const pill = document.createElement('div');
      pill.className = 'sim-telemetry-pill';
      pill.innerHTML = `
        <span>⚡ ${data.speed.toFixed(1)} t/s</span>
        <span>⏱️ ${data.ttft}ms TTFT</span>
        <span>🧠 ${data.thinkTokens} think tok</span>
        <span>📊 ${data.tokens} total tok</span>
      `;
      botContent.appendChild(pill);
      scrollScreenToBottom();

    }, 38);
  }

  function handleCustomQuery(rawQuery) {
    const q = rawQuery.trim();
    if (!q) return;

    if (inputEl) inputEl.value = '';

    // Check presets
    const lower = q.toLowerCase();
    if (lower.includes('delhi') || lower.includes('chief minister') || lower.includes('cm')) {
      runPreset('delhi');
      return;
    }
    if (lower.includes('python') || lower.includes('async') || lower.includes('queue') || lower.includes('code')) {
      runPreset('python');
      return;
    }
    if (lower.includes('quantum') || lower.includes('qubit')) {
      runPreset('quantum');
      return;
    }
    if (lower.includes('idea') || lower.includes('offline') || lower.includes('creative')) {
      runPreset('ideas');
      return;
    }

    // Dynamic response for arbitrary prompt
    const dynamicData = {
      user: q,
      think: `Analyzing the prompt: "${q}". The user is requesting on-device processing. Accessing quantized weights in RAM and applying temperature sampling without cloud transmission.`,
      answer: `Here is the response processed entirely on-device:\n\n**${escapeHtml(q)}** was analyzed locally using our ARM NEON-optimized llama.cpp backend. The model performed prompt evaluation with zero network latency or external API calls.`,
      speed: 19.1,
      ttft: 295,
      tokens: 72,
      thinkTokens: 28
    };

    presets._custom = dynamicData;
    runPreset('_custom');
  }

  // Bind Chip clicks
  chips.forEach(chip => {
    chip.addEventListener('click', () => {
      chips.forEach(c => c.classList.remove('active'));
      chip.classList.add('active');
      const promptKey = chip.dataset.prompt;
      if (presets[promptKey]) {
        runPreset(promptKey);
      }
    });
  });

  // Bind Send & Stop & Reset
  if (sendBtn && inputEl) {
    sendBtn.addEventListener('click', () => handleCustomQuery(inputEl.value));
    inputEl.addEventListener('keydown', (e) => {
      if (e.key === 'Enter') {
        e.preventDefault();
        handleCustomQuery(inputEl.value);
      }
    });
  }

  if (stopBtn) {
    stopBtn.addEventListener('click', stopGeneration);
  }

  if (resetBtn) {
    resetBtn.addEventListener('click', () => {
      stopGeneration();
      runPreset('python');
    });
  }

  // Model Selector interactive menu
  if (modelSelector) {
    const modelOptions = [
      { name: "Qwen 3.5 0.8B", speed: 24.5, ram: "480 MB" },
      { name: "LFM3 1.7B Instruct", speed: 18.4, ram: "532 MB" },
      { name: "DeepSeek R1 1.5B", speed: 14.8, ram: "890 MB" },
      { name: "Gemma 2 2B Instruct", speed: 12.2, ram: "1.2 GB" }
    ];
    let currentModelIdx = 0;
    modelSelector.style.cursor = 'pointer';
    modelSelector.addEventListener('click', () => {
      currentModelIdx = (currentModelIdx + 1) % modelOptions.length;
      const m = modelOptions[currentModelIdx];
      const nameEl = document.getElementById('simSelectedModelName');
      if (nameEl) nameEl.textContent = m.name;
      const ramEl = document.getElementById('dashRam');
      if (ramEl) ramEl.innerHTML = `~${m.ram.split(' ')[0]} <span class="unit">${m.ram.split(' ')[1]}</span>`;
      // Run current active chip
      const activeChip = document.querySelector('.sim-chip.active');
      const activeKey = activeChip ? activeChip.dataset.prompt : 'python';
      runPreset(activeKey);
    });
  }

  // Auto-run python preset on initial load
  setTimeout(() => {
    runPreset('python');
  }, 300);
}

/* --------------------------------------------------------------------------
   4. Model Matrix Filtering
   -------------------------------------------------------------------------- */
function initModelFilter() {
  const filterButtons = document.querySelectorAll('.filter-btn');
  const tableRows = document.querySelectorAll('.matrix-table tbody tr');

  filterButtons.forEach(btn => {
    btn.addEventListener('click', () => {
      filterButtons.forEach(b => b.classList.remove('active'));
      btn.classList.add('active');

      const filter = btn.dataset.filter;
      tableRows.forEach(row => {
        const ram = parseInt(row.dataset.ram, 10);
        if (filter === 'all') {
          row.style.display = '';
        } else if (filter === 'low' && ram <= 3) {
          row.style.display = '';
        } else if (filter === 'balanced' && ram >= 4 && ram <= 6) {
          row.style.display = '';
        } else if (filter === 'flagship' && ram >= 8) {
          row.style.display = '';
        } else {
          row.style.display = 'none';
        }
      });
    });
  });
}

/* --------------------------------------------------------------------------
   5. One-Click Copy for Terminal Commands
   -------------------------------------------------------------------------- */
function initCopyButtons() {
  const copyButtons = document.querySelectorAll('.copy-icon-btn');
  copyButtons.forEach(btn => {
    btn.addEventListener('click', () => {
      const code = btn.dataset.clipboard || btn.parentElement.querySelector('code')?.innerText;
      if (code) {
        navigator.clipboard.writeText(code.trim()).then(() => {
          const original = btn.textContent;
          btn.textContent = '✓ Copied';
          btn.style.background = 'var(--accent-red)';
          setTimeout(() => {
            btn.textContent = original;
            btn.style.background = '';
          }, 1800);
        });
      }
    });
  });
}

/* --------------------------------------------------------------------------
   6. Documentation Sidebar Active Link Spy
   -------------------------------------------------------------------------- */
function initDocsSidebar() {
  const sidebarLinks = document.querySelectorAll('.docs-nav-link');
  if (!sidebarLinks.length) return;

  const sections = document.querySelectorAll('.docs-section');
  if (!sections.length) return;

  window.addEventListener('scroll', () => {
    let currentId = '';
    const scrollPos = window.scrollY + 120;

    sections.forEach(sec => {
      const top = sec.offsetTop;
      const height = sec.offsetHeight;
      if (scrollPos >= top && scrollPos < top + height) {
        currentId = sec.getAttribute('id');
      }
    });

    if (currentId) {
      sidebarLinks.forEach(link => {
        link.classList.remove('active');
        if (link.getAttribute('href') === `#${currentId}`) {
          link.classList.add('active');
        }
      });
    }
  });
}

