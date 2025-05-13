// Obtener los elementos del carrusel
const prevBtn = document.getElementById('prevBtn');
const nextBtn = document.getElementById('nextBtn');
const productContainer = document.getElementById('productContainer');

// Manejar el botón de "next" (desplazar a la derecha)
nextBtn.addEventListener('click', () => {
  productContainer.scrollBy({
    left: 300,  // Desplazarse 300px a la derecha
    behavior: 'smooth'  // Efecto de desplazamiento suave
  });
});

// Manejar el botón de "prev" (desplazar a la izquierda)
prevBtn.addEventListener('click', () => {
  productContainer.scrollBy({
    left: -300,  // Desplazarse 300px a la izquierda
    behavior: 'smooth'  // Efecto de desplazamiento suave
  });
});
