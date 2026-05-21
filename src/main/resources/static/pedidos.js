const API_PEDIDOS = "http://localhost:8080/pedidos";
let tabActual = "pendientes";
let pedidosConocidos = new Set();

document.addEventListener("DOMContentLoaded", () => {
    cargarPedidos();
    setInterval(cargarPedidos, 5000);
});

function mostrarTab(tab) {
    tabActual = tab;
    document.getElementById("tab-pendientes").classList.toggle("active", tab === "pendientes");
    document.getElementById("tab-historial").classList.toggle("active", tab === "historial");
    cargarPedidos();
}

async function cargarPedidos() {
    try {
        const url = tabActual === "pendientes"
            ? `${API_PEDIDOS}/pendientes`
            : API_PEDIDOS;
        const response = await fetch(url);
        const pedidos = await response.json();

        if (tabActual === "pendientes") {
            actualizarBadge(pedidos.length);
            detectarNuevos(pedidos);
        }

        renderPedidos(pedidos);
    } catch (error) {
        console.error("Error al cargar pedidos:", error);
        document.getElementById("contenido-pedidos").innerHTML =
            `<p class="msg-espera">No se pudo conectar con el servidor.</p>`;
    }
}

function detectarNuevos(pedidos) {
    pedidos.forEach(p => {
        if (!pedidosConocidos.has(p.id)) {
            if (pedidosConocidos.size > 0) {
                parpadeaTitulo();
            }
            pedidosConocidos.add(p.id);
        }
    });
}

function parpadeaTitulo() {
    const original = document.title;
    let count = 0;
    const intervalo = setInterval(() => {
        document.title = count % 2 === 0 ? "NUEVO PEDIDO" : original;
        count++;
        if (count > 10) {
            clearInterval(intervalo);
            document.title = original;
        }
    }, 500);
}

function actualizarBadge(cantidad) {
    const badge = document.getElementById("badge-pendientes");
    badge.style.display = cantidad > 0 ? "inline-block" : "none";
    badge.textContent = cantidad;
}

function renderPedidos(pedidos) {
    const contenedor = document.getElementById("contenido-pedidos");

    if (!pedidos || pedidos.length === 0) {
        const msg = tabActual === "pendientes"
            ? "No hay pedidos pendientes. Esperando clientes de Telegram..."
            : "No hay pedidos en el historial.";
        contenedor.innerHTML = `<p class="msg-espera">${msg}</p>`;
        return;
    }

    contenedor.innerHTML = "";
    pedidos.forEach(pedido => contenedor.appendChild(crearCard(pedido)));
}

function crearCard(pedido) {
    const card = document.createElement("div");
    const esPendiente = pedido.estado === "PENDIENTE";
    card.className = `pedido-card ${esPendiente ? "pedido-pendiente" : "pedido-atendido"}`;

    const fechaFormateada = pedido.fechaHora
        ? new Date(pedido.fechaHora).toLocaleString("es-AR")
        : "Sin fecha";

    const itemsHTML = pedido.items.map(item => `
        <tr>
            <td>${item.nombre}</td>
            <td class="text-center">${item.cantidad}</td>
            <td class="text-right">$${item.precio.toFixed(2)}</td>
            <td class="text-right">$${item.subtotal.toFixed(2)}</td>
        </tr>
    `).join("");

    card.innerHTML = `
        <div class="pedido-header">
            <div>
                <span class="pedido-estado ${esPendiente ? 'estado-pendiente' : 'estado-atendido'}">${pedido.estado}</span>
                <strong class="pedido-cliente"> ${pedido.clienteNombre}</strong>
                <span class="pedido-chatid">Chat ID: ${pedido.chatId}</span>
            </div>
            <span class="pedido-fecha">${fechaFormateada}</span>
        </div>

        <table class="tabla-items">
            <thead>
                <tr>
                    <th>Producto</th>
                    <th class="text-center">Cantidad</th>
                    <th class="text-right">Precio unit.</th>
                    <th class="text-right">Subtotal</th>
                </tr>
            </thead>
            <tbody>${itemsHTML}</tbody>
        </table>

        <div class="pedido-footer">
            <strong class="pedido-total">TOTAL: $${pedido.total.toFixed(2)}</strong>
            ${esPendiente
                ? `<button class="btn-atender" onclick="atenderPedido(${pedido.id})">Marcar como Atendido</button>`
                : `<span class="texto-atendido">Pedido atendido</span>`}
        </div>
    `;

    return card;
}

async function atenderPedido(id) {
    try {
        await fetch(`${API_PEDIDOS}/${id}/atender`, { method: 'PUT' });
        pedidosConocidos.delete(id);
        cargarPedidos();
    } catch (error) {
        console.error("Error al atender pedido:", error);
    }
}
