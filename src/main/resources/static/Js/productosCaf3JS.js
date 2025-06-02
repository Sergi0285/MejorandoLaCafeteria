document.addEventListener('DOMContentLoaded', () => {
    const API_BASE_URL = 'http://localhost:8080/productos';
    const token = localStorage.getItem('token');

    // --- DOM Elements ---
    const desayunoTab = document.getElementById('desayuno-tab'); //
    const almuerzoTab = document.getElementById('almuerzo-tab'); //
    const desayunoContentContainer = document.querySelector('#desayuno > .tab-content.text-center'); //
    // const almuerzoDayContentContainerBase = document.querySelector('#almuerzo > .tab-content'); // // No se usa directamente, se usan los panes.

    const lunchDayTabs = { //
        LUN: document.getElementById('lunesAlmuerzo-tab'), //
        MAR: document.getElementById('martesAlmuerzo-tab'), //
        MIER: document.getElementById('miercolesAlmuerzo-tab'), //
        JUE: document.getElementById('juevesAlmuerzo-tab'), //
        VIER: document.getElementById('viernesAlmuerzo-tab') //
    };
    const lunchDayContentPanes = { //
        LUN: document.getElementById('lunesAlmuerzo'), //
        MAR: document.getElementById('martesAlmuerzo'), //
        MIER: document.getElementById('miercolesAlmuerzo'), //
        JUE: document.getElementById('juevesAlmuerzo'), //
        VIER: document.getElementById('viernesAlmuerzo') //
    };
    const carouselInner = document.querySelector('#productosCarrusel .carousel-inner'); //

    // --- ID de la Cafetería para filtrar ---
    const ID_CAFETERIA_FILTRO = 3;
    let productosFiltradosCafeteria = []; // Almacenará los productos de la cafetería 6

    // --- Helper Functions ---
    async function fetchData(url, options = {}) {
        const headers = new Headers({
            'Content-Type': 'application/json',
        });
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

    function createLikeDislikeHtml(likes = 0, dislikes = 0) {
        const randomLikes = Math.floor(Math.random() * 50) + 100;
        const randomDislikes = Math.floor(Math.random() * 10) + 1;
        return `
            <div class="like-dislike-container">
                <span>👍 ${randomLikes} Me gusta</span>
                <span>👎 ${randomDislikes} No me gusta</span>
            </div>
        `;
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
            // Opcionalmente, mostrar un mensaje al usuario en la página
            if(desayunoContentContainer) desayunoContentContainer.innerHTML = `<p>Error al cargar productos de la cafetería. Intente más tarde.</p>`;
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

        // Filtrar de la lista precargada
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
            colDiv.className = 'col-md-3 mb-3'; // Ajustado de acuerdo a la conversación previa

            const productoHtml = `
                <div class="combo-card mx-auto h-100">
                    <img src="${createProductImageSrc(producto.imagenProducto)}" alt="${producto.nombreProducto}" style="width: 100%; max-height: 200px; object-fit: cover; border-radius: 5px 5px 0 0;">
                    <div style="padding: 15px;">
                        <h5>${producto.nombreProducto}</h5>
                        <p><strong>Precio: $${producto.precio.toLocaleString('es-CO')}</strong></p>
                        ${createLikeDislikeHtml()}
                    </div>
                </div>
            `;
            colDiv.innerHTML = productoHtml;
            if (rowDiv) rowDiv.appendChild(colDiv);
        });
    }

    // --- Lunch Logic (Modificada) ---
    let ingredientesPorDiaGlobal = null;

    async function fetchIngredientesPorDia() {
        // Esta función no cambia, ya que define el "plan" del menú, no los detalles del producto en sí.
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
        const container = lunchDayContentPanes[dayKey]; //
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
            // Buscar el producto por ID en la lista precargada y filtrada de la cafetería
            // Asegúrate que 'idProducto' es el nombre correcto del campo ID en tus objetos de producto.
            // Podría ser 'id' o 'IDProducto', etc., dependiendo de tu backend.
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
    }
    
    function renderAlmuerzoDelDia(productos, container, dayKey) {
        container.innerHTML = ''; 
        const dayNames = { LUN: "Lunes", MAR: "Martes", MIER: "Miércoles", JUE: "Jueves", VIER: "Viernes"};
        const comboDelDiaCard = document.createElement('div');
        comboDelDiaCard.className = 'combo-card mx-auto p-3';
        comboDelDiaCard.innerHTML = `<h4 class="text-center mb-4">Combo ${dayNames[dayKey]} - Almuerzo Completo</h4>`;

        const tiposDeProductoConfig = {
            Proteina: { titulo: 'Proteína:', productos: [], tipoValor: "Proteina" },
            Acompanamiento: { titulo: 'Acompañamiento:', productos: [], tipoValor: "Acompanamiento" },
            Ensalada: { titulo: 'Ensalada:', productos: [], tipoValor: "Ensalada" },
            Carbohidrato: { titulo: 'Carbohidrato:', productos: [], tipoValor: "Carbohidrato" },
            Sopa: { titulo: 'Sopa del Día:', productos: [], tipoValor: "Sopa" },
            Jugo: { titulo: 'Jugo del Día:', productos: [], tipoValor: "Jugo" }
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
                    productColDiv.className = 'col-md-3 mb-3'; // Usando col-md-5 como se ajustó antes

                    const productCardHtml = `
                        <div class="">
                            <img src="${createProductImageSrc(p.imagenProducto)}" alt="${p.nombreProducto}" style="width: 100%; max-height: 180px; object-fit: cover; border-radius: 5px 5px 0 0;">
                            <div style="padding: 10px;" class="d-flex flex-column justify-content-between">
                                <div>
                                    <p class="mt-1 mb-2 text-center" style="font-size: 1em;">${p.nombreProducto}</p>
                                </div>
                                ${createLikeDislikeHtml()}
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

    // --- Carousel Logic (Modificada) ---
    async function loadCarouselProductos() {
        if (!carouselInner) {
            console.error("Carousel inner container not found.");
            return;
        }
        carouselInner.innerHTML = ''; 

        // Filtrar de la lista precargada
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
                        <div class="product-info">
                            <h5>${producto.nombreProducto}</h5>
                            <p class="text-muted">${producto.descripcion || 'Descripción no disponible.'}</p>
                            <p><strong>$${producto.precio.toLocaleString('es-CO')}</strong></p>
                            <span class="badge bg-success">Disponibles: ${producto.cantidad}</span>
                        </div>
                    </div>
                `;
                rowDiv.appendChild(colDiv);
            });
            carouselItemDiv.appendChild(rowDiv);
            carouselInner.appendChild(carouselItemDiv);
        }
    }

    // --- Tab Event Listeners & Initial Load ---
    function setupTabListeners() {
        if (desayunoTab) { //
            desayunoTab.addEventListener('shown.bs.tab', () => { //
                loadDesayunos();
                Object.values(lunchDayTabs).forEach(tab => tab?.classList.remove('active')); //
                Object.values(lunchDayContentPanes).forEach(pane => pane?.classList.remove('show', 'active')); //
                const desayunoDailyTabsContainer = document.getElementById('desayunoTabs'); //
                if (desayunoDailyTabsContainer) desayunoDailyTabsContainer.style.display = 'none'; //
            });
        }

        if (almuerzoTab) { //
            almuerzoTab.addEventListener('shown.bs.tab', () => { //
                if(desayunoContentContainer) desayunoContentContainer.innerHTML = ''; //
                
                // Activar Lunes por defecto
                const LUNES_KEY = 'LUN';
                if (lunchDayTabs[LUNES_KEY]) { //
                     // Desactivar otras pestañas de almuerzo antes de mostrar Lunes
                    for (const dayKey in lunchDayTabs) {
                        if (dayKey !== LUNES_KEY) {
                            lunchDayTabs[dayKey]?.classList.remove('active');
                            lunchDayTabs[dayKey]?.setAttribute('aria-selected', 'false');
                            lunchDayContentPanes[dayKey]?.classList.remove('show', 'active');
                        }
                    }
                    // Activar Lunes
                    lunchDayTabs[LUNES_KEY].classList.add('active');
                    lunchDayTabs[LUNES_KEY].setAttribute('aria-selected', 'true');
                    if (lunchDayContentPanes[LUNES_KEY]) { //
                        lunchDayContentPanes[LUNES_KEY].classList.add('show', 'active');
                    }
                    loadAlmuerzosPorDia(LUNES_KEY);
                } else {
                    loadAlmuerzosPorDia(LUNES_KEY); // Fallback
                }
            });
        }

        for (const dayKey in lunchDayTabs) { //
            const tabElement = lunchDayTabs[dayKey]; //
            if (tabElement) {
                tabElement.addEventListener('shown.bs.tab', () => { //
                    loadAlmuerzosPorDia(dayKey);
                });
            }
        }
    }
    
    async function initializePage() {
        const desayunoDayTabsUL = document.getElementById('desayunoTabs'); //
        if (desayunoDayTabsUL) desayunoDayTabsUL.style.display = 'none'; //
        
        const desayunoDayContentWrappers = document.querySelectorAll('#desayuno > .tab-content > .tab-pane'); //
        desayunoDayContentWrappers.forEach(wrapper => { //
            if (wrapper.id !== 'desayuno') { //
                 wrapper.innerHTML = ''; //
                 wrapper.classList.remove('active', 'show'); //
            }
        });
        
        if (desayunoContentContainer) { //
             desayunoContentContainer.innerHTML = ''; //
        }

        // 1. Cargar todos los productos de la cafetería especificada
        await cargarProductosDeCafeteria();

        // 2. Pre-cargar el plan de ingredientes para los almuerzos
        await fetchIngredientesPorDia(); 

        // 3. Configurar listeners para las pestañas
        setupTabListeners(); //

        // 4. Cargar contenido de la pestaña activa inicial y el carrusel
        if (desayunoTab && desayunoTab.classList.contains('active')) { //
            await loadDesayunos();
        } else if (almuerzoTab && almuerzoTab.classList.contains('active')) { //
            const LUNES_KEY = 'LUN';
            if (lunchDayTabs[LUNES_KEY] && lunchDayContentPanes[LUNES_KEY]) { //
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
            // Si ninguna pestaña principal está marcada como activa, cargar desayunos por defecto
            await loadDesayunos();
        }
        
        await loadCarouselProductos(); //
    }

    initializePage(); //
});