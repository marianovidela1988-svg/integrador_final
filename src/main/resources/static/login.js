document.addEventListener('DOMContentLoaded', function() {
    const form = document.getElementById('loginForm');

    if (form) {
        form.addEventListener('submit', function(event) {
            event.preventDefault();

            const u = document.getElementById('usuario').value;
            const p = document.getElementById('clave').value;

            const datosLogin = {
                id: 0,
                nombre: "",
                apellido: "",
                user: u,
                pass: p
            };

            fetch(`/admin/login`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(datosLogin)
            })
            .then(response => {
                if (!response.ok) throw new Error("Error en la respuesta del servidor");
                return response.json();
            })
            .then(data => {
                if (data.respuesta == "OK") {
                    window.location.href = "inicio.html";
                } else {
                    alert(data.mensaje || "Usuario o clave incorrectos");
                }
            })
            .catch(error => {
                console.error('Error', error);
                alert("No se pudo conectar con el servidor o la ruta es incorrecta");
            })
        });
    }
});