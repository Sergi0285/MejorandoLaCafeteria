let token = localStorage.getItem('token');

function verificarTokenYRedireccionarALogin() {

    // Verificar si el token está presente
    if (token === null) {
        // Si el token no está presente, redirigir al usuario al inicio de sesión
        window.location.href = '/Html/inicioSesion.html';
        var tokenParts = token.split('.');
        var tokenPayload = JSON.parse(atob(tokenParts[1]));
        var username=tokenPayload.sub;
        console.log(username);
    }
}

verificarTokenYRedireccionarALogin();

document.addEventListener('DOMContentLoaded', () => {
    const formAgregarDesayuno = document.getElementById('formAgregarDesayuno');
    const mensajeRespuestaDesayuno = document.getElementById('mensajeRespuestaDesayuno');

    const formAgregarAlmuerzo = document.getElementById('formAgregarAlmuerzo');
    const mensajeRespuestaAlmuerzo = document.getElementById('mensajeRespuestaAlmuerzo');

    const formAgregarProductoNormal = document.getElementById('formAgregarProductoNormal');
    const mensajeRespuestaProductoNormal = document.getElementById('mensajeRespuestaProductoNormal');

    const formActualizarProducto = document.getElementById('formActualizarProducto');
    if (formActualizarProducto) {
        formActualizarProducto.addEventListener('submit', async (event) => {
            event.preventDefault();
            await ActualizacionProducto();
        });
    }

    formAgregarDesayuno.addEventListener('submit', async (event) => {
        event.preventDefault(); 

        const nombreProducto = document.getElementById('nombreDesayuno').value;
        const imagenInput = document.getElementById('imagenDesayuno');
        const descripcion = document.getElementById('descripcionDesayuno').value;
        const precio = parseFloat(document.getElementById('precioDesayuno').value);

        const tokenAlMomentoDelEnvio = localStorage.getItem('token'); 
        console.log('Token recuperado al enviar:', tokenAlMomentoDelEnvio);

        if (!tokenAlMomentoDelEnvio) {
            mensajeRespuestaDesayuno.innerHTML = `<div class="alert alert-warning">No se encontró token. Por favor, inicia sesión de nuevo.</div>`;
            return;
        }

        if (!nombreProducto || !imagenInput.files[0] || isNaN(precio) || precio <= 0) {
            mensajeRespuestaDesayuno.innerHTML = `<div class="alert alert-danger">Por favor, completa todos los campos requeridos (incluyendo la imagen).</div>`;
            return;
        }

        const imagenArchivo = imagenInput.files[0];

        const convertirBase64 = (file) => {
            return new Promise((resolve, reject) => {
                const reader = new FileReader();
                reader.readAsDataURL(file);
                reader.onload = () => {
                    // reader.result es "data:image/png;base64,iVBORw0KGgo..."
                    // Necesitamos solo la parte después de la coma.
                    const fullDataUrl = reader.result;
                    const base64String = fullDataUrl.split(',')[1]; // <-- ¡CAMBIO CLAVE AQUÍ!
                    resolve(base64String); 
                };
                reader.onerror = (error) => reject(error);
            });
        };

        try {
            const imagenBase64Pura = await convertirBase64(imagenArchivo); // Ahora contendrá solo la data Base64
            console.log('Imagen Base64 PURA (primeros 100 chars):', imagenBase64Pura.substring(0, 100));

            const productoPayload = {
                nombreProducto: nombreProducto,
                imagenProducto: imagenBase64Pura, // Enviar la cadena Base64 pura
                descripcion: descripcion.trim() === "" ? null : descripcion,
                precio: precio,
                tipo: null, 
                nivel: "Desayuno",
                esBowl: false, 
                cantidad: null, 
                cafeteria: { 
                    idCafeteria: 6
                }
            };

            console.log('Payload JSON a enviar:', JSON.stringify(productoPayload, null, 2));
            mensajeRespuestaDesayuno.innerHTML = `<div class="alert alert-info">Enviando datos como JSON...</div>`;

            const headers = new Headers();
            headers.append('Content-Type', 'application/json');
            headers.append('Authorization', 'Bearer ' + tokenAlMomentoDelEnvio);

            const response = await fetch('http://localhost:8080/productos', {
                method: 'POST',
                headers: headers,
                body: JSON.stringify(productoPayload),
            });

            if (response.ok) {
                const resultado = await response.json(); 
                console.log('Producto agregado:', resultado);
                mensajeRespuestaDesayuno.innerHTML = `<div class="alert alert-success">¡Producto agregado con éxito! ${resultado.idProducto ? `ID: ${resultado.idProducto}` : ''}</div>`;
                formAgregarDesayuno.reset();
            } else {
                const errorTexto = await response.text(); 
                console.error('Error al agregar producto:', response.status, errorTexto);
                mensajeRespuestaDesayuno.innerHTML = `<div class="alert alert-danger">Error al agregar producto (${response.status}): ${errorTexto}</div>`;
            }
        } catch (error) {
            console.error('Error en la conversión a Base64 o en la petición:', error);
            mensajeRespuestaDesayuno.innerHTML = `<div class="alert alert-danger">Error: ${error.message}</div>`;
        }
    });

    formAgregarAlmuerzo.addEventListener('submit', async (event) => {
        event.preventDefault();

        const nombreProducto = document.getElementById('nombreAlmuerzo').value;
        const imagenInput = document.getElementById('imagenAlmuerzo');
        // Obtener el valor original del campo descripción
        const descripcionOriginalInput = document.getElementById('descripcionAlmuerzo').value;
        const precio = parseFloat(document.getElementById('precioAlmuerzo').value);

        const tokenAlMomentoDelEnvio = localStorage.getItem('token');
        console.log('Token recuperado al enviar:', tokenAlMomentoDelEnvio);

        if (!tokenAlMomentoDelEnvio) {
            mensajeRespuestaAlmuerzo.innerHTML = `<div class="alert alert-warning">No se encontró token. Por favor, inicia sesión de nuevo.</div>`;
            return;
        }

        if (!nombreProducto || !imagenInput.files[0] || isNaN(precio) || precio <= 0) {
            mensajeRespuestaAlmuerzo.innerHTML = `<div class="alert alert-danger">Por favor, completa todos los campos requeridos (incluyendo la imagen).</div>`;
            return;
        }

        const imagenArchivo = imagenInput.files[0];

        // --- INICIO: Nuevo código para procesar tipo y descripción ---
        let tipoExtraido;
        let descripcionProcesada;

        const indicePrimerEspacio = descripcionOriginalInput.indexOf(' ');

        if (indicePrimerEspacio !== -1) {
            // Si se encuentra un espacio, dividir la cadena
            tipoExtraido = descripcionOriginalInput.substring(0, indicePrimerEspacio).trim();
            descripcionProcesada = descripcionOriginalInput.substring(indicePrimerEspacio + 1).trim();
        } else {
            // Si no hay espacio, toda la cadena es el tipo, y la descripción queda vacía.
            // Podrías ajustar esta lógica si prefieres otro comportamiento.
            tipoExtraido = descripcionOriginalInput.trim();
            descripcionProcesada = ""; // O null si tu backend lo prefiere así
        }

        // Asegurar que descripcionProcesada sea null si está vacía, similar a tu lógica original
        if (descripcionProcesada === "") {
            descripcionProcesada = null;
        }
        // --- FIN: Nuevo código para procesar tipo y descripción ---

        const convertirBase64 = (file) => {
            return new Promise((resolve, reject) => {
                const reader = new FileReader();
                reader.readAsDataURL(file);
                reader.onload = () => {
                    const fullDataUrl = reader.result;
                    const base64String = fullDataUrl.split(',')[1];
                    resolve(base64String);
                };
                reader.onerror = (error) => reject(error);
            });
        };

        try {
            const imagenBase64Pura = await convertirBase64(imagenArchivo);
            console.log('Imagen Base64 PURA (primeros 100 chars):', imagenBase64Pura.substring(0, 100));

            // Modificar el productoPayload para usar los valores procesados
            const productoPayload = {
                nombreProducto: nombreProducto,
                imagenProducto: imagenBase64Pura,
                descripcion: descripcionProcesada,      // <--- VALOR MODIFICADO
                precio: precio,
                tipo: tipoExtraido,                     // <--- VALOR ASIGNADO
                nivel: "Almuerzo",
                esBowl: false,
                cantidad: null,
                cafeteria: {
                    idCafeteria: 6
                }
            };

            console.log('Payload JSON a enviar:', JSON.stringify(productoPayload, null, 2));
            mensajeRespuestaAlmuerzo.innerHTML = `<div class="alert alert-info">Enviando datos como JSON...</div>`;

            const headers = new Headers();
            headers.append('Content-Type', 'application/json');
            headers.append('Authorization', 'Bearer ' + tokenAlMomentoDelEnvio);

            const response = await fetch('http://localhost:8080/productos', {
                method: 'POST',
                headers: headers,
                body: JSON.stringify(productoPayload),
            });

            if (response.ok) {
                const resultado = await response.json();
                console.log('Producto agregado:', resultado);
                mensajeRespuestaAlmuerzo.innerHTML = `<div class="alert alert-success">¡Producto agregado con éxito! ${resultado.idProducto ? `ID: ${resultado.idProducto}` : ''}</div>`;
                // Asumo que es formAgregarAlmuerzo y no formAgregarDesayuno
                formAgregarAlmuerzo.reset();
            } else {
                const errorTexto = await response.text();
                console.error('Error al agregar producto:', response.status, errorTexto);
                mensajeRespuestaAlmuerzo.innerHTML = `<div class="alert alert-danger">Error al agregar producto (${response.status}): ${errorTexto}</div>`;
            }
        } catch (error) {
            console.error('Error en la conversión a Base64 o en la petición:', error);
            mensajeRespuestaAlmuerzo.innerHTML = `<div class="alert alert-danger">Error: ${error.message}</div>`;
        }
    });

    formAgregarProductoNormal.addEventListener('submit', async (event) => {
        event.preventDefault(); 

        const nombreProducto = document.getElementById('nombreProductoNormal').value;
        const imagenInput = document.getElementById('imagenProductoNormal');
        const descripcion = document.getElementById('descripcionProductoNormal').value;
        const precio = parseFloat(document.getElementById('precioProductoNormal').value);
        const cantidad = parseInt(document.getElementById('cantidadProductoNormal').value);

        const tokenAlMomentoDelEnvio = localStorage.getItem('token'); 
        console.log('Token recuperado al enviar:', tokenAlMomentoDelEnvio);

        if (!tokenAlMomentoDelEnvio) {
            mensajeRespuestaProductoNormal.innerHTML = `<div class="alert alert-warning">No se encontró token. Por favor, inicia sesión de nuevo.</div>`;
            return;
        }

        if (!nombreProducto || !imagenInput.files[0] || isNaN(precio) || precio <= 0 || cantidad <= 0) {
            mensajeRespuestaProductoNormal.innerHTML = `<div class="alert alert-danger">Por favor, completa todos los campos requeridos (incluyendo la imagen).</div>`;
            return;
        }

        const imagenArchivo = imagenInput.files[0];

        const convertirBase64 = (file) => {
            return new Promise((resolve, reject) => {
                const reader = new FileReader();
                reader.readAsDataURL(file);
                reader.onload = () => {
                    // reader.result es "data:image/png;base64,iVBORw0KGgo..."
                    // Necesitamos solo la parte después de la coma.
                    const fullDataUrl = reader.result;
                    const base64String = fullDataUrl.split(',')[1]; // <-- ¡CAMBIO CLAVE AQUÍ!
                    resolve(base64String); 
                };
                reader.onerror = (error) => reject(error);
            });
        };

        try {
            const imagenBase64Pura = await convertirBase64(imagenArchivo); // Ahora contendrá solo la data Base64
            console.log('Imagen Base64 PURA (primeros 100 chars):', imagenBase64Pura.substring(0, 100));

            const productoPayload = {
                nombreProducto: nombreProducto,
                imagenProducto: imagenBase64Pura, // Enviar la cadena Base64 pura
                descripcion: descripcion.trim() === "" ? null : descripcion,
                precio: precio,
                tipo: null, 
                nivel: "no",
                esBowl: false, 
                cantidad: cantidad, 
                cafeteria: { 
                    idCafeteria: 3
                }
            };

            console.log('Payload JSON a enviar:', JSON.stringify(productoPayload, null, 2));
            mensajeRespuestaProductoNormal.innerHTML = `<div class="alert alert-info">Enviando datos como JSON...</div>`;

            const headers = new Headers();
            headers.append('Content-Type', 'application/json');
            headers.append('Authorization', 'Bearer ' + tokenAlMomentoDelEnvio);

            const response = await fetch('http://localhost:8080/productos', {
                method: 'POST',
                headers: headers,
                body: JSON.stringify(productoPayload),
            });

            if (response.ok) {
                const resultado = await response.json(); 
                console.log('Producto agregado:', resultado);
                mensajeRespuestaProductoNormal.innerHTML = `<div class="alert alert-success">¡Producto agregado con éxito! ${resultado.idProducto ? `ID: ${resultado.idProducto}` : ''}</div>`;
                formAgregarProductoNormal.reset();
            } else {
                const errorTexto = await response.text(); 
                console.error('Error al agregar producto:', response.status, errorTexto);
                mensajeRespuestaProductoNormal.innerHTML = `<div class="alert alert-danger">Error al agregar producto (${response.status}): ${errorTexto}</div>`;
            }
        } catch (error) {
            console.error('Error en la conversión a Base64 o en la petición:', error);
            mensajeRespuestaProductoNormal.innerHTML = `<div class="alert alert-danger">Error: ${error.message}</div>`;
        }
    });

    async function ActualizacionProducto() {

        const convertirBase64 = (file) => {
            return new Promise((resolve, reject) => {
                const reader = new FileReader();
                reader.readAsDataURL(file);
                reader.onload = () => {
                    // reader.result es "data:image/png;base64,iVBORw0KGgo..."
                    // Necesitamos solo la parte después de la coma.
                    const fullDataUrl = reader.result;
                    const base64String = fullDataUrl.split(',')[1]; // <-- ¡CAMBIO CLAVE AQUÍ!
                    resolve(base64String); 
                };
                reader.onerror = (error) => reject(error);
            });
        };

        const form = document.getElementById('formActualizarProducto');
        const mensajeRespuesta = document.getElementById('mensajeRespuestaActualizarProducto');
        mensajeRespuesta.innerHTML = '';

        const nombreParaBuscar = document.getElementById('nombreActualizarProducto').value.trim();
        if (!nombreParaBuscar) {
            mensajeRespuesta.innerHTML = `<div class="alert alert-warning">Por favor, ingresa el nombre del producto a buscar y actualizar.</div>`;
            return;
        }

        const token = localStorage.getItem('token');
        if (!token) {
            mensajeRespuesta.innerHTML = `<div class="alert alert-warning">No se encontró token. Por favor, inicia sesión de nuevo.</div>`;
            return;
        }

        let productoOriginal; // Cambiado de productoAActualizar para claridad
        let productoId;

        // 1. Buscar el producto para obtener su ID y datos actuales
        try {
            mensajeRespuesta.innerHTML = `<div class="alert alert-info">Buscando producto '${nombreParaBuscar}'...</div>`;
            const responseBusqueda = await fetch(`http://localhost:8080/productos/buscarPorNombre?nombre=${encodeURIComponent(nombreParaBuscar)}`, {
                method: 'GET',
                headers: { 'Authorization': `Bearer ${token}` }
            });

            if (responseBusqueda.status === 404) {
                mensajeRespuesta.innerHTML = `<div class="alert alert-danger">Producto no encontrado: ${nombreParaBuscar}.</div>`;
                return;
            }
            if (!responseBusqueda.ok) {
                const errorTexto = await responseBusqueda.text();
                mensajeRespuesta.innerHTML = `<div class="alert alert-danger">Error al buscar (${responseBusqueda.status}): ${errorTexto}</div>`;
                return;
            }

            const productosEncontrados = await responseBusqueda.json();
            if (!productosEncontrados || productosEncontrados.length === 0) {
                mensajeRespuesta.innerHTML = `<div class="alert alert-danger">Producto no encontrado: ${nombreParaBuscar}.</div>`;
                return;
            }

            if (productosEncontrados.length > 1) {
                console.warn(`Múltiples productos encontrados para "${nombreParaBuscar}". Se usará el primero.`);
                mensajeRespuesta.innerHTML = `<div class="alert alert-warning">Múltiples productos encontrados. Se procesará el primero.</div>`;
            }
            productoOriginal = productosEncontrados[0];

            // Ajusta 'idProducto' si el campo ID en tu backend se llama diferente
            if (productoOriginal.idProducto === undefined || productoOriginal.idProducto === null) {
                mensajeRespuesta.innerHTML = `<div class="alert alert-danger">El producto encontrado no tiene un ID válido.</div>`;
                return;
            }
            productoId = productoOriginal.idProducto; // El ID es tipo 'int' en tu backend @PathVariable

        } catch (error) {
            console.error('Error en búsqueda:', error);
            mensajeRespuesta.innerHTML = `<div class="alert alert-danger">Error al buscar: ${error.message}</div>`;
            return;
        }

        // 2. Construir el payload de actualización.
        // Empezamos con una copia de TODOS los datos originales del producto.
        const payloadActualizacion = { ...productoOriginal };

        if (productoOriginal.cafeteria) {
            // Caso 1: productoOriginal.cafeteria es un objeto y tiene idCafeteria (esperado)
            if (typeof productoOriginal.cafeteria === 'object' && productoOriginal.cafeteria.idCafeteria !== undefined) {
                payloadActualizacion.cafeteria = { idCafeteria: productoOriginal.cafeteria.idCafeteria };
            }
            // Caso 2: productoOriginal.cafeteria es solo un número (como en tu log del payload)
            else if (typeof productoOriginal.cafeteria === 'number') {
                payloadActualizacion.cafeteria = { idCafeteria: productoOriginal.cafeteria };
            }
            // Caso 3: La estructura de cafeteria no es la esperada o falta el ID
            else {
                // Si la cafetería es crucial y no se puede determinar su ID, podrías mostrar un error.
                // Dado que setCafeteria está comentado en tu PUT, si la estructura es inesperada,
                // es más seguro eliminarla del payload para evitar errores de deserialización,
                // asumiendo que el backend puede manejar un productoActualizado con cafeteria=null
                // temporalmente durante la deserialización (el producto 'p' persistente no la perderá).
                console.warn('La estructura de productoOriginal.cafeteria es inesperada o falta idCafeteria. Omitiendo cafeteria del payload.', productoOriginal.cafeteria);
                delete payloadActualizacion.cafeteria;
            }
        } else {
            // Si productoOriginal.cafeteria es null o undefined, la eliminamos del payload.
            // Esto es importante si tu entidad Producto en Java no permite una Cafeteria null
            // y el campo no se está actualizando de todos modos.
            delete payloadActualizacion.cafeteria;
        }

        // El ID no se suele enviar en el cuerpo de un PUT a /productos/{id},
        // ya que está en la URL. Si tu clase 'producto' en Java tiene un 'id' y Jackson
        // intenta mapearlo desde el cuerpo si está presente, podría ser confuso.
        // Es más limpio si el backend no espera el ID en el cuerpo aquí.
        // Tu backend actual no hace p.setId(productoActualizado.getId()), así que está bien.
        // delete payloadActualizacion.idProducto; // Opcional, si quieres ser explícito.

        // Nombre del producto: siempre se toma del formulario.
        payloadActualizacion.nombreProducto = document.getElementById('nombreActualizarProducto').value.trim();

        // Imagen: solo se actualiza si se selecciona un nuevo archivo.
        const imagenInput = document.getElementById('imagenActualizarProducto');
        if (imagenInput.files && imagenInput.files[0]) {
            try {
                const imagenBase64Pura = await convertirBase64(imagenInput.files[0]);
                payloadActualizacion.imagenProducto = imagenBase64Pura;
            } catch (error) {
                console.error('Error al convertir imagen:', error);
                mensajeRespuesta.innerHTML = `<div class="alert alert-danger">Error al procesar imagen: ${error.message}</div>`;
                return;
            }
        }
        // Si no se selecciona nueva imagen, 'imagenProducto' en payloadActualizacion conserva el valor original.

        // Descripción y Tipo: solo se actualizan si el campo descripción NO está vacío.
        const descripcionInputVal = document.getElementById('descripcionActualizarProducto').value; // No trim todavía
        if (descripcionInputVal.trim() !== "") {
            const trimmedDescInput = descripcionInputVal.trim();
            const indiceEspacio = trimmedDescInput.indexOf(' ');
            if (indiceEspacio !== -1) {
                payloadActualizacion.tipo = trimmedDescInput.substring(0, indiceEspacio).trim();
                const descProcesada = trimmedDescInput.substring(indiceEspacio + 1).trim();
                payloadActualizacion.descripcion = descProcesada === "" ? null : descProcesada;
            } else {
                payloadActualizacion.tipo = trimmedDescInput;
                payloadActualizacion.descripcion = null;
            }
        }
        // Si el campo 'descripcionActualizarProducto' está vacío, 'tipo' y 'descripcion'
        // en payloadActualizacion conservarán sus valores originales.

        // Precio: solo se actualiza si el campo NO está vacío y es válido.
        const precioInputVal = document.getElementById('precioActualizarProducto').value;
        if (precioInputVal.trim() !== "") {
            const precio = parseFloat(precioInputVal);
            if (!isNaN(precio) && precio >= 0) {
                payloadActualizacion.precio = precio;
            } else {
                mensajeRespuesta.innerHTML += `<div class="alert alert-warning">Precio inválido. Se conservará el original.</div>`;
            }
        }
        // Si el campo 'precioActualizarProducto' está vacío, 'precio' en payloadActualizacion conserva su valor original.

        // Cantidad: solo se actualiza si el campo NO está vacío y es válido.
        const cantidadInputVal = document.getElementById('cantidadActualizarProducto').value;
        if (cantidadInputVal.trim() !== "") {
            const cantidad = parseInt(cantidadInputVal, 10);
            if (!isNaN(cantidad) && cantidad >= 0) {
                payloadActualizacion.cantidad = cantidad;
            } else {
                mensajeRespuesta.innerHTML += `<div class="alert alert-warning">Cantidad inválida. Se conservará la original.</div>`;
            }
        }
        // Si el campo 'cantidadActualizarProducto' está vacío, 'cantidad' en payloadActualizacion conserva su valor original.

        // Campos como 'nivel', 'esBowl' (y 'cafeteria' si fuera relevante y parte de productoOriginal)
        // ya están en 'payloadActualizacion' con sus valores originales de 'productoOriginal'.
        // El backend los recibirá y los reasignará, manteniendo efectivamente sus valores si no estaban en el formulario.

        // 3. Enviar la solicitud de actualización
        try {
            mensajeRespuesta.innerHTML += `<div class="alert alert-info">Actualizando producto '${productoOriginal.nombreProducto}'...</div>`;
            console.log("Payload de actualización final:", JSON.stringify(payloadActualizacion, null, 2));

            const responseActualizacion = await fetch(`http://localhost:8080/productos/${productoId}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
                body: JSON.stringify(payloadActualizacion)
            });

            if (responseActualizacion.ok) {
                const productoActualizadoRes = await responseActualizacion.json(); // El backend devuelve el producto actualizado
                mensajeRespuesta.innerHTML = `<div class="alert alert-success">Producto '${productoActualizadoRes.nombreProducto}' actualizado con éxito.</div>`;
                form.reset();
            } else {
                const errorTexto = await responseActualizacion.text();
                mensajeRespuesta.innerHTML = `<div class="alert alert-danger">Error al actualizar (${responseActualizacion.status}): ${errorTexto}</div>`;
                console.error('Error al actualizar:', responseActualizacion.status, errorTexto);
            }
        } catch (error) {
            console.error('Error en petición de actualización:', error);
            mensajeRespuesta.innerHTML = `<div class="alert alert-danger">Error al conectar para actualizar: ${error.message}</div>`;
        }
    }

});