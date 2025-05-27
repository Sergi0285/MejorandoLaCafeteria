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

    // Segun mi investigacion, es buena práctica usar con métodos que modifican datos el @Transactional
    @Transactional 
    public cafeteria guardarCafeteria(cafeteria c) {
        // Aquí podrías añadir validaciones antes de guardar
        // Por ejemplo, verificar si el administrador asociado existe (usando adminrepositorio)
        return repositorio.guardar(c);
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