document.addEventListener('DOMContentLoaded', () => {
  // Elementos del DOM que vamos a actualizar (un único <img> y <span> o <p> para fecha)
  const imgAvisoEl = document.getElementById('imgAviso');
  const fechaAvisoEl = document.getElementById('fechaAviso');

  // Para evitar caché en la petición JSON
  fetch(`/api/avisos?_=${Date.now()}`)
    .then(response => {
      if (!response.ok) {
        throw new Error(`Error al obtener avisos: ${response.status}`);
      }
      return response.json();
    })
    .then(avisos => {
      if (!Array.isArray(avisos) || avisos.length === 0) {
        // No hay avisos: dejamos el GIF por defecto y la fecha vacía (o lo que tu HTML tenga por defecto)
        return;
      }

      // 1) Ordenar todos los avisos por fechaPublicacion, descendente (más reciente primero).
      avisos.sort((a, b) => {
        // Se asume que "fechaPublicacion" viene como "YYYY-MM-DD"
        if (a.fechaPublicacion < b.fechaPublicacion) return 1;
        if (a.fechaPublicacion > b.fechaPublicacion) return -1;
        return 0;
      });

      // 2) Tomar las primeras 5 entradas (o las que existan si hay menos de 5)
      const topAvisos = avisos.slice(0, 5);

      // 3) Función para mostrar un aviso según su índice en topAvisos
      let currentIndex = 0;
      const rotationInterval = 10000; // 5 segundos para rotar al siguiente aviso

      function mostrarAviso(idx) {
        const aviso = topAvisos[idx];
        // Actualizar <img> apuntando a la ruta que sirve el BLOB de la imagen de ese aviso
        imgAvisoEl.src = `/api/avisos/imagen/${aviso.idAviso}?_=${Date.now()}`;
        imgAvisoEl.alt = `Aviso publicado el ${aviso.fechaPublicacion}`;

        // Formatear “YYYY-MM-DD” ➔ “DD/MM/YYYY”
        const partes = aviso.fechaPublicacion.split('-');
        if (partes.length === 3) {
          fechaAvisoEl.textContent = `${partes[2]}/${partes[1]}/${partes[0]}`;
        } else {
          fechaAvisoEl.textContent = aviso.fechaPublicacion;
        }
      }

      // 4) Mostrar inmediatamente el primer aviso
      mostrarAviso(currentIndex);

      // 5) Si hay más de uno, empezar el intervalo para rotar
      if (topAvisos.length > 1) {
        setInterval(() => {
          currentIndex = (currentIndex + 1) % topAvisos.length;
          mostrarAviso(currentIndex);
        }, rotationInterval);
      }
    })
    .catch(err => {
      console.error('No se pudo cargar avisos:', err);
      // En caso de error, dejamos el GIF/imagen por defecto y sin fecha
    });
});
