package com.disenocreativo.Diseno.Controlador;

import com.disenocreativo.Diseno.Entidad.administrador;
import com.disenocreativo.Diseno.DTO.administradorDTO;
import com.disenocreativo.Diseno.Servicio.administradorServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/administradores") // Ruta base para administradores
public class administradorControlador {

    @Autowired
    private administradorServicio servicio;

    // Guardar un nuevo administrador
    // La imagen dice ResponseEntity<Administrador>, pero es mejor devolver el DTO o solo estado.
    // Para mantener consistencia con la imagen en el tipo de retorno del cuerpo (Administrador),
    // pero teniendo en cuenta que la entidad tiene contraseña, se devolverá la entidad guardada.
    // Sin embargo, es más seguro devolver un DTO sin la contraseña.
    // Optaré por devolver la entidad completa como en la imagen, pero con una advertencia.
    @PostMapping
    public ResponseEntity<administrador> guardar(@RequestBody administrador admin) {
        try {
            // ADVERTENCIA: Devolver la entidad completa puede exponer la contraseña si no se maneja adecuadamente
            // (ej. con @JsonIgnore en la entidad para el campo contraseña en respuestas).
            // Es preferible devolver un DTO.
            administrador adminGuardado = servicio.guardarAdministrador(admin);
            return new ResponseEntity<>(adminGuardado, HttpStatus.CREATED);
        } catch (Exception e) {
            // Considerar un manejo de errores más específico
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Eliminar un administrador por ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarId(@PathVariable Long id) {
        try {
            administradorDTO adminExistente = servicio.buscarAdministradorPorId(id);
            if (adminExistente != null) {
                servicio.eliminarAdministradorPorId(id);
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Listar todos los administradores (como DTOs)
    @GetMapping
    public ResponseEntity<List<administradorDTO>> listar() {
        try {
            List<administradorDTO> administradores = servicio.listarAdministradores();
            if (administradores.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(administradores, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Buscar administrador por ID (devuelve DTO)
    // La imagen dice ResponseEntity<Administrador>, pero el servicio devuelve DTO.
    // Se usará DTO para la respuesta.
    @GetMapping("/{id}")
    public ResponseEntity<administradorDTO> buscarId(@PathVariable Long id) {
        administradorDTO adminDTO = servicio.buscarAdministradorPorId(id);
        if (adminDTO != null) {
            return new ResponseEntity<>(adminDTO, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Buscar administradores por nombre (devuelve lista de DTOs)
    // La imagen dice ResponseEntity<Administrador>, pero el servicio devuelve DTO y podría ser una lista.
    // Se usará List<Administrador>.
    @GetMapping("/buscarPorNombre")
    public ResponseEntity<List<administradorDTO>> buscarNombre(@RequestParam String nombre) {
        List<administradorDTO> adminsDTO = servicio.buscarAdministradoresPorNombre(nombre);
        if (adminsDTO != null && !adminsDTO.isEmpty()) {
            return new ResponseEntity<>(adminsDTO, HttpStatus.OK);
        } else {
            // Podría ser NO_CONTENT si la lista está vacía pero la búsqueda fue exitosa,
            // o NOT_FOUND si se considera que "no encontrar nada" es un error.
            // Por consistencia, si la lista está vacía, devolver NO_CONTENT o una lista vacía con OK.
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // O return new ResponseEntity<>(adminsDTO, HttpStatus.OK);
        }
    }


    // Buscar administrador por telefono (devuelve DTO)
    // La imagen dice ResponseEntity<Administrador>, pero el servicio devuelve DTO.
    // Se usará DTO para la respuesta.
    @GetMapping("/buscarPorTelefono")
    public ResponseEntity<administradorDTO> buscarTelefono(@RequestParam String telefono) {
        administradorDTO adminDTO = servicio.buscarAdministradorPorTelefono(telefono);
        if (adminDTO != null) {
            return new ResponseEntity<>(adminDTO, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /* Endpoint para login (ejemplo)
    @PostMapping("/login")
    public ResponseEntity<administradorDTO> login(@RequestBody LoginRequest loginRequest) {
        Optional<administradorDTO> adminDTOOpt = servicio.validarCredenciales(loginRequest.getCorreo(), loginRequest.getContrasena());
        return adminDTOOpt
                .map(adminDTO -> new ResponseEntity<>(adminDTO, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.UNAUTHORIZED));
    }
    */

    // Clase auxiliar para el request de login
    public static class LoginRequest {
        private String correo;
        private String contrasena;

        public String getCorreo() { return correo; }
        public void setCorreo(String correo) { this.correo = correo; }
        public String getContrasena() { return contrasena; }
        public void setContrasena(String contrasena) { this.contrasena = contrasena; }
    }
}