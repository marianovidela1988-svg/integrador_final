document.addEventListener("DOMContentLoaded", () => {
    const welcomeEl = document.getElementById("welcome-msg");
    if (welcomeEl) {
        document.addEventListener("auth-ready", (e) => {
            welcomeEl.textContent = `Bienvenido, ${e.detail.user}`;
        });
    }

    const btnStock = document.getElementById("btn-stock");
    if (btnStock) btnStock.onclick = () => window.location.href = "stock.html";

    const btnPedidos = document.getElementById("btn-pedidos");
    if (btnPedidos) btnPedidos.onclick = () => window.location.href = "pedidos.html";
});
