package com.disenocreativo.Diseno.Repositorio;

import com.disenocreativo.Diseno.Entidad.producto;
import com.disenocreativo.Diseno.Entidad.cafeteria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface productoCRUDRepositorio extends JpaRepository<producto, Integer> {

    // Basado en la imagen: + findByNombreProducto(String nombre): List<Producto>
    List<producto> findByNombreProductoContainingIgnoreCase(String nombre); // Usamos ContainingIgnoreCase para búsquedas más flexibles

    // Basado en la imagen: + findByCafeteria(Cafeteria cafeteria): List<Producto>
    List<producto> findByCafeteria(cafeteria cafeteria);
    
    // Adicionalmente, si quieres buscar por ID de cafetería directamente:
    List<producto> findByCafeteriaIdCafeteria(int idCafeteria);

    // Basado en la imagen: + findByTipo(String tipo): List<Producto>
    List<producto> findByTipoIgnoreCase(String tipo);

    // Basado en la imagen: + findByNivel(String nivel): List<Producto>
    List<producto> findByNivelIgnoreCase(String nivel);

    // Basado en la imagen: + findByBowlTrue(): List<Producto>
    // Spring Data JPA infiere el query a partir del nombre del método y el atributo 'esBowl'
    List<producto> findByEsBowlTrue();
}