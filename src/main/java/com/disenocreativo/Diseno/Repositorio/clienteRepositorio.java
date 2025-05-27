package com.disenocreativo.Diseno.Repositorio;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.disenocreativo.Diseno.Entidad.role;
import com.disenocreativo.Diseno.Entidad.administrador;
@Repository
public class clienteRepositorio {
    @Autowired
    private clienteCRUDrepositorio clienteCRUD;

    public administrador findByCorreo(String correo){
        return clienteCRUD.findByCorreo(correo);
    }    

    @SuppressWarnings("null")
    public administrador guardaraUsuario (administrador m){
        return clienteCRUD.save(m);
    }

    public role obtenerRolPorUsuario(String correo) {
        administrador user = clienteCRUD.findByCorreo(correo);
        return user.getRol();
    }

    public List<administrador> getAllUsuarios() {
        return (List<administrador>) clienteCRUD.findAll();
    }
}