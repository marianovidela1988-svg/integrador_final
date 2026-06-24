const API_URL = "http://localhost:8080/categorias";
const PAGE_SIZE = 12;

let paginaActual = 0;
let filtroNombre = "";

document.addEventListener("DOMContentLoaded", () => {
    fetchCategorias();

    document.getElementById("btn-add-category").addEventListener("click", crearCategoria);

    document.getElementById("filter-category").addEventListener("input", (e) => {
        filtroNombre = e.target.value.trim();
        paginaActual = 0;
        fetchCategorias();
    });
});

async function fetchCategorias() {
    try {
        const params = new URLSearchParams({ page: paginaActual, size: PAGE_SIZE, nombre: filtroNombre });
        const response = await fetch(`${API_URL}/paginadas?${params}`);
        if (!response.ok) {
            throw new Error(`HTTP ${response.status}`);
        }
        const data = await response.json();
        renderCategorias(data.content);
        renderContador(data.numberOfElements, data.totalElements);
        renderPaginacion(data.number, data.totalPages);
    } catch (error) {
        console.error("Error al cargar categorías:", error);
        document.getElementById("categories-container").innerHTML =
            `<p style='color:var(--red)'>Error al cargar categorías: ${error.message}</p>`;
    }
}

function renderCategorias(categorias) {
    const container = document.getElementById("categories-container");
    container.innerHTML = "";

    if (categorias.length === 0) {
        container.innerHTML = "<p style='color: var(--text-muted)'>No hay categorías que coincidan.</p>";
        return;
    }

    categorias.forEach(cat => {
        const div = document.createElement("div");
        div.className = "category-card";
        div.innerHTML = `
            <div class="cat-name" onclick="verProductos(${cat.id})">
                📂 ${cat.nombre}
            </div>
            <button class="btn-delete-cat" onclick="eliminarCategoria(${cat.id})">
                Eliminar Categoría
            </button>
        `;
        container.appendChild(div);
    });
}

function renderContador(mostradas, total) {
    const el = document.getElementById("categorias-contador");
    if (total === 0) {
        el.textContent = "Sin resultados";
    } else {
        const inicio = paginaActual * PAGE_SIZE + 1;
        const fin = paginaActual * PAGE_SIZE + mostradas;
        el.textContent = `Mostrando ${inicio}–${fin} de ${total} categoría${total !== 1 ? "s" : ""}`;
    }
}

function renderPaginacion(current, totalPaginas) {
    const container = document.getElementById("paginacion-container");
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

    // Botón anterior
    const btnPrev = document.createElement("button");
    btnPrev.className = "btn-pag";
    btnPrev.textContent = "←";
    btnPrev.disabled = current === 0;
    btnPrev.addEventListener("click", () => irAPagina(current - 1));
    container.appendChild(btnPrev);

    // Páginas visibles: siempre primera y última, y ventana de ±2 alrededor de la actual
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

    // Botón siguiente
    const btnNext = document.createElement("button");
    btnNext.className = "btn-pag";
    btnNext.textContent = "→";
    btnNext.disabled = current === totalPaginas - 1;
    btnNext.addEventListener("click", () => irAPagina(current + 1));
    container.appendChild(btnNext);
}

function irAPagina(pagina) {
    paginaActual = pagina;
    fetchCategorias();
    window.scrollTo({ top: 0, behavior: "smooth" });
}

async function crearCategoria() {
    const input = document.getElementById("new-category-name");
    const nombre = input.value.trim();
    if (!nombre) return alert("Escribe un nombre");

    try {
        await fetch(API_URL, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ nombre })
        });
        input.value = "";
        paginaActual = 0;
        fetchCategorias();
    } catch (error) {
        console.error("Error al crear:", error);
    }
}

async function eliminarCategoria(id) {
    if (!confirm("¿Seguro que deseas eliminar esta categoría?")) return;

    try {
        const response = await fetch(`${API_URL}/${id}`, { method: "DELETE" });

        if (!response.ok) {
            const data = await response.json();
            alert(data.mensaje || "No se pudo eliminar la categoría.");
            return;
        }

        fetchCategorias();
    } catch (error) {
        console.error("Error al eliminar:", error);
    }
}

function verProductos(id) {
    window.location.href = `productos.html?categoriaId=${id}`;
}
