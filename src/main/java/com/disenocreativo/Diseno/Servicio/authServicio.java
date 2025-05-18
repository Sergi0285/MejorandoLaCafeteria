package com.disenocreativo.Diseno.Servicio;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.disenocreativo.Diseno.DTO.authRespuesta;
import com.disenocreativo.Diseno.DTO.inicioPeticion;
import com.disenocreativo.Diseno.DTO.registroPeticion;
import com.disenocreativo.Diseno.Entidad.role;
import com.disenocreativo.Diseno.Entidad.usuario;
import com.disenocreativo.Diseno.Repositorio.usuarioRepositorio;
import com.disenocreativo.Diseno.Seguridad.jwtServicio;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class authServicio {
    private final usuarioRepositorio userRepository;
    private final jwtServicio jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    // Método para validar la seguridad de la contraseña
    private boolean isPasswordSecure(String password) {
        int minLength = 8;
        boolean hasUpperCase = password.chars().anyMatch(Character::isUpperCase);
        boolean hasNumber = password.chars().anyMatch(Character::isDigit);
        boolean hasSpecialChar = password.chars().anyMatch(ch -> "!@#$%^&*(),.?\":{}|<>".indexOf(ch) >= 0);
        return password.length() >= minLength && hasUpperCase && hasNumber && hasSpecialChar;
    }

    // Método para el inicio de sesión
    public authRespuesta login(inicioPeticion request) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getCorreo(), request.getPassword())
        );
        UserDetails usuario = userRepository.findByCorreo(request.getCorreo()).orElseThrow();
        String token = jwtService.getToken(usuario);
        return authRespuesta.builder()
            .token(token)
            .build();
    }

    // Método para registrar un nuevo usuario
    public authRespuesta register(registroPeticion request) {
        // Validar la seguridad de la contraseña
        if (!isPasswordSecure(request.getPassword())) {
            throw new IllegalArgumentException("La contraseña no cumple con los criterios de seguridad: " +
                "Debe tener al menos 8 caracteres, incluir una letra mayúscula, un número y un carácter especial.");
        }
        if (userRepository.existsByCorreo(request.getCorreo())) {
            throw new IllegalArgumentException("El correo ya está en uso. Por favor, elija otro.");
        }
        
   

        // Crear y guardar el usuario
        usuario user = usuario.builder()
            .nombre(request.getNombre())
            .correo(request.getCorreo())
            .telefono(request.getTelefono())
            .password(passwordEncoder.encode(request.getPassword()))
            .rol(role.USER)
            .build();

        userRepository.save(user);

        // Retornar el token de autenticación
        return authRespuesta.builder()
            .token(jwtService.getToken(user))
            .build();
   
    }
}
