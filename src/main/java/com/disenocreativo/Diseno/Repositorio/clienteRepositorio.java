package com.disenocreativo.Diseno.Repositorio;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.disenocreativo.Diseno.Entidad.role;
import com.disenocreativo.Diseno.Entidad.usuario;
@Repository
public class clienteRepositorio {
    @Autowired
    private clienteCRUDrepositorio clienteCRUD;

    public usuario findByCorreo(String correo){
        return clienteCRUD.findByCorreo(correo);
    }    

    @SuppressWarnings("null")
    public usuario guardaraUsuario (usuario m){
        return clienteCRUD.save(m);
    }

    public role obtenerRolPorUsuario(String correo) {
        usuario user = clienteCRUD.findByCorreo(correo);
        return user.getRol();
    }

    public List<usuario> getAllUsuarios() {
        return (List<usuario>) clienteCRUD.findAll();
    }
}