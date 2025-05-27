package com.disenocreativo.Diseno.Repositorio;

import com.disenocreativo.Diseno.Entidad.administrador;
import com.disenocreativo.Diseno.Entidad.cafeteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class cafeteriaRepositorio {

    @Autowired
    private cafeteriaCRUDrepositorio cafeteriaCRUD;

    public cafeteria guardar(cafeteria c) {
        return cafeteriaCRUD.save(c);
    }

    public boolean eliminarPorId(int id) {
        if (cafeteriaCRUD.existsById(id)) {
            cafeteriaCRUD.deleteById(id);
            return true;
        }
        return false;
    }

    public void eliminar(cafeteria c) {
        cafeteriaCRUD.delete(c);
    }

    public List<cafeteria> obtenerTodos() {
        return (List<cafeteria>) cafeteriaCRUD.findAll();
    }

    public Optional<cafeteria> buscarPorId(int id) {
        return cafeteriaCRUD.findById(id);
    }

    public Optional<cafeteria> findByNombreCafeteria(String nombre) {
        return cafeteriaCRUD.findByNombreCafeteria(nombre);
    }

    public Optional<cafeteria> findByAdministrador(administrador admin) {
        return cafeteriaCRUD.findByAdministrador(admin);
    }

    public List<cafeteria> findByAdministradorId(Long idAdministrador) {
        return cafeteriaCRUD.findByAdministradorId(idAdministrador);
    }
}