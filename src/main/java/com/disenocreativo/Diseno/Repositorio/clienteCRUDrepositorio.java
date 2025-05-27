package com.disenocreativo.Diseno.Repositorio;

import org.springframework.data.repository.CrudRepository;
import com.disenocreativo.Diseno.Entidad.administrador;

public interface clienteCRUDrepositorio extends CrudRepository <administrador,Long>{

    administrador findByCorreo(String correo);
}