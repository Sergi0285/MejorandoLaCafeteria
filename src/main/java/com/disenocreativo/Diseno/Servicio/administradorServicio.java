package com.disenocreativo.Diseno.Servicio;

import com.disenocreativo.Diseno.Entidad.administrador;
import com.disenocreativo.Diseno.DTO.administradorDTO; // Asegúrate que la ruta sea correcta
import com.disenocreativo.Diseno.Repositorio.administradorRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class administradorServicio {

    @Autowired
    private administradorRepositorio repositorio;

    // Método para convertir Entidad a DTO
    private administradorDTO convertirAAdministradorDTO(administrador admin) {
        if (admin == null) {
            return null;
        }
        return new administradorDTO(
                admin.getIdAdministrador(),
                admin.getNombre(),
                admin.getCelular(),
                admin.getCorreo()
        );
    }

    @Transactional
    public administrador guardarAdministrador(administrador admin) {
        // Aquí podrías añadir validaciones, como verificar si el correo ya existe
        // o encriptar la contraseña antes de guardarla.
        // Ejemplo de encriptación (necesitarías un PasswordEncoder Bean):
        // admin.setConstrasena(passwordEncoder.encode(admin.getConstrasena()));
        return repositorio.guardar(admin);
    }

    @Transactional
    public void eliminarAdministradorPorId(int id) {
        // Considerar validaciones: ej., no eliminar si tiene cafeterías activas,
        // o manejar la eliminación en cascada según la configuración de la entidad.
        repositorio.eliminar(id);
    }

    @Transactional(readOnly = true)
    public List<administradorDTO> listarAdministradores() {
        return repositorio.obtenerTodos().stream()
                .map(this::convertirAAdministradorDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public administradorDTO buscarAdministradorPorId(int id) {
        Optional<administrador> admin = repositorio.buscarPorId(id);
        return admin.map(this::convertirAAdministradorDTO).orElse(null); // O lanzar NotFoundException
    }

    @Transactional(readOnly = true)
    public List<administradorDTO> buscarAdministradoresPorNombre(String nombre) {
        // La imagen del repositorio indica List<Administrador>, así que el repositorio devuelve lista.
        // El servicio en la imagen indica "buscarNombre(String nombre): administradorDTO",
        // lo cual es extraño si el nombre no es único. Asumo que puede haber múltiples
        // administradores con nombres similares, por lo que devolver una lista de DTOs es más coherente.
        // Si se espera un único resultado, la lógica de negocio o la consulta en el repo deberían ajustarse.
        // Por ahora, mantendré la devolución de lista si el repo devuelve lista.
        // Si la imagen del servicio es estricta y solo debe devolver UN DTO, se necesitaría más lógica.
        // Por ahora, devuelvo una lista de DTOs.
        return repositorio.findByNombre(nombre).stream()
                .map(this::convertirAAdministradorDTO)
                .collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    public administradorDTO buscarAdministradorPorCelular(String celular) {
        // La imagen del servicio dice "buscarCelular(String celular): administradorDTO"
        Optional<administrador> admin = repositorio.findByCelular(celular);
        return admin.map(this::convertirAAdministradorDTO).orElse(null); // O lanzar NotFoundException
    }

    @Transactional(readOnly = true)
    public administradorDTO buscarAdministradorPorCorreo(String correo) {
        Optional<administrador> admin = repositorio.findByCorreo(correo);
        return admin.map(this::convertirAAdministradorDTO).orElse(null); // O lanzar NotFoundException
    }

    // Método para validar credenciales (ejemplo básico)
    @Transactional(readOnly = true)
    public Optional<administradorDTO> validarCredenciales(String correo, String contrasenaPlana) {
        Optional<administrador> adminOpt = repositorio.findByCorreo(correo);
        if (adminOpt.isPresent()) {
            administrador admin = adminOpt.get();
            // Aquí deberías comparar la contraseña plana con la almacenada (posiblemente encriptada).
            // Si usas Spring Security con PasswordEncoder:
            // if (passwordEncoder.matches(contrasenaPlana, admin.getConstrasena())) {
            //     return Optional.of(convertirAAdministradorDTO(admin));
            // }
            // Por ahora, una comparación directa (NO RECOMENDADO PARA PRODUCCIÓN):
            if (admin.getConstrasena().equals(contrasenaPlana)) {
                return Optional.of(convertirAAdministradorDTO(admin));
            }
        }
        return Optional.empty();
    }
}