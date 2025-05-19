package com.disenocreativo.Diseno.Repositorio;

import com.disenocreativo.Diseno.Entidad.producto;
import com.disenocreativo.Diseno.Entidad.cafeteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class productoRepositorio {

    @Autowired
    private productoCRUDRepositorio productoCRUD;

    public producto guardar(producto p) {
        return productoCRUD.save(p);
    }

    public List<producto> guardarTodos(List<producto> productos) {
        return productoCRUD.saveAll(productos);
    }
    
    public boolean eliminarPorId(int id) {
        if (productoCRUD.existsById(id)) {
            productoCRUD.deleteById(id);
            return true;
        }
        return false;
    }

    public void eliminar(producto p) {
        productoCRUD.delete(p);
    }

    public List<producto> obtenerTodos() {
        return productoCRUD.findAll();
    }

    public Optional<producto> buscarPorId(int id) {
        return productoCRUD.findById(id);
    }

    public List<producto> buscarPorNombre(String nombre) {
        return productoCRUD.findByNombreProductoContainingIgnoreCase(nombre);
    }

    public List<producto> buscarPorCafeteria(cafeteria c) {
        return productoCRUD.findByCafeteria(c);
    }
    
    public List<producto> buscarPorIdCafeteria(int idCafeteria) {
        return productoCRUD.findByCafeteriaIdCafeteria(idCafeteria);
    }

    public List<producto> buscarPorTipo(String tipo) {
        return productoCRUD.findByTipoIgnoreCase(tipo);
    }

    public List<producto> buscarPorNivel(String nivel) {
        return productoCRUD.findByNivelIgnoreCase(nivel);
    }

    public List<producto> buscarSiEsBowl() {
        return productoCRUD.findByEsBowlTrue();
    }
}