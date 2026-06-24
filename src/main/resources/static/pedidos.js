const API_PEDIDOS   = "http://localhost:8080/pedidos";
const API_CATEGORIAS = "http://localhost:8080/categorias";
const PAGE_SIZE = 10;

let tabActual = "pendientes";
let pedidosConocidos = new Set();
let paginaActual = 0;

// Estado del filtro de producto
let productoSeleccionado = "";
let productoElementoSeleccionado = null;
let categoriasDropdownCargadas = false;

// Estado del filtro de estado
let estadoSeleccionado = "";
let estadoElementoSeleccionado = null;

let filtros = {
    clienteNombre:  "",
    nombreProducto: "",
    estado:         "",
    totalMin:       "",
    totalMax:       "",
    fecha:          ""
};

document.addEventListener("DOMContentLoaded", () => {
    cargarPendientes();

    // Cerrar dropdowns al hacer click fuera
    document.addEventListener("click", (e) => {
        const productoSelect = document.getElementById("producto-select");
        if (productoSelect && !productoSelect.contains(e.target)) cerrarDropdownProducto();
        const estadoSelect = document.getElementById("estado-select");
        if (estadoSelect && !estadoSelect.contains(e.target)) cerrarDropdownEstado();
    });

    setInterval(() => {
        if (tabActual === "pendientes") {
            cargarPendientes();
        } else {
            fetchBadge();
        }
    }, 5000);
});

function mostrarTab(tab) {
    tabActual = tab;
    document.getElementById("tab-pendientes").classList.toggle("active", tab === "pendientes");
    document.getElementById("tab-historial").classList.toggle("active", tab === "historial");

    const esHistorial = tab === "historial";
    document.getElementById("panel-filtros").style.display       = esHistorial ? "block" : "none";
    document.getElementById("toolbar-historial").style.display   = esHistorial ? "flex"  : "none";
    document.getElementById("paginacion-historial").style.display = esHistorial ? "flex"  : "none";

    if (esHistorial) {
        if (!categoriasDropdownCargadas) {
            cargarCategoriasParaFiltro();
            categoriasDropdownCargadas = true;
        }
        paginaActual = 0;
        cargarHistorial();
    } else {
        cargarPendientes();
    }
}

// ─── DROPDOWN JERÁRQUICO DE PRODUCTOS ─────────────────────────

async function cargarCategoriasParaFiltro() {
    try {
        const response = await fetch(API_CATEGORIAS);
        const categorias = await response.json();
        construirDropdown(categorias);
    } catch (error) {
        console.error("Error cargando categorías para el filtro:", error);
    }
}

function construirDropdown(categorias) {
    const dropdown = document.getElementById("producto-dropdown");
    dropdown.innerHTML = "";

    const clearOpt = document.createElement("div");
    clearOpt.className = "custom-select-option clear-option";
    clearOpt.textContent = "Todos";
    clearOpt.addEventListener("click", () => seleccionarProducto("", clearOpt));
    dropdown.appendChild(clearOpt);

    categorias.forEach((cat, index) => {
        if (!cat.productos || cat.productos.length === 0) return;

        const catWrapper = document.createElement("div");

        const header = document.createElement("div");
        header.className = "category-header";
        header.innerHTML = `📂 ${cat.nombre} <span class="category-arrow" id="arrow-cat-${index}">▶</span>`;
        header.addEventListener("click", () => toggleCategoria(index));

        const productsDiv = document.createElement("div");
        productsDiv.id = `cat-products-${index}`;
        productsDiv.style.display = "none";

        cat.productos.forEach(prod => {
            const opt = document.createElement("div");
            opt.className = "custom-select-option product-option";
            opt.textContent = prod.nombre;
            opt.addEventListener("click", () => seleccionarProducto(prod.nombre, opt));
            productsDiv.appendChild(opt);
        });

        catWrapper.appendChild(header);
        catWrapper.appendChild(productsDiv);
        dropdown.appendChild(catWrapper);
    });
}

function toggleProductoDropdown() {
    const dropdown = document.getElementById("producto-dropdown");
    const isOpen = dropdown.style.display !== "none";
    if (isOpen) {
        cerrarDropdownProducto();
    } else {
        dropdown.style.display = "block";
        document.getElementById("producto-select").classList.add("open");
    }
}

function cerrarDropdownProducto() {
    const dropdown = document.getElementById("producto-dropdown");
    if (dropdown) dropdown.style.display = "none";
    const select = document.getElementById("producto-select");
    if (select) select.classList.remove("open");
}

function toggleCategoria(index) {
    const products = document.getElementById(`cat-products-${index}`);
    const arrow    = document.getElementById(`arrow-cat-${index}`);
    const isOpen   = products.style.display !== "none";
    products.style.display = isOpen ? "none" : "block";
    arrow.classList.toggle("open", !isOpen);
}

