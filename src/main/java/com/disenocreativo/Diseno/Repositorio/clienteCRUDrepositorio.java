package com.disenocreativo.Diseno.Repositorio;

import org.springframework.data.repository.CrudRepository;
import com.disenocreativo.Diseno.Entidad.usuario;

public interface clienteCRUDrepositorio extends CrudRepository <usuario,Long>{

    usuario findByCorreo(String correo);
}