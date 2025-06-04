(function ($) {
    "use strict";
  
    // Sticky Navbar
    $(window).scroll(function () {
      if ($(this).scrollTop() > 40) {
        $('.navbar').addClass('sticky-top');
      } else {
        $('.navbar').removeClass('sticky-top');
      }
    });
  
    // Dropdown on mouse hover
    $(document).ready(function () {
      function toggleNavbarMethod() {
        if ($(window).width() > 992) {
          $('.navbar .dropdown').on('mouseover', function () {
            $('.dropdown-toggle', this).trigger('click');
          }).on('mouseout', function () {
            $('.dropdown-toggle', this).trigger('click').blur();
          });
        } else {
          $('.navbar .dropdown').off('mouseover').off('mouseout');
        }
      }
      toggleNavbarMethod();
      $(window).resize(toggleNavbarMethod);
    });
  
    // Modal Video
    $(document).ready(function () {
      var $videoSrc;
      $('.btn-play').click(function () {
        $videoSrc = $(this).data("src");
      });
  
      $('#videoModal').on('shown.bs.modal', function () {
        $("#video").attr('src', $videoSrc + "?autoplay=1&amp;modestbranding=1&amp;showinfo=0");
      });
  
      $('#videoModal').on('hide.bs.modal', function () {
        $("#video").attr('src', $videoSrc);
      });
    });
  
    // Facts Counter
    $('[data-toggle="counter-up"]').counterUp({
      delay: 10,
      time: 2000
    });
  
    // Testimonials carousel
    $(".testimonial-carousel").owlCarousel({
      autoplay: true,
      smartSpeed: 1500,
      margin: 45,
      dots: true,
      loop: true,
      center: true,
      responsive: {
        0: { items: 1 },
        576: { items: 1 },
        768: { items: 2 },
        992: { items: 3 }
      }
    });
  
    // ===========================
    // Sección: Rotar Platos Favoritos
    // ===========================
// ===========================
$(document).ready(function () {
  const endpoint = "/interaccion/favoritos";
  const $cardContainer = $("#favoritosContainer");
  const template = $("#favoritoTemplate")[0];
  let favoritos = [];
  let currentIndex = 0;
  const visibleCards = 3;
  const rotationInterval = 5000; // milisegundos

  // Solicita los favoritos al backend
  $.ajax({
      url: endpoint,
      method: "GET",
      success: function (data) {
          favoritos = data.slice(0, 10);
          if (favoritos.length === 0) {
              $cardContainer.html("<p class='text-center'>No hay platos favoritos aún.</p>");
              return;
          }

          renderCards();

          if (favoritos.length > visibleCards) {
              setInterval(function () {
                  currentIndex = (currentIndex + visibleCards) % favoritos.length;
                  renderCards();
              }, rotationInterval);
          }
      },
      error: function (err) {
          console.error("Error al obtener favoritos:", err);
          $cardContainer.html("<p class='text-center text-danger'>Error al cargar los platos favoritos.</p>");
      }
  });

  // Renderiza las tarjetas en pantalla
  function renderCards() {
      $cardContainer.empty();

      for (let i = 0; i < visibleCards; i++) {
          const index = (currentIndex + i) % favoritos.length;
          const fav = favoritos[index];
          const $clone = $(template.content.cloneNode(true));

          // Imagen: si existe la cadena Base64, la usamos; si no hay, usamos la imagen por defecto
          if (fav.imagenProducto) {
              // Jackson nos envia aquí un string Base64, así que directamente lo colocamos.
              $clone
                  .find("img")
                  .attr("src", `data:image/jpeg;base64,${fav.imagenProducto}`);
          } else {
              $clone
                  .find("img")
                  .attr("src", "/Recursos/img/Sin imagen.png"); // tu imagen por defecto
          }
          $clone.find("img").attr("alt", fav.nombreProducto);

          // Título y descripción
          $clone.find("h4").text(fav.nombreProducto);
          $clone.find("p").html(`
              ${fav.descripcionProducto}<br>
              <strong>Cafetería:</strong> ${fav.nombreCafeteria}<br>
              <strong>Precio:</strong> $${fav.precioProducto}
          `);

          // Me gusta / No me gusta
          $clone
              .find(".likes")
              .html(`<i class="bi bi-hand-thumbs-up"></i> ${fav.meGusta} Me gusta`);
          $clone
              .find(".dislikes")
              .html(`<i class="bi bi-hand-thumbs-down"></i> ${fav.noGusta} No me gusta`);

          $cardContainer.append($clone);
      }
  }
});
    
  
  })(jQuery);