function toggleEstadoDropdown() {
    const dropdown = document.getElementById("estado-dropdown");
    const isOpen = dropdown.style.display !== "none";
    if (isOpen) {
        cerrarDropdownEstado();
    } else {
        dropdown.style.display = "block";
        document.getElementById("estado-select").classList.add("open");
    }
}

function cerrarDropdownEstado() {
    const dropdown = document.getElementById("estado-dropdown");
    if (dropdown) dropdown.style.display = "none";
    const select = document.getElementById("estado-select");
    if (select) select.classList.remove("open");
}

function seleccionarEstado(valor, elemento) {
    estadoSeleccionado = valor;
    document.getElementById("estado-label").textContent = elemento.textContent;

    if (estadoElementoSeleccionado) estadoElementoSeleccionado.classList.remove("selected");
    if (valor) {
        elemento.classList.add("selected");
        estadoElementoSeleccionado = elemento;
    } else {
        estadoElementoSeleccionado = null;
    }

    cerrarDropdownEstado();
}

function seleccionarProducto(nombre, elemento) {
    productoSeleccionado = nombre;
    document.getElementById("producto-label").textContent = nombre || "Todos";

    if (productoElementoSeleccionado) {
        productoElementoSeleccionado.classList.remove("selected");
    }
    if (nombre && elemento) {
        elemento.classList.add("selected");
        productoElementoSeleccionado = elemento;
    } else {
        productoElementoSeleccionado = null;
    }

    cerrarDropdownProducto();
}

// ─── PENDIENTES ───────────────────────────────────────────────

async function cargarPendientes() {
    try {
        const response = await fetch(`${API_PEDIDOS}/pendientes`);
        const pedidos  = await response.json();
        actualizarBadge(pedidos.length);
        detectarNuevos(pedidos);
        renderPedidos(pedidos);
    } catch (error) {
        console.error("Error al cargar pedidos:", error);
        document.getElementById("contenido-pedidos").innerHTML =
            `<p class="msg-espera">No se pudo conectar con el servidor.</p>`;
    }
}

async function fetchBadge() {
    try {
        const response = await fetch(`${API_PEDIDOS}/pendientes`);
        const pedidos  = await response.json();
        actualizarBadge(pedidos.length);
        detectarNuevos(pedidos);
    } catch (_) {}
}

// ─── HISTORIAL ────────────────────────────────────────────────

async function cargarHistorial() {
    try {
        const params = new URLSearchParams({
            page:           paginaActual,
            size:           PAGE_SIZE,
            clienteNombre:  filtros.clienteNombre,
            nombreProducto: filtros.nombreProducto,
            estado:         filtros.estado,
            fecha:          filtros.fecha
        });
        if (filtros.totalMin !== "") params.append("totalMin", filtros.totalMin);
        if (filtros.totalMax !== "") params.append("totalMax", filtros.totalMax);

        const response = await fetch(`${API_PEDIDOS}/historial?${params}`);
        if (!response.ok) throw new Error(`HTTP ${response.status}`);
        const data = await response.json();

        renderPedidos(data.content);
        renderContador(data.numberOfElements, data.totalElements);
        renderPaginacion(data.number, data.totalPages);
    } catch (error) {
        console.error("Error al cargar historial:", error);
        document.getElementById("contenido-pedidos").innerHTML =
            `<p class="msg-espera" style="color:var(--red)">Error: ${error.message}</p>`;
    }
}

function aplicarFiltros() {
    filtros.clienteNombre  = document.getElementById("f-cliente").value.trim();
    filtros.nombreProducto = productoSeleccionado;
    filtros.estado         = estadoSeleccionado;
    filtros.totalMin       = document.getElementById("f-total-min").value;
    filtros.totalMax       = document.getElementById("f-total-max").value;
    filtros.fecha          = document.getElementById("f-fecha").value;
    paginaActual = 0;
    cargarHistorial();
}

function limpiarFiltros() {
    document.getElementById("f-cliente").value   = "";
    document.getElementById("f-total-min").value = "";
    document.getElementById("f-total-max").value = "";
    document.getElementById("f-fecha").value     = "";
    seleccionarProducto("", null);
    seleccionarEstado("", { textContent: "Todos" });
    filtros = { clienteNombre: "", nombreProducto: "", estado: "", totalMin: "", totalMax: "", fecha: "" };
    paginaActual = 0;
    cargarHistorial();
}

// ─── RENDER ───────────────────────────────────────────────────

