// Java 0-to-Hero dochub — client-side interactivity.
// Ported from the KGK Flutter dochub:
//   theme toggle, progress bar, rendered/raw toggle, sidebar filter,
//   TOC scroll-spy, code copy buttons, hero search, Mermaid lazy-load.

// ---------- Theme toggle (auto / light / dark) ----------
(function () {
  var btn = document.getElementById('theme-toggle');
  if (!btn) return;
  var icon = btn.querySelector('.tt-icon');
  var label = btn.querySelector('.tt-label');
  var states = ['auto', 'light', 'dark'];
  var icons  = { auto: '◐', light: '☀', dark: '☾' };
  var labels = { auto: 'Auto',  light: 'Light', dark: 'Dark' };

  function current() {
    try {
      var v = localStorage.getItem('dochub-theme');
      return v && states.indexOf(v) !== -1 ? v : 'auto';
    } catch (e) { return 'auto'; }
  }
  function apply(t) {
    if (t === 'auto') document.documentElement.removeAttribute('data-theme');
    else document.documentElement.dataset.theme = t;
    try { localStorage.setItem('dochub-theme', t); } catch (e) {}
    if (icon)  icon.textContent  = icons[t];
    if (label) label.textContent = labels[t];
    btn.title = 'Theme: ' + t + ' (click to cycle)';
  }
  apply(current());

  btn.addEventListener('click', function () {
    var c = current();
    var next = states[(states.indexOf(c) + 1) % states.length];
    apply(next);
  });
})();

// ---------- Reading progress bar ----------
(function () {
  var bar = document.querySelector('.progress-bar .fill');
  if (!bar) return;
  function update() {
    var doc = document.documentElement;
    var scrolled = doc.scrollTop || document.body.scrollTop;
    var max = (doc.scrollHeight - doc.clientHeight);
    var pct = max > 0 ? (scrolled / max) * 100 : 0;
    bar.style.width = pct + '%';
  }
  document.addEventListener('scroll', update, { passive: true });
  update();
})();

// ---------- Rendered / Raw toggle ----------
(function () {
  var toggle = document.querySelector('.view-toggle');
  if (!toggle) return;
  toggle.addEventListener('click', function (e) {
    var btn = e.target.closest('button[data-view]');
    if (!btn) return;
    var view = btn.dataset.view;
    document.body.classList.toggle('raw-mode', view === 'raw');
    toggle.querySelectorAll('button').forEach(function (b) {
      b.classList.toggle('active', b.dataset.view === view);
    });
  });
})();

// ---------- Sidebar search filter ----------
(function () {
  var input = document.getElementById('side-search');
  if (!input) return;
  var groups = document.querySelectorAll('.sidebar .nav-section');
  function filter() {
    var q = (input.value || '').trim().toLowerCase();
    groups.forEach(function (g) {
      var matchAny = false;
      g.querySelectorAll('[data-q]').forEach(function (n) {
        var hay = n.dataset.q || '';
        var ok = !q || hay.indexOf(q) !== -1;
        n.style.display = ok ? '' : 'none';
        if (ok) matchAny = true;
      });
      g.style.display = matchAny || !q ? '' : 'none';
      if (q) {
        g.querySelectorAll('details.mod').forEach(function (d) {
          var anyVisible = Array.from(d.querySelectorAll('.pages [data-q]'))
            .some(function (n) { return n.style.display !== 'none'; });
          if (anyVisible) d.setAttribute('open', '');
        });
      }
    });
  }
  input.addEventListener('input', filter);
})();

// ---------- TOC scroll spy (IntersectionObserver) ----------
(function () {
  var toc = document.querySelector('.toc-rail');
  if (!toc) return;
  var links = Array.from(toc.querySelectorAll('a[data-toc-link]'));
  if (!links.length) return;

  var idToLink = {};
  var targets = [];
  links.forEach(function (a) {
    var id = a.getAttribute('href').slice(1);
    var el = document.getElementById(id);
    if (el) { idToLink[id] = a; targets.push(el); }
  });
  if (!targets.length) return;

  var visible = new Set();
  function setActive(id) {
    links.forEach(function (a) { a.classList.remove('active'); });
    var a = idToLink[id];
    if (a) {
      a.classList.add('active');
      var r = a.getBoundingClientRect();
      var rr = toc.getBoundingClientRect();
      if (r.top < rr.top + 20 || r.bottom > rr.bottom - 20) {
        a.scrollIntoView({ block: 'nearest' });
      }
    }
  }

  var io = new IntersectionObserver(function (entries) {
    entries.forEach(function (e) {
      if (e.isIntersecting) visible.add(e.target.id);
      else visible.delete(e.target.id);
    });
    var top = null;
    var topY = Infinity;
    targets.forEach(function (t) {
      if (!visible.has(t.id)) return;
      var y = t.getBoundingClientRect().top;
      if (y < topY) { topY = y; top = t.id; }
    });
    if (top) {
      setActive(top);
    } else {
      var best = null, bestY = -Infinity;
      targets.forEach(function (t) {
        var y = t.getBoundingClientRect().top;
        if (y <= 100 && y > bestY) { bestY = y; best = t.id; }
      });
      if (best) setActive(best);
    }
  }, { rootMargin: '-90px 0px -65% 0px', threshold: [0, 1] });

  targets.forEach(function (t) { io.observe(t); });

  window.requestAnimationFrame(function () {
    var best = targets[0].id, bestY = Infinity;
    targets.forEach(function (t) {
      var y = Math.abs(t.getBoundingClientRect().top - 120);
      if (y < bestY) { bestY = y; best = t.id; }
    });
    setActive(best);
  });
})();

