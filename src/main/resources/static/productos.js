const params = new URLSearchParams(window.location.search);
const categoriaId = params.get("categoriaId");
const API_CAT = "http://localhost:8080/categorias";
const API_PROD = "http://localhost:8080/productos"; // Para POST/DELETE

let editandoId = null;

document.addEventListener("DOMContentLoaded", () => {
    if (!categoriaId) {
        window.location.href = "stock.html";
        return;
    }
    fetchDatosCategoria();

    document.getElementById("btn-save-prod").addEventListener("click", guardarProducto);
});

// 1. Obtener la categoría y sus productos
async function fetchDatosCategoria() {
    try {
        const response = await fetch(`${API_CAT}/${categoriaId}`);
        const categoria = await response.json();
        
        // Actualiza el título de la página con el nombre de la categoría
        document.getElementById("categoria-titulo").innerText = `Categoría: ${categoria.nombre}`;
        
        // Mapea la lista de productos que viene dentro del objeto Categoria
        renderProductos(categoria.productos || []); 
    } catch (error) {
        console.error("Error cargando datos:", error);
    }
}

function renderProductos(productos) {
    const tabla = document.getElementById("lista-productos");
    tabla.innerHTML = "";

    if (!productos || productos.length === 0) {
        tabla.innerHTML = "<tr><td colspan='5'>No hay productos en esta categoría.</td></tr>";
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

// 2. Guardar o Actualizar Producto
async function guardarProducto() {
    const nombre = document.getElementById("prod-nombre").value;
    const desc = document.getElementById("prod-desc").value;
    const precio = document.getElementById("prod-precio").value;
    const stock = document.getElementById("prod-stock").value;

    const datosProd = {
        nombre: nombre,
        descripcion: desc,
        precio: parseFloat(precio),
        stock: parseInt(stock),
        categoriaId: parseInt(categoriaId)
    };

    let url = API_PROD;
    let metodo = 'POST';

    if (editandoId) {
        datosProd.id = editandoId;
        metodo = 'PUT';
    }

    try {
        const response = await fetch(url, {
            method: metodo,
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(datosProd)
        });

        if(response.ok) {
            alert(editandoId ? "Producto actualizado" : "Producto guardado");
            limpiarFormulario();
            fetchDatosCategoria(); // Recarga para ver el nuevo producto
        } else {
            console.error("Error en el servidor: ", response.status);
            alert("Error al procesar el producto")
        }
    } catch (error) {
        alert("Error al conectar con el servidor");
    }
}

// 3. Eliminar Producto (DELETE)
async function eliminarProducto(id) {
    if (!confirm("¿Estás seguro de eliminar este producto?")) return;

    try {
        await fetch(`${API_PROD}/${id}`, { method: 'DELETE' });
        fetchDatosCategoria();
    } catch (error) {
        console.error("Error al eliminar:", error);
    }
}


function prepararEdicion(id) {
    const fila = document.querySelector(`button[onclick="prepararEdicion(${id})"]`).closest("tr");
    const celdas = fila.querySelectorAll("td");

    document.getElementById("prod-nombre").value = celdas[0].innerText;
    document.getElementById("prod-desc").value = celdas[1].innerText;
    document.getElementById("prod-precio").value = celdas[2].innerText.replace("$", " ");
    document.getElementById("prod-stock").value = celdas[3].innerText;

    editandoId = id;
    const btnGuardar = document.getElementById("btn-save-prod");
    btnGuardar.innerText = "Actualizar cambios";
    btnGuardar.style.backgroundColor = "#28a745"
}

function limpiarFormulario() {
    editandoId = null;
    
    document.getElementById("prod-nombre").value = "";
    document.getElementById("prod-desc").value = "";
    document.getElementById("prod-precio").value = "";
    document.getElementById("prod-stock").value = "";

    const btnGuardar = document.getElementById("btn-save-prod");
    btnGuardar.innerText = "Guardar producto";
    btnGuardar.style.backgroundColor = "";
}