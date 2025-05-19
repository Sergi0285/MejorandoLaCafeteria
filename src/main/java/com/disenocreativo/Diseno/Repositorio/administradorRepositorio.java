package com.disenocreativo.Diseno.Repositorio;

import com.disenocreativo.Diseno.Entidad.administrador;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class administradorRepositorio {

    @Autowired
    private administradorCRUDrepositorio administradorCRUD;

    public List<administrador> obtenerTodos() {
        return administradorCRUD.findAll();
    }

    public Optional<administrador> buscarPorId(int id) {
        return administradorCRUD.findById(id);
    }

    public administrador guardar(administrador admin) {
        return administradorCRUD.save(admin);
    }

    public void eliminar(int id) {
        administradorCRUD.deleteById(id);
    }

    public List<administrador> findByNombre(String nombre) {
        return administradorCRUD.findByNombreContainingIgnoreCase(nombre);
    }

    public Optional<administrador> findByCelular(String celular) {
        return administradorCRUD.findByCelular(celular);
    }

    public Optional<administrador> findByCorreo(String correo) {
        return administradorCRUD.findByCorreo(correo);
    }
}