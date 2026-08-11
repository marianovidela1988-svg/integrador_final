(function () {
    fetch('/admin/session')
        .then(response => {
            if (!response.ok) throw new Error('No autenticado');
            return response.json();
        })
        .then(data => {
            window.ADMIN_USER = data.user;
            document.body.classList.remove('auth-pending');
            document.dispatchEvent(new CustomEvent('auth-ready', { detail: { user: data.user } }));
        })
        .catch(() => {
            window.location.replace('login.html');
        });

    window.cerrarSesion = function () {
        fetch('/admin/logout', { method: 'POST' })
            .finally(() => window.location.replace('login.html'));
    };

    document.addEventListener('DOMContentLoaded', () => {
        const btnLogout = document.getElementById('btn-logout');
        if (btnLogout) btnLogout.addEventListener('click', window.cerrarSesion);
    });
})();
