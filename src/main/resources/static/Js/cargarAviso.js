document.addEventListener('DOMContentLoaded', () => {
    // 1. Cambia aquí el ID de cafetería que corresponda:
    const idCafeteria = 3;

    // 2. Elementos del DOM que vamos a actualizar
    const imgAvisoEl = document.getElementById('imgAviso');
    const fechaAvisoEl = document.getElementById('fechaAviso');

    // 3. Para evitar que el navegador cachee la respuesta JSON, agregamos un query param con timestamp
    fetch(`/api/avisos/cafeteria/${idCafeteria}?_=${Date.now()}`)
      .then(response => {
        if (!response.ok) {
          throw new Error(`Error al obtener avisos: ${response.status}`);
        }
        return response.json();
      })
      .then(avisos => {
        if (!Array.isArray(avisos) || avisos.length === 0) {
          // No hay avisos: dejamos el GIF por defecto y la fecha vacía
          return;
        }

        // 4. Encontrar el aviso con la fechaPublicacion más reciente
        //    Se asume que cada aviso tiene campo "fechaPublicacion" tipo "YYYY-MM-DD"
        avisos.sort((a, b) => {
          // Comparamos cadenas "YYYY-MM-DD": orden lexicográfico funciona
          if (a.fechaPublicacion < b.fechaPublicacion) return 1;
          if (a.fechaPublicacion > b.fechaPublicacion) return -1;
          return 0;
        });
        const avisoReciente = avisos[0];

        // 5. Actualizar el <img> para que apunte al BLOB (imagen) de ese aviso
        //    Agregamos un query param con timestamp para romper la cache de la imagen
        imgAvisoEl.src = `/api/avisos/imagen/${avisoReciente.idAviso}?_=${Date.now()}`;
        imgAvisoEl.alt = 'Aviso del Día';

        // 6. Formatear la fechaPublicacion ("YYYY-MM-DD" ➔ "DD/MM/YYYY")
        const partes = avisoReciente.fechaPublicacion.split('-');
        if (partes.length === 3) {
          const fechaFormateada = `${partes[2]}/${partes[1]}/${partes[0]}`;
          fechaAvisoEl.textContent = fechaFormateada;
        }
      })
      .catch(err => {
        console.error('No se pudo cargar aviso:', err);
        // En caso de error, dejamos el GIF por defecto y sin fecha
      });
});