package com.disenocreativo.Diseno.Repositorio;

import com.disenocreativo.Diseno.Entidad.aviso;
import com.disenocreativo.Diseno.Entidad.cafeteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public class avisoRepositorio {
    @Autowired
    private avisoCRUDrepositorio avisoCRUD;

    public aviso guardarAviso(aviso a) {
        return avisoCRUD.save(a);
    }

    public boolean eliminarAvisoPorId(int id) {
        if (avisoCRUD.existsById(id)) {
            avisoCRUD.deleteById(id);
            return true;
        }
        return false;
    }

    public void eliminarAviso(aviso a) {
        avisoCRUD.delete(a);
    }

    public List<aviso> obtenerTodosAvisos() {
        return (List<aviso>) avisoCRUD.findAll();
    }

    public Optional<aviso> buscarAvisosPorId(int id) {
        return avisoCRUD.findById(id);
    }

    public List<aviso> findAvisoByCafeteria(cafeteria cafeteria) {
        return avisoCRUD.findByCafeteria(cafeteria);
    }

    public List<aviso> findAvisoByFechaPublicacion(LocalDate fecha) {
        return avisoCRUD.findByFechaPublicacion(fecha);
    }

    
    
}
