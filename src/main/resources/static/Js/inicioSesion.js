$(document).ready(function () {
  // Botón de inicio de sesión
  $("#loginBtn").click(function () {
    logUsuario();
  });

  function logUsuario() {
    var correo = $("#usuarioInput").val().trim();
    var contrasena = $("#passwordInput").val().trim();

    if (correo === '') {
      Swal.fire({
        icon: 'error',
        title: 'Campo Correo Vacío',
        text: 'Por favor, ingresa tu correo.',
        confirmButtonText: 'Aceptar'
      });
      return;
    }

    if (contrasena === '') {
      Swal.fire({
        icon: 'error',
        title: 'Campo Contraseña Vacío',
        text: 'Por favor, ingresa tu contraseña.',
        confirmButtonText: 'Aceptar'
      });
      return;
    }

    var data = {
      correo: correo,
      password: contrasena
    };

    $.ajax({
      url: '/auth/login',
      type: 'POST',
      contentType: 'application/json',
      data: JSON.stringify(data),
      success: function (response) {
        console.log('Token recibido:', response.token);
        localStorage.setItem('token', response.token);

        var tokenParts = response.token.split('.');
        var tokenPayload = JSON.parse(atob(tokenParts[1]));
        var username = tokenPayload.sub;

        Swal.fire({
          icon: 'success',
          title: `¡Bienvenido, ${username}!`,
          showConfirmButton: false,
          timer: 1500
        }).then(() => {
          $.ajax({
            url: '/controladorCliente/rol',
            type: 'GET',
            headers: {
              'Authorization': 'Bearer ' + response.token
            },
            success: function (role) {
              if (role === 'ADMIN') {
                window.location.href = "/Html/service.html";
              } else if (role === 'USER') {
                window.location.href = "/index.html";
              } else {
                alert('Rol desconocido.');
              }
            },
            error: function (xhr, status, error) {
              console.error('Error al obtener el rol:', error);
              alert('Error al obtener el rol del usuario.');
            }
          });
        });
      },
      error: function (xhr, status, error) {
        Swal.fire({
          icon: 'error',
          title: 'Error en la solicitud',
          text: 'Correo o contraseña incorrectos. Intenta nuevamente.',
          confirmButtonText: 'Aceptar'
        });
      }
    });
  }
});
