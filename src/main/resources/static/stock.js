const API_URL = "http://localhost:8080/categorias";

document.addEventListener("DOMContentLoaded", () => {
    fetchCategorias();

    // Evento para crear categoría
    document.getElementById("btn-add-category").addEventListener("click", crearCategoria);
});

// 1. Obtener y mostrar categorías
async function fetchCategorias() {
    try {
        const response = await fetch(API_URL);
        const categorias = await response.json();
        renderCategorias(categorias);
    } catch (error) {
        console.error("Error al cargar categorías:", error);
    }
}

function renderCategorias(categorias) {
    const container = document.getElementById("categories-container");
    container.innerHTML = ""; // Limpiar antes de cargar

    if (categorias.length === 0) {
        container.innerHTML = "<p>No hay categorías creadas aún.</p>";
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

// 2. Crear nueva categoría
async function crearCategoria() {
    const input = document.getElementById("new-category-name");
    const nombre = input.value.trim();

    if (!nombre) return alert("Escribe un nombre");

    try {
        await fetch(API_URL, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ nombre: nombre })
        });
        input.value = "";
        fetchCategorias(); // Recargar lista
    } catch (error) {
        console.error("Error al crear:", error);
    }
}

// 3. Eliminar categoría
async function eliminarCategoria(id) {
    if (!confirm("¿Seguro que deseas eliminar esta categoría?")) return;

    try {
        await fetch(`${API_URL}/${id}`, { method: 'DELETE' });
        fetchCategorias(); // Recargar lista
    } catch (error) {
        console.error("Error al eliminar:", error);
    }
}

// 4. Redirección a productos
function verProductos(id) {
    window.location.href = `productos.html?categoriaId=${id}`;
}