package com.disenocreativo.Diseno.Servicio;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import  com.disenocreativo.Diseno.Entidad.role;
import  com.disenocreativo.Diseno.Entidad.administrador;
import  com.disenocreativo.Diseno.Repositorio.clienteRepositorio;

@Service
public class clienteServicio {
  @Autowired
    private clienteRepositorio usuarioRepository;
    
    public administrador findByCorreo(String correo){
        return usuarioRepository.findByCorreo(correo);
    }

    public role obtenerRolPorUsuario(String correo) {
        administrador user = usuarioRepository.findByCorreo(correo);
        return user.getRol();
    }

    public List<administrador> getAllUsuarios(){
        return usuarioRepository.getAllUsuarios();
    }    

    public administrador save(administrador k){
        return usuarioRepository.guardaraUsuario(k);
    }
}