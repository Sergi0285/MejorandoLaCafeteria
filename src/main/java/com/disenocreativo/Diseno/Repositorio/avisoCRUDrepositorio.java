package com.disenocreativo.Diseno.Repositorio;

import com.disenocreativo.Diseno.Entidad.aviso;
import com.disenocreativo.Diseno.Entidad.cafeteria;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface avisoCRUDrepositorio extends CrudRepository<aviso, Integer>{
    List<aviso> findByCafeteria(cafeteria cafeteria);
    List<aviso> findByFechaPublicacion(LocalDate fechaPublicacion);
    
}
