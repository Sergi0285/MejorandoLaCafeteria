package com.disenocreativo.Diseno.Repositorio;

import com.disenocreativo.Diseno.Entidad.administrador;
import com.disenocreativo.Diseno.Entidad.cafeteria;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface cafeteriaCRUDrepositorio extends CrudRepository<cafeteria, Integer> {

    Optional<cafeteria> findByNombreCafeteria(String nombreCafeteria);

    Optional<cafeteria> findByAdministrador(administrador administrador);

    List<cafeteria> findByAdministradorId(Long idAdministrador); 

}