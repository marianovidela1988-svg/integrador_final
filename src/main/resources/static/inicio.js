document.addEventListener("DOMContentLoaded", () => {
    // Redireccion a stock
    const btnStock = document.getElementById("btn-stock");
    if (btnStock) {
        btnStock.onclick = () => window.location.href = "stock.html";
    }

    // Redireccion a pedidos
    const btnPedidos = document.getElementById("btn-pedidos");
    if (btnPedidos) {
        btnPedidos.onclick = () => window.location.href = "pedidos.html";
    }
});