function renderPedidos(pedidos) {
    const contenedor = document.getElementById("contenido-pedidos");

    if (!pedidos || pedidos.length === 0) {
        const msg = tabActual === "pendientes"
            ? "No hay pedidos pendientes. Esperando clientes de Telegram..."
            : "No se encontraron pedidos con esos filtros.";
        contenedor.innerHTML = `<p class="msg-espera">${msg}</p>`;
        return;
    }

    contenedor.innerHTML = "";
    pedidos.forEach(pedido => contenedor.appendChild(crearCard(pedido)));
}

function renderContador(mostradas, total) {
    const el = document.getElementById("historial-contador");
    if (total === 0) {
        el.textContent = "Sin resultados";
    } else {
        const inicio = paginaActual * PAGE_SIZE + 1;
        const fin    = paginaActual * PAGE_SIZE + mostradas;
        el.textContent = `Mostrando ${inicio}–${fin} de ${total} pedido${total !== 1 ? "s" : ""}`;
    }
}

function renderPaginacion(current, totalPaginas) {
    const container = document.getElementById("paginacion-historial");
    container.innerHTML = "";
    if (totalPaginas <= 1) return;

    const addBtn = (i) => {
        const btn = document.createElement("button");
        btn.className = "btn-pag" + (i === current ? " active" : "");
        btn.textContent = i + 1;
        btn.addEventListener("click", () => irAPagina(i));
        container.appendChild(btn);
    };

    const addEllipsis = () => {
        const span = document.createElement("span");
        span.className = "pag-ellipsis";
        span.textContent = "…";
        container.appendChild(span);
    };

    const btnPrev = document.createElement("button");
    btnPrev.className = "btn-pag";
    btnPrev.textContent = "←";
    btnPrev.disabled = current === 0;
    btnPrev.addEventListener("click", () => irAPagina(current - 1));
    container.appendChild(btnPrev);

    const visible = new Set([0, totalPaginas - 1]);
    for (let i = Math.max(0, current - 2); i <= Math.min(totalPaginas - 1, current + 2); i++) {
        visible.add(i);
    }

    let ultimo = -1;
    for (const pagina of [...visible].sort((a, b) => a - b)) {
        if (ultimo !== -1 && pagina > ultimo + 1) addEllipsis();
        addBtn(pagina);
        ultimo = pagina;
    }

    const btnNext = document.createElement("button");
    btnNext.className = "btn-pag";
    btnNext.textContent = "→";
    btnNext.disabled = current === totalPaginas - 1;
    btnNext.addEventListener("click", () => irAPagina(current + 1));
    container.appendChild(btnNext);
}

function irAPagina(pagina) {
    paginaActual = pagina;
    cargarHistorial();
    window.scrollTo({ top: 0, behavior: "smooth" });
}

// ─── CARD ─────────────────────────────────────────────────────

function crearCard(pedido) {
    const card = document.createElement("div");
    const esPendiente = pedido.estado === "PENDIENTE";
    const estadoClass = esPendiente ? "pedido-pendiente"
        : pedido.estado === "CONFIRMADO" ? "pedido-confirmado"
        : "pedido-cancelado";
    card.className = `pedido-card ${estadoClass}`;

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
                <span class="pedido-estado ${esPendiente ? 'estado-pendiente' : pedido.estado === 'CONFIRMADO' ? 'estado-confirmado' : 'estado-cancelado'}">${pedido.estado}</span>
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
                ? `<button class="btn-confirmar" onclick="atenderPedido(${pedido.id}, 'CONFIRMADO')">Confirmar</button>`
                +  `<button class="btn-cancelar" onclick="atenderPedido(${pedido.id}, 'CANCELADO')">Cancelar</button>`
                : `<span class="${pedido.estado === 'CONFIRMADO' ? 'texto-confirmado' : 'texto-cancelado'}">Pedido ${pedido.estado}</span>`}
        </div>
    `;

    return card;
}

// ─── ACCIONES ─────────────────────────────────────────────────

async function atenderPedido(id, estado) {
    try {
        await fetch(`${API_PEDIDOS}/${id}/estado?estado=${estado}`, { method: "PUT" });
        pedidosConocidos.delete(id);
        if (tabActual === "pendientes") {
            cargarPendientes();
        } else {
            cargarHistorial();
        }
    } catch (error) {
        console.error("Error al atender pedido:", error);
    }
}

function detectarNuevos(pedidos) {
    pedidos.forEach(p => {
        if (!pedidosConocidos.has(p.id)) {
            if (pedidosConocidos.size > 0) parpadeaTitulo();
            pedidosConocidos.add(p.id);
        }
    });
}

function parpadeaTitulo() {
    const original = document.title;
    let count = 0;
    const intervalo = setInterval(() => {
        document.title = count % 2 === 0 ? "¡NUEVO PEDIDO!" : original;
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
