document.addEventListener('DOMContentLoaded', () => {
    const API_BASE_URL = 'http://localhost:8080/productos';
    const INTERACCION_BASE_URL = 'http://localhost:8080/interaccion';
    const token = localStorage.getItem('token');

    // --- DOM Elements ---
    const desayunoTab = document.getElementById('desayuno-tab');
    const almuerzoTab = document.getElementById('almuerzo-tab');
    const desayunoContentContainer = document.querySelector('#desayuno > .tab-content.text-center');
    const lunchDayTabs = {
        LUN: document.getElementById('lunesAlmuerzo-tab'),
        MAR: document.getElementById('martesAlmuerzo-tab'),
        MIER: document.getElementById('miercolesAlmuerzo-tab'),
        JUE: document.getElementById('juevesAlmuerzo-tab'),
        VIER: document.getElementById('viernesAlmuerzo-tab')
    };
    const lunchDayContentPanes = {
        LUN: document.getElementById('lunesAlmuerzo'),
        MAR: document.getElementById('martesAlmuerzo'),
        MIER: document.getElementById('miercolesAlmuerzo'),
        JUE: document.getElementById('juevesAlmuerzo'),
        VIER: document.getElementById('viernesAlmuerzo')
    };
    const carouselInner = document.querySelector('#productosCarrusel .carousel-inner');

    // --- ID de la Cafetería para filtrar ---
    const ID_CAFETERIA_FILTRO = 3;
    let productosFiltradosCafeteria = [];

    // --- Helper Functions ---
    async function fetchData(url, options = {}) {
        const headers = new Headers({ 'Content-Type': 'application/json' });
        if (token) {
            headers.append('Authorization', 'Bearer ' + token);
        }
        options.headers = headers;

        try {
            const response = await fetch(url, options);
            if (!response.ok) {
                console.error(`Error fetching ${url}: ${response.status} ${response.statusText}`);
                const errorData = await response.text();
                console.error("Error details:", errorData);
                return null;
            }
            if (response.status === 204 || response.headers.get("content-length") === "0") {
                return [];
            }
            return await response.json();
        } catch (error) {
            console.error(`Network error or other issue fetching ${url}:`, error);
            return null;
        }
    }

    function createProductImageSrc(base64String) {
        if (!base64String) return '../Recursos/img/placeholder.PNG';
        return `data:image/jpeg;base64,${base64String}`;
    }

    /**
     * Genera el HTML para el contenedor de Me gusta / No me gusta.
     * Recibe el idProducto y dayKey (por ejemplo "DESAYUNO", "LUN", "MAR", "PRODUCTO", etc.)
     */
    function createLikeDislikeHtml(idProducto, dayKey) {
        return `
            <div class="like-dislike-container" data-producto-id="${idProducto}" data-day-key="${dayKey}">
                <button class="btn-like btn btn-light me-2">
                    👍 <span class="like-count">0</span>
                </button>
                <button class="btn-dislike btn btn-light">
                    👎 <span class="dislike-count">0</span>
                </button>
            </div>
        `;
    }

    /**
     * Recorre todos los contenedores .like-dislike-container que ya estén en el DOM,
     * obtiene sus contadores actuales desde GET /interaccion/producto/{idProducto},
     * y atacha los listeners para POST /interaccion/{idProducto}/megusta o /nogusta.
     * Además, si en localStorage ya existe 'interaccion_{idProducto}_{dayKey}', deshabilita ambos botones.
     */
    function attachLikeDislikeHandlers() {
        const containers = document.querySelectorAll('.like-dislike-container');
        containers.forEach(container => {
            const idProducto = container.dataset.productoId;
            const dayKey     = container.dataset.dayKey;
            const likeSpan   = container.querySelector('.like-count');
            const dislikeSpan= container.querySelector('.dislike-count');
            const btnLike    = container.querySelector('.btn-like');
            const btnDislike = container.querySelector('.btn-dislike');

            // Clave localStorage por producto + día
            const claveLocal = `interaccion_${idProducto}_${dayKey}`;

            // Si ya votó en ESTE día, deshabilitar botones
            if (localStorage.getItem(claveLocal)) {
                btnLike.disabled    = true;
                btnDislike.disabled = true;
            }

            // 1) Obtener conteo actual (global) desde backend
            fetch(`${INTERACCION_BASE_URL}/producto/${idProducto}`, {
                method: 'GET',
                headers: { 'Content-Type': 'application/json' }
            })
                .then(res => res.json())
                .then(data => {
                    likeSpan.textContent    = data.meGusta ?? 0;
                    dislikeSpan.textContent = data.noGusta ?? 0;
                })
                .catch(err => {
                    console.error(`Error al obtener interacción de producto ${idProducto}:`, err);
                });

            // 2) Listener para "Me gusta"
            btnLike.addEventListener('click', () => {
                if (localStorage.getItem(claveLocal)) return;

                fetch(`${INTERACCION_BASE_URL}/${idProducto}/megusta`, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' }
                })
                    .then(res => {
                        if (!res.ok) {
                            console.error(`Error POST /megusta de ${idProducto}: ${res.status}`);
                            return;
                        }
                        // Guardar en localStorage y deshabilitar botones
                        localStorage.setItem(claveLocal, 'votado');
                        btnLike.disabled    = true;
                        btnDislike.disabled = true;
                        // Recargar contadores
                        return fetch(`${INTERACCION_BASE_URL}/producto/${idProducto}`, {
                            method: 'GET',
                            headers: { 'Content-Type': 'application/json' }
                        });
                    })
                    .then(res2 => {
                        if (!res2) return;
                        if (!res2.ok) {
                            console.error(`Error al recargar conteo meGusta ${idProducto}: ${res2.status}`);
                            return;
                        }
                        return res2.json();
                    })
                    .then(data2 => {
                        if (!data2) return;
                        likeSpan.textContent    = data2.meGusta ?? 0;
                        dislikeSpan.textContent = data2.noGusta ?? 0;
                    })
                    .catch(err => {
                        console.error(`Error al procesar meGusta de ${idProducto}:`, err);
                    });
            });

            // 3) Listener para "No me gusta"
            btnDislike.addEventListener('click', () => {
                if (localStorage.getItem(claveLocal)) return;

                fetch(`${INTERACCION_BASE_URL}/${idProducto}/nogusta`, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' }
                })
                    .then(res => {
                        if (!res.ok) {
                            console.error(`Error POST /noGusta de ${idProducto}: ${res.status}`);
                            return;
                        }
                        // Guardar en localStorage y deshabilitar botones
                        localStorage.setItem(claveLocal, 'votado');
                        btnLike.disabled    = true;
                        btnDislike.disabled = true;
                        // Recargar contadores
                        return fetch(`${INTERACCION_BASE_URL}/producto/${idProducto}`, {
                            method: 'GET',
                            headers: { 'Content-Type': 'application/json' }
                        });
                    })
                    .then(res2 => {
                        if (!res2) return;
                        if (!res2.ok) {
                            console.error(`Error al recargar conteo noGusta ${idProducto}: ${res2.status}`);
                            return;
                        }
                        return res2.json();
                    })
                    .then(data2 => {
                        if (!data2) return;
                        likeSpan.textContent    = data2.meGusta ?? 0;
                        dislikeSpan.textContent = data2.noGusta ?? 0;
                    })
                    .catch(err => {
                        console.error(`Error al procesar noGusta de ${idProducto}:`, err);
                    });
            });
        });
    }

    // --- Función para cargar todos los productos de la cafetería especificada ---
    async function cargarProductosDeCafeteria() {
        const url = `${API_BASE_URL}/porCafeteria/${ID_CAFETERIA_FILTRO}`;
        const productos = await fetchData(url);
        if (productos) {
            productosFiltradosCafeteria = productos;
        } else {
            productosFiltradosCafeteria = [];
            console.error(`No se pudieron cargar productos para la cafetería ${ID_CAFETERIA_FILTRO}. Las secciones podrían aparecer vacías.`);
            if (desayunoContentContainer) {
                desayunoContentContainer.innerHTML = `<p>Error al cargar productos de la cafetería. Intente más tarde.</p>`;
            }
        }
    }

    // --- Breakfast Logic (Modificada) ---
    async function loadDesayunos() {
        if (!desayunoContentContainer) {
            console.error("Breakfast content container not found.");
            return;
        }
        desayunoContentContainer.innerHTML = '';
        const desayunoDayTabsElement = document.getElementById('desayunoTabs');
        if (desayunoDayTabsElement) {
            desayunoDayTabsElement.style.display = 'none';
        }

        const productosDesayuno = productosFiltradosCafeteria.filter(p => p.nivel === 'Desayuno');
        if (!productosDesayuno || productosDesayuno.length === 0) {
            desayunoContentContainer.innerHTML = '<p>No hay desayunos disponibles por el momento para esta cafetería.</p>';
            return;
        }

        let rowDiv;
        productosDesayuno.forEach((producto, index) => {
            if (index % 2 === 0) {
                rowDiv = document.createElement('div');
                rowDiv.className = 'row justify-content-center mb-4';
                desayunoContentContainer.appendChild(rowDiv);
            }
            const colDiv = document.createElement('div');
            colDiv.className = 'col-md-3 mb-3';

            const productoHtml = `
                <div class="combo-card mx-auto h-100">
                    <img src="${createProductImageSrc(producto.imagenProducto)}" alt="${producto.nombreProducto}" style="width: 100%; max-height: 200px; object-fit: cover; border-radius: 5px 5px 0 0;">
                    <div style="padding: 15px;">
                        <h5>${producto.nombreProducto}</h5>
                        <p><strong>Precio: $${producto.precio.toLocaleString('es-CO')}</strong></p>
                        ${createLikeDislikeHtml(producto.idProducto, 'DESAYUNO')}
                    </div>
                </div>
            `;
            colDiv.innerHTML = productoHtml;
            rowDiv.appendChild(colDiv);
        });

        attachLikeDislikeHandlers();
    }

    // --- Lunch Logic (Modificada) ---
    let ingredientesPorDiaGlobal = null;

    async function fetchIngredientesPorDia() {
        if (!ingredientesPorDiaGlobal) {
            const data = await fetchData(`${API_BASE_URL}/ingredientesPorDia`);
            if (data && data.ingredientesPorDia) {
                ingredientesPorDiaGlobal = data.ingredientesPorDia;
            } else {
                console.error("Failed to fetch or parse ingredientesPorDia");
                ingredientesPorDiaGlobal = {};
            }
        }
        return ingredientesPorDiaGlobal;
    }

    async function loadAlmuerzosPorDia(dayKey) {
        const container = lunchDayContentPanes[dayKey];
        if (!container) {
            console.error(`Container for ${dayKey} lunch not found.`);
            return;
        }
        container.innerHTML = '<div class="text-center"><p>Cargando almuerzos...</p></div>';

        const ingredientesMap = await fetchIngredientesPorDia();
        if (!ingredientesMap || !ingredientesMap[dayKey] || ingredientesMap[dayKey].length === 0) {
            container.innerHTML = '<div class="text-center"><p>No hay almuerzos programados para este día.</p></div>';
            return;
        }

        const productoIds = ingredientesMap[dayKey];
        const productosDelDia = [];

        for (const id of productoIds) {
            const producto = productosFiltradosCafeteria.find(p => p.idProducto === id);
            if (producto) {
                productosDelDia.push(producto);
            } else {
                console.warn(`Producto con ID ${id} (planificado para ${dayKey}) no encontrado en la cafetería ${ID_CAFETERIA_FILTRO} o no existe.`);
            }
        }

        if (productosDelDia.length === 0) {
            container.innerHTML = '<div class="text-center"><p>No se pudieron cargar los detalles de los almuerzos para este día en esta cafetería.</p></div>';
            return;
        }

        renderAlmuerzoDelDia(productosDelDia, container, dayKey);
        attachLikeDislikeHandlers();
    }

    function renderAlmuerzoDelDia(productos, container, dayKey) {
        container.innerHTML = '';
        const dayNames = { LUN: "Lunes", MAR: "Martes", MIER: "Miércoles", JUE: "Jueves", VIER: "Viernes" };
        const comboDelDiaCard = document.createElement('div');
        comboDelDiaCard.className = 'combo-card mx-auto p-3';
        comboDelDiaCard.innerHTML = `<h4 class="text-center mb-4">Combo ${dayNames[dayKey]} - Almuerzo Completo</h4>`;

        const tiposDeProductoConfig = {
            Proteina:       { titulo: 'Proteína:', productos: [], tipoValor: "Proteina" },
            Acompanamiento: { titulo: 'Acompañamiento:', productos: [], tipoValor: "Acompanamiento" },
            Ensalada:       { titulo: 'Ensalada:', productos: [], tipoValor: "Ensalada" },
            Carbohidrato:   { titulo: 'Carbohidrato:', productos: [], tipoValor: "Carbohidrato" },
            Sopa:           { titulo: 'Sopa del Día:', productos: [], tipoValor: "Sopa" },
            Jugo:           { titulo: 'Jugo del Día:', productos: [], tipoValor: "Jugo" }
        };

        productos.forEach(p => {
            const tipoKey = Object.keys(tiposDeProductoConfig).find(key => tiposDeProductoConfig[key].tipoValor === p.tipo);
            if (tipoKey && tiposDeProductoConfig[tipoKey]) {
                tiposDeProductoConfig[tipoKey].productos.push(p);
            }
        });

        for (const tipoKey in tiposDeProductoConfig) {
            const tipoData = tiposDeProductoConfig[tipoKey];
            const sectionDiv = document.createElement('div');
            sectionDiv.className = 'combo-section mb-4';
            sectionDiv.innerHTML = `<p class="mb-3"><strong>${tipoData.titulo}</strong></p>`;

            if (tipoData.productos.length > 0) {
                let categoryRowDiv;
                tipoData.productos.forEach((p, index) => {
                    if (index % 2 === 0) {
                        categoryRowDiv = document.createElement('div');
                        categoryRowDiv.className = 'row justify-content-center mb-2';
                        sectionDiv.appendChild(categoryRowDiv);
                    }
                    const productColDiv = document.createElement('div');
                    productColDiv.className = 'col-md-3 mb-3';

                    const productCardHtml = `
                        <div>
                            <img src="${createProductImageSrc(p.imagenProducto)}" alt="${p.nombreProducto}" style="width: 100%; max-height: 180px; object-fit: cover; border-radius: 5px 5px 0 0;">
                            <div style="padding: 10px;" class="d-flex flex-column justify-content-between">
                                <div>
                                    <p class="mt-1 mb-2 text-center" style="font-size: 1em;">${p.nombreProducto}</p>
                                </div>
                                ${createLikeDislikeHtml(p.idProducto, dayKey)}
                            </div>
                        </div>
                    `;
                    productColDiv.innerHTML = productCardHtml;
                    if (categoryRowDiv) categoryRowDiv.appendChild(productColDiv);
                });
            } else {
                sectionDiv.innerHTML += '<p class="text-muted" style="font-size: 0.9em;"><em>Hoy no se encuentran productos de este tipo para el combo.</em></p>';
            }
            comboDelDiaCard.appendChild(sectionDiv);
        }

        let comboPrice = 14700;
        const priceParagraph = document.createElement('p');
        priceParagraph.className = 'text-center mt-4';
        priceParagraph.innerHTML = `<strong>Precio Total del Combo: $${comboPrice.toLocaleString('es-CO')}</strong>`;
        comboDelDiaCard.appendChild(priceParagraph);

        container.appendChild(comboDelDiaCard);
    }

    // --- Carousel Logic (modificada para incluir Me gusta / No me gusta) ---
    async function loadCarouselProductos() {
        if (!carouselInner) {
            console.error("Carousel inner container not found.");
            return;
        }
        carouselInner.innerHTML = '';

        const productosCarousel = productosFiltradosCafeteria.filter(p => p.nivel === 'no');
        if (!productosCarousel || productosCarousel.length === 0) {
            carouselInner.innerHTML = '<div class="carousel-item active"><p class="text-center">No hay productos destacados por el momento para esta cafetería.</p></div>';
            return;
        }

        const itemsPerSlide = 3;
        for (let i = 0; i < productosCarousel.length; i += itemsPerSlide) {
            const slideItems = productosCarousel.slice(i, i + itemsPerSlide);
            const carouselItemDiv = document.createElement('div');
            carouselItemDiv.className = `carousel-item ${i === 0 ? 'active' : ''}`;
            const rowDiv = document.createElement('div');
            rowDiv.className = 'row';

            slideItems.forEach(producto => {
                const colDiv = document.createElement('div');
                colDiv.className = 'col-md-4';
                colDiv.innerHTML = `
                    <div class="product-card">
                        <img src="${createProductImageSrc(producto.imagenProducto)}" alt="${producto.nombreProducto}">
                        <div class="product-info p-2">
                            <h5>${producto.nombreProducto}</h5>
                            <p class="text-muted">${producto.descripcion || 'Descripción no disponible.'}</p>
                            <p><strong>$${producto.precio.toLocaleString('es-CO')}</strong></p>
                            <span class="badge bg-success">Disponibles: ${producto.cantidad}</span>
                            ${createLikeDislikeHtml(producto.idProducto, 'PRODUCTO')}
                        </div>
                    </div>
                `;
                rowDiv.appendChild(colDiv);
            });

            carouselItemDiv.appendChild(rowDiv);
            carouselInner.appendChild(carouselItemDiv);
        }

        // Una vez que los botones de like/dislike están en el DOM, atachamos sus handlers
        attachLikeDislikeHandlers();
    }

    // --- Tab Event Listeners & Initial Load ---
    function setupTabListeners() {
        if (desayunoTab) {
            desayunoTab.addEventListener('shown.bs.tab', () => {
                loadDesayunos();
                Object.values(lunchDayTabs).forEach(tab => tab?.classList.remove('active'));
                Object.values(lunchDayContentPanes).forEach(pane => pane?.classList.remove('show', 'active'));
                const desayunoDailyTabsContainer = document.getElementById('desayunoTabs');
                if (desayunoDailyTabsContainer) desayunoDailyTabsContainer.style.display = 'none';
            });
        }

        if (almuerzoTab) {
            almuerzoTab.addEventListener('shown.bs.tab', () => {
                if (desayunoContentContainer) desayunoContentContainer.innerHTML = '';
                const LUNES_KEY = 'LUN';
                if (lunchDayTabs[LUNES_KEY]) {
                    for (const dayKey in lunchDayTabs) {
                        if (dayKey !== LUNES_KEY) {
                            lunchDayTabs[dayKey]?.classList.remove('active');
                            lunchDayTabs[dayKey]?.setAttribute('aria-selected', 'false');
                            lunchDayContentPanes[dayKey]?.classList.remove('show', 'active');
                        }
                    }
                    lunchDayTabs[LUNES_KEY].classList.add('active');
                    lunchDayTabs[LUNES_KEY].setAttribute('aria-selected', 'true');
                    if (lunchDayContentPanes[LUNES_KEY]) {
                        lunchDayContentPanes[LUNES_KEY].classList.add('show', 'active');
                    }
                    loadAlmuerzosPorDia(LUNES_KEY);
                } else {
                    loadAlmuerzosPorDia(LUNES_KEY);
                }
            });
        }

        for (const dayKey in lunchDayTabs) {
            const tabElement = lunchDayTabs[dayKey];
            if (tabElement) {
                tabElement.addEventListener('shown.bs.tab', () => {
                    loadAlmuerzosPorDia(dayKey);
                });
            }
        }
    }

    async function initializePage() {
        const desayunoDayTabsUL = document.getElementById('desayunoTabs');
        if (desayunoDayTabsUL) desayunoDayTabsUL.style.display = 'none';

        const desayunoDayContentWrappers = document.querySelectorAll('#desayuno > .tab-content > .tab-pane');
        desayunoDayContentWrappers.forEach(wrapper => {
            if (wrapper.id !== 'desayuno') {
                wrapper.innerHTML = '';
                wrapper.classList.remove('active', 'show');
            }
        });

        if (desayunoContentContainer) {
            desayunoContentContainer.innerHTML = '';
        }

        // 1. Cargar productos de la cafetería
        await cargarProductosDeCafeteria();

        // 2. Pre-cargar plan de ingredientes para almuerzos
        await fetchIngredientesPorDia();

        // 3. Configurar listeners para las pestañas
        setupTabListeners();

        // 4. Cargar contenido inicial según la pestaña activa
        if (desayunoTab && desayunoTab.classList.contains('active')) {
            await loadDesayunos();
        } else if (almuerzoTab && almuerzoTab.classList.contains('active')) {
            const LUNES_KEY = 'LUN';
            if (lunchDayTabs[LUNES_KEY] && lunchDayContentPanes[LUNES_KEY]) {
                for (const dayKey in lunchDayTabs) {
                    if (dayKey !== LUNES_KEY) {
                        lunchDayTabs[dayKey]?.classList.remove('active');
                        lunchDayTabs[dayKey]?.setAttribute('aria-selected', 'false');
                        lunchDayContentPanes[dayKey]?.classList.remove('show', 'active');
                    }
                }
                lunchDayTabs[LUNES_KEY].classList.add('active');
                lunchDayTabs[LUNES_KEY].setAttribute('aria-selected', 'true');
                lunchDayContentPanes[LUNES_KEY].classList.add('show', 'active');
                await loadAlmuerzosPorDia(LUNES_KEY);
            }
        } else {
            await loadDesayunos();
        }

        // 5. Cargar el carrusel de productos (ahora con Me gusta / No me gusta)
        await loadCarouselProductos();
    }

    initializePage();
});