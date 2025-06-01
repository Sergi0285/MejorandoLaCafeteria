document.addEventListener('DOMContentLoaded', () => {
    const API_BASE_URL = 'http://localhost:8080/productos';
    const token = localStorage.getItem('token'); // Assuming token might be needed for GET requests

    // --- DOM Elements ---
    // Main tabs
    const desayunoTab = document.getElementById('desayuno-tab');
    const almuerzoTab = document.getElementById('almuerzo-tab');

    // Content containers
    const desayunoContentContainer = document.querySelector('#desayuno > .tab-content.text-center');
    const almuerzoDayContentContainerBase = document.querySelector('#almuerzo > .tab-content'); // Base for daily lunch content

    // Lunch day tabs
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
            if (response.status === 204 || response.headers.get("content-length") === "0") { // No content
                 return []; // Or null, depending on how you want to handle it
            }
            return await response.json();
        } catch (error) {
            console.error(`Network error or other issue fetching ${url}:`, error);
            return null;
        }
    }

    function createProductImageSrc(base64String) {
        if (!base64String) return '../Recursos/img/placeholder.PNG'; // Fallback image
        return `data:image/jpeg;base64,${base64String}`;
    }

    function createLikeDislikeHtml(likes = 0, dislikes = 0) {
        // Using placeholder values as requested
        const randomLikes = Math.floor(Math.random() * 50) + 100; // Random likes between 100-149
        const randomDislikes = Math.floor(Math.random() * 10) + 1;   // Random dislikes between 1-10
        return `
            <div class="like-dislike-container">
                <span>👍 ${randomLikes} Me gusta</span>
                <span>👎 ${randomDislikes} No me gusta</span>
            </div>
        `;
    }

    // --- Breakfast Logic ---
    async function loadDesayunos() {
        if (!desayunoContentContainer) {
            console.error("Breakfast content container not found.");
            return;
        }
        // Clear any existing content (e.g., the static lunch example under breakfast tab)
        desayunoContentContainer.innerHTML = '';
         // Hide the daily tabs within the breakfast section if they exist
        const desayunoDayTabsElement = document.getElementById('desayunoTabs');
        if (desayunoDayTabsElement) {
            desayunoDayTabsElement.style.display = 'none';
        }


        const productosDesayuno = await fetchData(`${API_BASE_URL}/buscarPorNivel?nivel=Desayuno`);
        if (!productosDesayuno || productosDesayuno.length === 0) {
            desayunoContentContainer.innerHTML = '<p>No hay desayunos disponibles por el momento.</p>';
            return;
        }

        let rowDiv;
        productosDesayuno.forEach((producto, index) => {
            if (index % 2 === 0) { // Start a new row for every 2 products
                rowDiv = document.createElement('div');
                rowDiv.className = 'row justify-content-center mb-4'; // Added justify-content-center
                desayunoContentContainer.appendChild(rowDiv);
            }

            const colDiv = document.createElement('div');
            colDiv.className = 'col-md-5'; // Use col-md-5 for slightly more space if needed, or col-md-6 for exact half

            const productoHtml = `
                <div class="combo-card mx-auto"> <img src="${createProductImageSrc(producto.imagenProducto)}" alt="${producto.nombreProducto}" style="width: 100%; max-height: 200px; object-fit: cover; border-radius: 5px 5px 0 0;">
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

    // --- Lunch Logic ---
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
            const producto = await fetchData(`${API_BASE_URL}/${id}`);
            if (producto) {
                productosDelDia.push(producto);
            }
        }

        if (productosDelDia.length === 0) {
            container.innerHTML = '<div class="text-center"><p>No se pudieron cargar los detalles de los almuerzos para este día.</p></div>';
            return;
        }

        renderAlmuerzoDelDia(productosDelDia, container, dayKey);
    }
    
    function renderAlmuerzoDelDia(productos, container, dayKey) {
        container.innerHTML = ''; 

        const dayNames = { LUN: "Lunes", MAR: "Martes", MIER: "Miércoles", JUE: "Jueves", VIER: "Viernes"};
        // Main card for the entire day's combo
        const comboDelDiaCard = document.createElement('div');
        comboDelDiaCard.className = 'combo-card mx-auto p-3'; // Added padding to the main combo card
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
            sectionDiv.className = 'combo-section mb-4'; // Increased bottom margin for section spacing

            sectionDiv.innerHTML = `<h5 class="mb-3"><strong>${tipoData.titulo}</strong></h5>`; // Title for the section (e.g., "Proteína:")

            if (tipoData.productos.length > 0) {
                let categoryRowDiv; // This will hold two product cards
                tipoData.productos.forEach((p, index) => {
                    if (index % 2 === 0) { // Start a new row for every 2 products in this category
                        categoryRowDiv = document.createElement('div');
                        categoryRowDiv.className = 'row justify-content-center mb-2'; // mb-2 for spacing between rows of products
                        sectionDiv.appendChild(categoryRowDiv);
                    }

                    const productColDiv = document.createElement('div');
                    // col-md-6 to make two items fit per row.
                    // Use col-md-5 if you want slightly more spacing similar to breakfast and have justify-content-center on categoryRowDiv.
                    productColDiv.className = 'col-md-4 mb-3'; // mb-3 for spacing below each card

                    // Create the card for the product, styled like breakfast items
                    const productCardHtml = `
                        <div class="combo-card mx-auto h-100">
                            <img src="${createProductImageSrc(p.imagenProducto)}" alt="${p.nombreProducto}" style="width: 100%; max-height: 180px; object-fit: cover; border-radius: 5px 5px 0 0;">
                            <div style="padding: 10px;" class="d-flex flex-column justify-content-between">
                                <div>
                                    <p class="mt-1 mb-2 text-center" style="font-size: 1em;"><strong>${p.nombreProducto}</strong></p>
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


    // --- Carousel Logic ---
    async function loadCarouselProductos() {
        if (!carouselInner) {
            console.error("Carousel inner container not found.");
            return;
        }
        carouselInner.innerHTML = ''; // Clear existing items

        const productosCarousel = await fetchData(`${API_BASE_URL}/buscarPorNivel?nivel=no`);

        if (!productosCarousel || productosCarousel.length === 0) {
            carouselInner.innerHTML = '<div class="carousel-item active"><p class="text-center">No hay productos destacados por el momento.</p></div>';
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
        if (desayunoTab) {
            desayunoTab.addEventListener('shown.bs.tab', () => {
                loadDesayunos();
                 // Ensure lunch day tabs are not active if we switch to breakfast
                Object.values(lunchDayTabs).forEach(tab => tab?.classList.remove('active'));
                Object.values(lunchDayContentPanes).forEach(pane => pane?.classList.remove('show', 'active'));

                // Hide breakfast daily sub-tabs (if they were part of the original HTML and not removed)
                const desayunoDailyTabsContainer = document.getElementById('desayunoTabs');
                if (desayunoDailyTabsContainer) desayunoDailyTabsContainer.style.display = 'none';
                const desayunoDailyContentPanes = document.querySelectorAll('#desayuno > .tab-content > .tab-pane');
                 desayunoDailyContentPanes.forEach(pane => {
                    if(pane.id !== 'desayuno') { // Keep the main desayuno pane active, but not sub-panes
                       // This logic might need refinement based on exact HTML structure for sub-panes of breakfast
                    }
                });
            });
        }

        if (almuerzoTab) {
            almuerzoTab.addEventListener('shown.bs.tab', () => {
                // Activate Lunes by default when switching to Almuerzos tab
                if (lunchDayTabs.LUN) {
                     // Ensure breakfast content is cleared/hidden
                    if(desayunoContentContainer) desayunoContentContainer.innerHTML = '';

                    new bootstrap.Tab(lunchDayTabs.LUN).show(); // Programmatically show Lunes tab
                    loadAlmuerzosPorDia('LUN');
                } else {
                    // Fallback if Lunes tab isn't found
                    loadAlmuerzosPorDia('LUN'); // Attempt to load, might show error if no container
                }
            });
        }

        // Event listeners for lunch day tabs
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
        // Initial setup based on which main tab is active in HTML (Desayunos)
        // The HTML provided has Desayunos as active.
        // Remove or hide the breakfast daily sub-tabs as they are not needed.
        const desayunoDayTabsUL = document.getElementById('desayunoTabs');
        if (desayunoDayTabsUL) desayunoDayTabsUL.style.display = 'none';
        
        // Clear out any static content within the breakfast daily tab panes if they exist
        const desayunoDayContentWrappers = document.querySelectorAll('#desayuno > .tab-content > .tab-pane');
        desayunoDayContentWrappers.forEach(wrapper => {
            // Keep the main 'desayuno' tab-pane, but clear its children if they are day-specific containers from template
            if (wrapper.id !== 'desayuno') { // Example: #lunesDesayuno, #martesDesayuno
                 wrapper.innerHTML = ''; // Clear them
                 wrapper.classList.remove('active', 'show');
            }
        });
        
        // Ensure the main breakfast container is ready
        if (desayunoContentContainer) {
             desayunoContentContainer.innerHTML = ''; // Clear previous template content like "Combo Lunes - Almuerzo"
        }

        await loadDesayunos(); // Load breakfasts first as it's the default active tab
        await fetchIngredientesPorDia(); // Pre-fetch lunch data
        await loadCarouselProductos();
        setupTabListeners();

        // Correct initial state: If "desayuno-tab" is active, ensure its content area is shown.
        // The HTML already makes "desayuno" tab active and its pane "show active".
        // We just need to ensure the content *within* #desayuno > .tab-content.text-center is populated correctly.
    }

    initializePage();
});