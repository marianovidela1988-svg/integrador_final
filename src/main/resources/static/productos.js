const urlParams = new URLSearchParams(window.location.search);
const categoriaId = urlParams.get("categoriaId");
const API_CAT  = "http://localhost:8080/categorias";
const API_PROD = "http://localhost:8080/productos";
const PAGE_SIZE = 12;

let editandoId = null;
let paginaActual = 0;
let filtroNombre = "";

document.addEventListener("DOMContentLoaded", () => {
    if (!categoriaId) {
        window.location.href = "stock.html";
        return;
    }

    fetchNombreCategoria();
    fetchProductos();

    document.getElementById("btn-save-prod").addEventListener("click", guardarProducto);

    document.getElementById("filter-producto").addEventListener("input", (e) => {
        filtroNombre = e.target.value.trim();
        paginaActual = 0;
        fetchProductos();
    });
});

// 1. Obtener solo el nombre de la categoría para el título
async function fetchNombreCategoria() {
    try {
        const response = await fetch(`${API_CAT}/${categoriaId}`);
        const categoria = await response.json();
        document.getElementById("categoria-titulo").innerText = `Categoría: ${categoria.nombre}`;
    } catch (error) {
        console.error("Error cargando categoría:", error);
    }
}

// 2. Obtener productos paginados
async function fetchProductos() {
    try {
        const params = new URLSearchParams({ page: paginaActual, size: PAGE_SIZE, nombre: filtroNombre });
        const response = await fetch(`${API_PROD}/categoria/${categoriaId}/paginados?${params}`);
        if (!response.ok) throw new Error(`HTTP ${response.status}`);
        const data = await response.json();
        renderProductos(data.content);
        renderContador(data.numberOfElements, data.totalElements);
        renderPaginacion(data.number, data.totalPages);
    } catch (error) {
        console.error("Error al cargar productos:", error);
        document.getElementById("lista-productos").innerHTML =
            `<tr><td colspan="5" style="color:var(--red)">Error al cargar: ${error.message}</td></tr>`;
    }
}

function renderProductos(productos) {
    const tabla = document.getElementById("lista-productos");
    tabla.innerHTML = "";

    if (!productos || productos.length === 0) {
        tabla.innerHTML = "<tr><td colspan='5' style='color:var(--text-muted)'>No hay productos que coincidan.</td></tr>";
        return;
    }

    productos.forEach(p => {
        const row = document.createElement("tr");
        row.innerHTML = `
            <td>${p.nombre}</td>
            <td>${p.descripcion}</td>
            <td>$${p.precio}</td>
            <td>${p.stock}</td>
            <td>
                <button class="btn-edit" onclick="prepararEdicion(${p.id})">Editar</button>
                <button class="btn-delete" onclick="eliminarProducto(${p.id})">Eliminar</button>
            </td>
        `;
        tabla.appendChild(row);
    });
}

function renderContador(mostradas, total) {
    const el = document.getElementById("productos-contador");
    if (total === 0) {
        el.textContent = "Sin resultados";
    } else {
        const inicio = paginaActual * PAGE_SIZE + 1;
        const fin = paginaActual * PAGE_SIZE + mostradas;
        el.textContent = `Mostrando ${inicio}–${fin} de ${total} producto${total !== 1 ? "s" : ""}`;
    }
}

function renderPaginacion(current, totalPaginas) {
    const container = document.getElementById("paginacion-productos");
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

    const visible = new Set();
    visible.add(0);
    visible.add(totalPaginas - 1);
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
    fetchProductos();
    window.scrollTo({ top: 0, behavior: "smooth" });
}

// 3. Guardar o actualizar producto
async function guardarProducto() {
    const nombre = document.getElementById("prod-nombre").value;
    const desc   = document.getElementById("prod-desc").value;
    const precio = document.getElementById("prod-precio").value;
    const stock  = document.getElementById("prod-stock").value;

    const datosProd = {
        nombre,
        descripcion: desc,
        precio: parseFloat(precio),
        stock: parseInt(stock),
        categoriaId: parseInt(categoriaId)
    };

    let metodo = "POST";
    if (editandoId) {
        datosProd.id = editandoId;
        metodo = "PUT";
    }

    try {
        const response = await fetch(API_PROD, {
            method: metodo,
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(datosProd)
        });

        if (response.ok) {
            alert(editandoId ? "Producto actualizado" : "Producto guardado");
            limpiarFormulario();
            paginaActual = 0;
            fetchProductos();
        } else {
            alert("Error al procesar el producto");
        }
    } catch (error) {
        alert("Error al conectar con el servidor");
    }
}

// 4. Eliminar producto
async function eliminarProducto(id) {
    if (!confirm("¿Estás seguro de eliminar este producto?")) return;

    try {
        await fetch(`${API_PROD}/${id}`, { method: "DELETE" });
        fetchProductos();
    } catch (error) {
        console.error("Error al eliminar:", error);
    }
}

// 5. Preparar edición leyendo desde la fila de la tabla
function prepararEdicion(id) {
    const fila = document.querySelector(`button[onclick="prepararEdicion(${id})"]`).closest("tr");
    const celdas = fila.querySelectorAll("td");

    document.getElementById("prod-nombre").value  = celdas[0].innerText;
    document.getElementById("prod-desc").value    = celdas[1].innerText;
    document.getElementById("prod-precio").value  = celdas[2].innerText.replace("$", "");
    document.getElementById("prod-stock").value   = celdas[3].innerText;

    editandoId = id;
    const btnGuardar = document.getElementById("btn-save-prod");
    btnGuardar.innerText = "Actualizar cambios";
    btnGuardar.style.backgroundColor = "#28a745";
}

function limpiarFormulario() {
    editandoId = null;

    document.getElementById("prod-nombre").value  = "";
    document.getElementById("prod-desc").value    = "";
    document.getElementById("prod-precio").value  = "";
    document.getElementById("prod-stock").value   = "";

    const btnGuardar = document.getElementById("btn-save-prod");
    btnGuardar.innerText = "Guardar producto";
    btnGuardar.style.backgroundColor = "";
}
