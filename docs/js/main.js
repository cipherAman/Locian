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
    if (themeIcon) {
      themeIcon.textContent = theme === 'dark' ? '☀️' : '🌙';
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
  const startBtn = document.getElementById('simStartBtn');
  const stopBtn = document.getElementById('simStopBtn');
  const promptSelect = document.getElementById('simPromptSelect');
  const assistantBubble = document.getElementById('simAssistantBubble');
  const telemetryPill = document.getElementById('simTelemetryPill');
  const tokSpeedEl = document.getElementById('simTokSpeed');
  const ttftEl = document.getElementById('simTtft');
  const totalTokensEl = document.getElementById('simTotalTokens');
  const statusIndicator = document.getElementById('simStatusIndicator');

  let streamTimer = null;
  let isGenerating = false;

  const responses = {
    python: {
      text: `Sure, here is a clean Python script using asyncio to implement an in-memory asynchronous task queue:`,
      code: `import asyncio\nfrom typing import Any\n\nclass TaskQueue:\n    def __init__(self, max_concurrency: int = 3):\n        self.queue = asyncio.Queue()\n        self.semaphore = asyncio.Semaphore(max_concurrency)\n\n    async def enqueue(self, task_name: str, payload: Any):\n        await self.queue.put((task_name, payload))\n        print(f"📦 [Queued] {task_name}")\n\n    async def worker(self, worker_id: int):\n        while True:\n            task_name, payload = await self.queue.get()\n            async with self.semaphore:\n                print(f"⚡ [Worker {worker_id}] Executing: {task_name}")\n                await asyncio.sleep(0.5)\n            self.queue.task_done()`,
      tokens: 142,
      ttft: 385,
      speed: 18.2
    },
    quantum: {
      text: `Quantum computing leverages the principles of quantum mechanics—specifically superposition and entanglement—to process information differently than classical binary computers. While a classical bit is either 0 or 1, a qubit can exist in a superposition of both states simultaneously until measured, enabling massive parallelism for specific mathematical problems like prime factorization and molecular simulation.`,
      code: ``,
      tokens: 78,
      ttft: 290,
      speed: 21.5
    },
    flow: {
      text: `In Kotlin, Flow is a cold asynchronous data stream that produces values sequentially:`,
      code: `import kotlinx.coroutines.flow.*\nimport kotlinx.coroutines.runBlocking\n\nfun tokenStream(): Flow<String> = flow {\n    val tokens = listOf("Locian", " runs", " 100%", " offline", " on-device!")\n    for (token in tokens) {\n        emit(token)\n        kotlinx.coroutines.delay(120)\n    }\n}\n\nfun main() = runBlocking {\n    tokenStream()\n        .collect { print(it) }\n}`,
      tokens: 96,
      ttft: 320,
      speed: 19.8
    }
  };

  if (!startBtn) return;

  startBtn.addEventListener('click', () => {
    if (isGenerating) return;
    const selectedKey = promptSelect ? promptSelect.value : 'python';
    const target = responses[selectedKey] || responses.python;

    isGenerating = true;
    startBtn.style.display = 'none';
    if (stopBtn) stopBtn.style.display = 'inline-flex';
    if (statusIndicator) statusIndicator.textContent = 'Generating tokens via llama.cpp...';
    if (telemetryPill) telemetryPill.style.display = 'none';

    // Clear previous output
    assistantBubble.innerHTML = '<span class="typing-cursor">▌</span>';

    const fullContent = target.text + (target.code ? '\n\n```python\n' + target.code + '\n```' : '');
    let charIndex = 0;
    const startTime = performance.now();

    streamTimer = setInterval(() => {
      charIndex += 4;
      if (charIndex >= fullContent.length) {
        charIndex = fullContent.length;
        clearInterval(streamTimer);
        isGenerating = false;
        startBtn.style.display = 'inline-flex';
        if (stopBtn) stopBtn.style.display = 'none';
        if (statusIndicator) statusIndicator.textContent = 'Ready (Idle)';

        renderFormattedMessage(assistantBubble, target.text, target.code);

        // Show telemetry pill only upon completion!
        if (telemetryPill) {
          telemetryPill.style.display = 'inline-flex';
          if (tokSpeedEl) tokSpeedEl.textContent = `${target.speed} tok/s`;
          if (ttftEl) ttftEl.textContent = `${target.ttft} ms`;
          if (totalTokensEl) totalTokensEl.textContent = `${target.tokens} tok`;
        }
      } else {
        const partial = fullContent.slice(0, charIndex);
        assistantBubble.textContent = partial;
      }
    }, 28);
  });

  if (stopBtn) {
    stopBtn.addEventListener('click', () => {
      if (streamTimer) clearInterval(streamTimer);
      isGenerating = false;
      startBtn.style.display = 'inline-flex';
      stopBtn.style.display = 'none';
      if (statusIndicator) statusIndicator.textContent = 'Cancelled by user';
    });
  }

  function renderFormattedMessage(container, text, code) {
    container.innerHTML = '';
    const p = document.createElement('p');
    p.textContent = text;
    container.appendChild(p);

    if (code) {
      const codeBox = document.createElement('div');
      codeBox.className = 'code-container';
      codeBox.innerHTML = `
        <div class="code-titlebar">
          <div class="window-dots">
            <span class="window-dot dot-red"></span>
            <span class="window-dot dot-yellow"></span>
            <span class="window-dot dot-green"></span>
          </div>
          <span class="code-lang">PYTHON</span>
          <button class="copy-btn" onclick="navigator.clipboard.writeText(this.closest('.code-container').querySelector('code').innerText); this.textContent='Copied!'; setTimeout(() => this.textContent='Copy', 1500)">Copy</button>
        </div>
        <div class="code-body">
          <pre><code>${highlightSyntax(code)}</code></pre>
        </div>
      `;
      container.appendChild(codeBox);
    }
  }

  function highlightSyntax(code) {
    // Quick regex syntax highlight for Python demo
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

