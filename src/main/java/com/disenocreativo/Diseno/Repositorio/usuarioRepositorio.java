package com.disenocreativo.Diseno.Repositorio;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import  com.disenocreativo.Diseno.Entidad.usuario;

public interface usuarioRepositorio extends JpaRepository<usuario,Long> {
   Optional<usuario> findByCorreo(String correo); 
   boolean existsByCorreo(String correo);
}