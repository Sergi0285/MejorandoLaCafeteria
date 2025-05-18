package com.disenocreativo.Diseno.Servicio;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import  com.disenocreativo.Diseno.Entidad.role;
import  com.disenocreativo.Diseno.Entidad.usuario;
import  com.disenocreativo.Diseno.Repositorio.clienteRepositorio;

@Service
public class clienteServicio {
  @Autowired
    private clienteRepositorio usuarioRepository;
    
    public usuario findByCorreo(String correo){
        return usuarioRepository.findByCorreo(correo);
    }

    public role obtenerRolPorUsuario(String correo) {
        usuario user = usuarioRepository.findByCorreo(correo);
        return user.getRol();
    }

    public List<usuario> getAllUsuarios(){
        return usuarioRepository.getAllUsuarios();
    }    

    public usuario save(usuario k){
        return usuarioRepository.guardaraUsuario(k);
    }
}