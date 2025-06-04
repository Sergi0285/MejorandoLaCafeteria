package com.disenocreativo.Diseno.Servicio;

import com.disenocreativo.Diseno.DTO.productoFavoritoDTO;
import com.disenocreativo.Diseno.Entidad.interaccion;
import com.disenocreativo.Diseno.Entidad.producto;
import com.disenocreativo.Diseno.Repositorio.interaccionRepositorio;
import com.disenocreativo.Diseno.Repositorio.productoRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class interaccionServicio {

    @Autowired
    private interaccionRepositorio repositorio;

    @Autowired
    private productoRepositorio productoRepo;

    //Guarda o actualiza una interaccion en BD.
    public interaccion guardarInter(interaccion i) {
        return repositorio.guardarInteraccion(i);
    }

    //Elimina la interaccion con PK = id.
    public void eliminarInterId(int id) {
        repositorio.eliminarInteraccionPorId(id);
    }

    //Retorna todas las interacciones.
    public List<interaccion> listarInter() {
        return repositorio.listarInteraccion();
    }

    /**
     * Busca (o crea si no existe) la interaccion asociada a un producto.
     * @param idProducto id del producto
     * @return la entidad interaccion correspondiente
     */
    @Transactional(readOnly = true)
    public interaccion buscarInterProducto(int idProducto) {
        //Obtenemos el producto
        Optional<producto> prodOpt = productoRepo.buscarPorId(idProducto);
        if (prodOpt.isEmpty()) {
            throw new IllegalArgumentException("No existe producto con id " + idProducto);
        }
        producto prod = prodOpt.get();

        //Intentamos encontrar la interaccion existente
        interaccion inter = repositorio.buscarinteraccionPorProducto(prod);
        if (inter != null) {
            return inter;
        }

        //Si no existe, creamos una nueva fila con contadores en cero
        interaccion nueva = new interaccion(prod);
        return repositorio.guardarInteraccion(nueva);
    }

    //Busca una interaccion por su PK (idInteraccion).
    @Transactional(readOnly = true)
    public interaccion buscarInterId(int id) {
        Optional<interaccion> opt = repositorio.buscarinteraccionPorId(id);
        if (opt.isEmpty()) {
            throw new IllegalArgumentException("No existe interaccion con id " + id);
        }
        return opt.get();
    }

    //Lista todas las interacciones ordenadas por mayor número de meGusta.
    @Transactional(readOnly = true)
    public List<interaccion> listarInterMeGusta() {
        return repositorio.listarinteraccionPorMeGustaDesc();
    }

    //Lista todas las interacciones ordenadas por mayor número de noGusta.
    @Transactional(readOnly = true)
    public List<interaccion> listarInterNoGusta() {
        return repositorio.listarinteraccionPorNoGustaDesc();
    }

    // Agrega un “Me gusta” al contador de la interaccion asociada a idProducto.
    // Si no existía una fila, la crea primero (con contador en cero) y luego incrementa.
    @Transactional
    public void anadirMeGusta(int idProducto) {
        // 1. Asegurarse de que exista la interaccion
        interaccion inter = buscarInterProducto(idProducto);
        // 2. Incrementar contador
        repositorio.incrementarMeGusta(idProducto);
    }

    // Agrega un “No me gusta” al contador de la interaccion asociada a idProducto.
    // Si no existía una fila, la crea primero (con contador en cero) y luego incrementa.
    @Transactional
    public void anadirNoGusta(int idProducto) {
        interaccion inter = buscarInterProducto(idProducto);
        repositorio.incrementarNoGusta(idProducto);
    }

    @Transactional(readOnly = true)
    public List<productoFavoritoDTO> favoritos(){
        List<interaccion> interacciones = listarInterMeGusta();
        List<productoFavoritoDTO> favoritos = new ArrayList<>();
        for (interaccion i : interacciones) {
            Optional<producto> prodOpt = productoRepo.buscarPorId(i.getProducto().getIdProducto());
            if (prodOpt.isPresent()) {
                producto prod = prodOpt.get();
                productoFavoritoDTO dto = new productoFavoritoDTO();
                dto.setMeGusta(i.getMeGusta());
                dto.setNoGusta(i.getNoGusta());
                dto.setIdProducto(prod.getIdProducto());
                dto.setNombreProducto(prod.getNombreProducto());
                dto.setDescripcionProducto(prod.getDescripcion());
                dto.setPrecioProducto(String.valueOf(prod.getPrecio()));
                dto.setNombreCafeteria(prod.getCafeteria().getNombreCafeteria());
                dto.setImagenProducto(prod.getImagenProducto());
                favoritos.add(dto);
            }
        }
        return favoritos;
    }
    
}
