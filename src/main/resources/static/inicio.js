document.addEventListener("DOMContentLoaded", () => {
    const user = localStorage.getItem("adminUser");
    const welcomeEl = document.getElementById("welcome-msg");
    if (welcomeEl && user) welcomeEl.textContent = `Bienvenido, ${user}`;

    const btnStock = document.getElementById("btn-stock");
    if (btnStock) btnStock.onclick = () => window.location.href = "stock.html";

    const btnPedidos = document.getElementById("btn-pedidos");
    if (btnPedidos) btnPedidos.onclick = () => window.location.href = "pedidos.html";
});
