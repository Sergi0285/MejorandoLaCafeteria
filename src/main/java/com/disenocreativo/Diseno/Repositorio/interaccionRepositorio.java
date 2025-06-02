package com.disenocreativo.Diseno.Repositorio;

import com.disenocreativo.Diseno.Entidad.interaccion;
import com.disenocreativo.Diseno.Entidad.producto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public class interaccionRepositorio {

    @Autowired
    private interaccionCRUDrepositorio interaccionCRUD;

    //Guarda o actualiza una entidad Interaccion.
    public interaccion guardarInteraccion(interaccion i) {
        return interaccionCRUD.save(i);
    }

    //Elimina una interaccion por su idInteraccion.
    public void eliminarInteraccionPorId(int id) {
        if (interaccionCRUD.existsById(id)) {
            interaccionCRUD.deleteById(id);
        }
    }

    //Obtiene todas las interacciones en la tabla.
    public List<interaccion> listarInteraccion() {
        return interaccionCRUD.findAll();
    }

    //Busca la interaccion asociada a un producto dado.
    public interaccion buscarinteraccionPorProducto(producto p) {
        return interaccionCRUD.findByProducto(p);
    }

    //Busca una interaccion por su idInteraccion (PK).
    public Optional<interaccion> buscarinteraccionPorId(int id) {
        return interaccionCRUD.findById(id);
    }

    //Lista todas las interacciones ordenadas por meGusta descendente.
    public List<interaccion> listarinteraccionPorMeGustaDesc() {
        return interaccionCRUD.findAllByOrderByMeGustaDesc();
    }

    //Lista todas las interacciones ordenadas por noGusta descendente.
    public List<interaccion> listarinteraccionPorNoGustaDesc() {
        return interaccionCRUD.findAllByOrderByNoGustaDesc();
    }

    // Incrementa el contador meGusta para el producto cuyo id = idProducto.
    // Asume que ya existe una fila de interaccion para ese producto.
    public void incrementarMeGusta(int idProducto) {
        interaccionCRUD.operarMeGusta(idProducto);
    }

    //Incrementa el contador noGusta para el producto cuyo id = idProducto.
    public void incrementarNoGusta(int idProducto) {
        interaccionCRUD.operarNoGusta(idProducto);
    }
}