// ---------- Copy buttons on code blocks ----------
(function () {
  document.querySelectorAll('.code-copy').forEach(function (btn) {
    btn.addEventListener('click', function () {
      var block = btn.closest('.code-block');
      if (!block) return;
      var code = block.querySelector('pre');
      if (!code) return;
      var text = code.innerText;
      if (navigator.clipboard && navigator.clipboard.writeText) {
        navigator.clipboard.writeText(text).then(function () {
          btn.classList.add('copied');
          var prev = btn.textContent;
          btn.textContent = 'Copied';
          setTimeout(function () { btn.textContent = prev; btn.classList.remove('copied'); }, 1500);
        });
      } else {
        var ta = document.createElement('textarea');
        ta.value = text; document.body.appendChild(ta); ta.select();
        try { document.execCommand('copy'); } catch (e) {}
        document.body.removeChild(ta);
      }
    });
  });
})();

// ---------- Hero search (landing page only) ----------
(function () {
  var box = document.getElementById('hero-search');
  if (!box) return;
  var input  = document.getElementById('docs-search');
  var panel  = document.getElementById('docs-search-panel');
  var list   = document.getElementById('docs-search-results');
  var status = document.getElementById('docs-search-status');

  var index = null;
  var loading = false;
  var lastResults = [];
  var activeIdx = -1;

  function loadIndex() {
    if (index || loading) return;
    loading = true;
    fetch('assets/search-index.json', { cache: 'force-cache' })
      .then(function (r) { return r.json(); })
      .then(function (data) { index = data; loading = false; render(); })
      .catch(function () {
        loading = false;
        if (status) { status.textContent = 'Could not load search index.'; status.classList.remove('hidden'); }
      });
  }

  function tokens(q) {
    return (q || '').toLowerCase().trim().split(/\s+/).filter(Boolean);
  }

  function score(item, toks, query) {
    if (!toks.length) return 0;
    var t = item.t.toLowerCase(), l = item.l.toLowerCase(),
        p = item.p.toLowerCase(), b = item.b.toLowerCase();
    var s = 0;
    for (var i = 0; i < toks.length; i++) {
      var tok = toks[i];
      var hitTitle = t.indexOf(tok) !== -1;
      var hitLabel = l.indexOf(tok) !== -1;
      var hitPath  = p.indexOf(tok) !== -1;
      var hitBody  = b.indexOf(tok) !== -1;
      if (!(hitTitle || hitLabel || hitPath || hitBody)) return 0;
      if (hitTitle) s += 12;
      if (hitLabel) s += 10;
      if (hitPath)  s += 5;
      if (hitBody)  s += 2;
      if (t.indexOf(tok) === 0) s += 6;
      if (l.indexOf(tok) === 0) s += 4;
    }
    var phrase = (query || '').toLowerCase().trim();
    if (phrase.length > 2) {
      if (t.indexOf(phrase) !== -1) s += 8;
      if (b.indexOf(phrase) !== -1) s += 4;
    }
    return s;
  }

  function escapeHtml(s) {
    return String(s).replace(/[&<>"']/g, function (c) {
      return ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' })[c];
    });
  }
  function highlight(text, toks) {
    if (!toks.length || !text) return escapeHtml(text || '');
    var safe = escapeHtml(text);
    var sorted = toks.slice().sort(function (a, b) { return b.length - a.length; });
    var re = new RegExp('(' + sorted.map(function (t) { return t.replace(/[.*+?^${}()|[\]\\]/g, '\\$&'); }).join('|') + ')', 'gi');
    return safe.replace(re, '<mark>$1</mark>');
  }
  function snippetFor(body, toks) {
    if (!body) return '';
    if (!toks.length) return body.slice(0, 180) + (body.length > 180 ? '…' : '');
    var low = body.toLowerCase();
    var pos = -1;
    for (var i = 0; i < toks.length; i++) {
      var k = low.indexOf(toks[i]);
      if (k !== -1 && (pos === -1 || k < pos)) pos = k;
    }
    if (pos === -1) return body.slice(0, 180) + (body.length > 180 ? '…' : '');
    var start = Math.max(0, pos - 60);
    var end = Math.min(body.length, pos + 140);
    var pre = (start > 0 ? '…' : '');
    var post = (end < body.length ? '…' : '');
    return pre + body.slice(start, end) + post;
  }

  function render() {
    if (!input) return;
    var q = input.value;
    var toks = tokens(q);
    if (!q.trim()) {
      panel.hidden = true;
      lastResults = []; activeIdx = -1;
      return;
    }
    if (!index) {
      panel.hidden = false;
      list.innerHTML = '';
      status.textContent = loading ? 'Loading index…' : 'Type to search.';
      status.classList.remove('hidden');
      return;
    }
    var matches = [];
    for (var i = 0; i < index.length; i++) {
      var sc = score(index[i], toks, q);
      if (sc > 0) matches.push({ it: index[i], s: sc });
    }
    matches.sort(function (a, b) { return b.s - a.s; });
    var top = matches.slice(0, 30);
    lastResults = top;
    activeIdx = top.length ? 0 : -1;

    panel.hidden = false;
    if (!top.length) {
      list.innerHTML = '';
      status.textContent = 'No matches for “' + escapeHtml(q) + '”.';
      status.classList.remove('hidden');
      return;
    }
    status.classList.add('hidden');
    var html = '';
    for (var j = 0; j < top.length; j++) {
      var it = top[j].it;
      var active = j === 0 ? ' active' : '';
      html += (
        '<li role="option">' +
          '<a class="hero-search-result tone-' + escapeHtml(it.co) + active + '" href="' + escapeHtml(it.u) + '" data-idx="' + j + '">' +
            '<span class="res-ic">' + escapeHtml(it.ci) + '</span>' +
            '<span class="res-body">' +
              '<span class="res-top">' +
                '<span class="res-chapter">' + escapeHtml(it.c) + '</span>' +
                '<span class="res-label">' + highlight(it.l, toks) + '</span>' +
              '</span>' +
              '<div class="res-title">' + highlight(it.t, toks) + '</div>' +
              '<div class="res-snippet">' + highlight(snippetFor(it.b, toks), toks) + '</div>' +
            '</span>' +
            '<span class="res-go">→</span>' +
          '</a>' +
        '</li>'
      );
    }
    list.innerHTML = html;
  }

  function setActive(i) {
    var rows = list.querySelectorAll('.hero-search-result');
    if (!rows.length) return;
    if (i < 0) i = rows.length - 1;
    if (i >= rows.length) i = 0;
    rows.forEach(function (r) { r.classList.remove('active'); });
    rows[i].classList.add('active');
    rows[i].scrollIntoView({ block: 'nearest' });
    activeIdx = i;
  }
  function openActive() {
    var rows = list.querySelectorAll('.hero-search-result');
    if (activeIdx >= 0 && rows[activeIdx]) window.location.href = rows[activeIdx].getAttribute('href');
  }
  function close() { panel.hidden = true; input.blur(); }

  input.addEventListener('focus', loadIndex);
  input.addEventListener('input', render);
  input.addEventListener('keydown', function (e) {
    if (e.key === 'ArrowDown') { e.preventDefault(); setActive(activeIdx + 1); }
    else if (e.key === 'ArrowUp') { e.preventDefault(); setActive(activeIdx - 1); }
    else if (e.key === 'Enter') { e.preventDefault(); openActive(); }
    else if (e.key === 'Escape') { input.value = ''; close(); }
  });

  document.addEventListener('click', function (e) {
    if (!box.contains(e.target)) panel.hidden = true;
  });

  // "/" keyboard shortcut to focus the search
  document.addEventListener('keydown', function (e) {
    if (e.key === '/' && document.activeElement !== input
        && !/(INPUT|TEXTAREA|SELECT)/.test((document.activeElement || {}).tagName || '')) {
      e.preventDefault();
      input.focus();
    }
  });
})();

// ---------- Mermaid (lazy CDN load only if mermaid blocks exist) ----------
(function () {
  if (!document.querySelector('.mermaid')) return;
  var s = document.createElement('script');
  s.src = 'https://cdn.jsdelivr.net/npm/mermaid@11.4.1/dist/mermaid.min.js';
  s.crossOrigin = 'anonymous';
  s.onload = function () {
    try {
      var dark = window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches;
      var forced = document.documentElement.dataset.theme;
      if (forced === 'dark') dark = true;
      if (forced === 'light') dark = false;
      window.mermaid.initialize({ startOnLoad: true, theme: dark ? 'dark' : 'default', securityLevel: 'loose' });
      window.mermaid.run();
    } catch (e) {}
  };
  s.onerror = function () {
    document.querySelectorAll('.mermaid').forEach(function (n) {
      var pre = document.createElement('pre');
      pre.textContent = n.textContent;
      pre.style.background = 'var(--code-bg)';
      pre.style.padding = '14px';
      pre.style.borderRadius = '10px';
      n.replaceWith(pre);
    });
  };
  document.head.appendChild(s);
})();
