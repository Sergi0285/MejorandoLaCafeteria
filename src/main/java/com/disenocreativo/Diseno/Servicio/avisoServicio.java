package com.disenocreativo.Diseno.Servicio;

import com.disenocreativo.Diseno.Entidad.aviso;
import com.disenocreativo.Diseno.Entidad.cafeteria;
import com.disenocreativo.Diseno.Repositorio.avisoRepositorio;
import com.disenocreativo.Diseno.Repositorio.cafeteriaRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class avisoServicio {

    @Autowired
    private avisoRepositorio avisoRepositorio;

    @Autowired
    private cafeteriaRepositorio cafeteriaRepositorio;

    public aviso guardarAvis(aviso a) {
        return avisoRepositorio.guardarAviso(a);
    }

    public void eliminarAvisId(int id) {
        avisoRepositorio.eliminarAvisoPorId(id);
    }

    public List<aviso> listarAvis() {
        return avisoRepositorio.obtenerTodosAvisos();
    }

    public List<aviso> buscarAvisCafeteria(int idCafeteria) {
        Optional<cafeteria> cafOpt = cafeteriaRepositorio.buscarPorId(idCafeteria);
        return cafOpt.map(avisoRepositorio::findAvisoByCafeteria).orElse(List.of());
    }

    public List<aviso> buscarAvisFecha(LocalDate fecha) {
        return avisoRepositorio.findAvisoByFechaPublicacion(fecha);
    }

    public aviso buscarAvisId(int id) {
        return avisoRepositorio.buscarAvisosPorId(id).orElse(null);
    }
    
    
}
