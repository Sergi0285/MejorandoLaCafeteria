document.addEventListener('DOMContentLoaded', () => {
    // 1) Creamos un input file oculto que usaremos para seleccionar la imagen
    const inputFileAviso = document.createElement('input');
    inputFileAviso.type = 'file';
    inputFileAviso.accept = 'image/*';
    inputFileAviso.style.display = 'none';
    document.body.appendChild(inputFileAviso);
  
    // 2) Referencia al botón que quedó con id="btnSubirAviso"
    const btnSubirAviso = document.getElementById('btnSubirAviso');
    if (!btnSubirAviso) return;
  
    // 3) Al hacer click en el botón, disparamos el file picker
    btnSubirAviso.addEventListener('click', (e) => {
      e.preventDefault();
      inputFileAviso.click();
    });
  
    // 4) Cuando el usuario elige un archivo, lo enviamos al backend
    inputFileAviso.addEventListener('change', async () => {
      const archivo = inputFileAviso.files[0];
      if (!archivo) return;
  
      // 4.1) Armar FormData: cafeteriaId (cámbialo si es otro) + el blob de la imagen
      const formData = new FormData();
      formData.append('cafeteriaId', '3'); // <- Cambia "3" por el id real de la cafetería
      formData.append('aviso', archivo);
  
      try {
        // 4.2) Petición POST a /api/avisos (sin token, si tu endpoint no requiere auth)
        const respuesta = await fetch('/api/avisos', {
          method: 'POST',
          body: formData
        });
  
        if (respuesta.ok) {
          // ----- ÉXITO: reemplaza alert por Swal.fire -----
          Swal.fire({
            icon: 'success',
            title: 'Aviso subido correctamente',
            showConfirmButton: false,
            timer: 1500
          }).then(() => {
            // 4.3) Recargamos la página para que se actualice la sección de “Frase Divertida”
            window.location.reload();
          });
        } else {
          // Extraemos el texto de error que devuelva el servidor
          const textoError = await respuesta.text();
          // ----- ERROR EN LA RESPUESTA HTTP: Swal.fire con icono de error -----
          Swal.fire({
            icon: 'error',
            title: 'Error al subir el aviso',
            text: `(${respuesta.status}): ${textoError}`,
            confirmButtonText: 'Aceptar'
          });
        }
      } catch (err) {
        // ----- ERROR DE CONEXIÓN / EXCEPCIÓN: Swal.fire con icono de error -----
        Swal.fire({
          icon: 'error',
          title: 'No se pudo conectar al servidor',
          text: err.message,
          confirmButtonText: 'Aceptar'
        });
      }
    });
  });