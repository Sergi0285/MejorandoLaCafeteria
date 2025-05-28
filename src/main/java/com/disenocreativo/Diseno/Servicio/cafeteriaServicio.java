package com.disenocreativo.Diseno.Servicio;

import com.disenocreativo.Diseno.Entidad.administrador;
import com.disenocreativo.Diseno.Entidad.cafeteria;
import com.disenocreativo.Diseno.Repositorio.cafeteriaRepositorio;
import com.disenocreativo.Diseno.Repositorio.administradorRepositorio; 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class cafeteriaServicio {

    @Autowired
    private cafeteriaRepositorio repositorio;

    @Autowired
    private administradorRepositorio adminRepositorio;

    @Transactional
    public cafeteria guardarCafeteria(cafeteria c) {
        // Validate and set the full administrator object
        if (c.getAdministrador() != null && c.getAdministrador().getId() != null) {
            Long adminId = c.getAdministrador().getId();
            // Fetch the full administrator entity from the database
            administrador admin = adminRepositorio.buscarPorId(adminId) // 
                .orElseThrow(() -> new RuntimeException("Administrador no encontrado con ID: " + adminId));
            // Set the fully loaded administrator on the cafeteria entity
            c.setAdministrador(admin); // 
        } else if (c.getAdministrador() == null || c.getAdministrador().getId() == null) {
            // This condition might need adjustment based on whether an admin is strictly required.
            // If an admin is always required, the above if block is sufficient if an ID is always passed.
            // If an admin ID is not passed and it's an error, this or similar exception is appropriate.
            throw new RuntimeException("La cafetería debe estar asociada a un administrador válido con un ID.");
        }
        
        // Now c.getAdministrador() is a fully managed/loaded entity from the database
        return repositorio.guardar(c); // 
    }

    @Transactional
    public boolean eliminarCafeteriaPorId(int id) {
        return repositorio.eliminarPorId(id);
    }

    // Esta jugada se ahce para operaciones de solo lectura, mejora el rendimiento
    @Transactional(readOnly = true) 
    public List<cafeteria> listarCafeterias() {
        return repositorio.obtenerTodos();
    }

    @Transactional(readOnly = true)
    public Optional<cafeteria> buscarCafeteriaPorNombre(String nombreCafeteria) {
        return repositorio.findByNombreCafeteria(nombreCafeteria);
    }

    @Transactional(readOnly = true)
    public Optional<cafeteria> buscarCafeteriaPorId(int id) {
        return repositorio.buscarPorId(id);
    }

    @Transactional(readOnly = true)
    public List<cafeteria> buscarCafeteriasPorAdministrador(Long idAdministrador) {
        return repositorio.findByAdministradorId(idAdministrador);
    }

}