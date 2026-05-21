(function () {
    const root = document.documentElement;
    const stored = localStorage.getItem('cheatsheets-theme');
    if (stored === 'dark' || stored === 'light') {
        root.setAttribute('data-theme', stored);
    }

    function applyTheme(theme) {
        root.setAttribute('data-theme', theme);
        localStorage.setItem('cheatsheets-theme', theme);
        document.querySelectorAll('[data-theme-toggle]').forEach(function (btn) {
            const isDark = theme === 'dark';
            btn.innerHTML = isDark
                ? '<i class="fa-solid fa-sun"></i>'
                : '<i class="fa-solid fa-moon"></i>';
            btn.setAttribute('aria-label', isDark ? 'Switch to light mode' : 'Switch to dark mode');
        });
    }

    document.addEventListener('click', function (e) {
        const toggle = e.target.closest('[data-theme-toggle]');
        if (!toggle) return;
        const next = root.getAttribute('data-theme') === 'dark' ? 'light' : 'dark';
        applyTheme(next);
    });

    if (stored) {
        applyTheme(stored);
    }

    var notiLink = document.querySelector('a[href$="/notifications"]');
    if (notiLink) {
        var pollUrl = notiLink.getAttribute('href').replace(/\/notifications$/, '/notifications/unread-count');
        function refreshNotiBadge() {
            fetch(pollUrl, { credentials: 'same-origin' })
                .then(function (r) { return r.ok ? r.json() : null; })
                .then(function (data) {
                    if (!data) return;
                    var badge = notiLink.querySelector('.noti-badge');
                    var count = data.count || 0;
                    if (count > 0) {
                        if (!badge) {
                            badge = document.createElement('span');
                            badge.className = 'noti-badge';
                            notiLink.appendChild(badge);
                        }
                        badge.textContent = count;
                    } else if (badge) {
                        badge.remove();
                    }
                })
                .catch(function () { });
        }
        refreshNotiBadge();
        setInterval(refreshNotiBadge, 45000);
    }
})();
