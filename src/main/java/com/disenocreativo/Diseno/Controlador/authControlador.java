package com.disenocreativo.Diseno.Controlador;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import com.disenocreativo.Diseno.DTO.authRespuesta;
import com.disenocreativo.Diseno.DTO.inicioPeticion;
import com.disenocreativo.Diseno.DTO.registroPeticion;
import com.disenocreativo.Diseno.Servicio.authServicio;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class authControlador {
    private final authServicio AuthService;
    
   @PostMapping(value = "login")
    public ResponseEntity<authRespuesta> login(@RequestBody inicioPeticion request) {
        // Limpiar espacios en correo y contraseña
        String correoTrim = request.getCorreo() != null ? request.getCorreo().trim() : null;
        String passwordTrim = request.getPassword() != null ? request.getPassword().trim() : null;

        // Crear un nuevo objeto inicioPeticion con valores limpios
        inicioPeticion cleanedRequest = new inicioPeticion();
        cleanedRequest.setCorreo(correoTrim);
        cleanedRequest.setPassword(passwordTrim);

        return ResponseEntity.ok(AuthService.login(cleanedRequest));
    }
    @PostMapping(value = "register")
    public ResponseEntity<authRespuesta> register(@RequestBody registroPeticion request)
    {
        return ResponseEntity.ok(AuthService.register(request));
    }  
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handleInvalidRequestException(IllegalArgumentException ex) {
        String message = ex.getMessage();
        if (message.contains("correo ya está en uso")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("correo ya en uso");
        } else if (message.contains("contraseña")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("contraseña inválida");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ocurrió un error inesperado");
        }
    }
}