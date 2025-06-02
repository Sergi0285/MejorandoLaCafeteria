package com.disenocreativo.Diseno.Repositorio;

import com.disenocreativo.Diseno.Entidad.interaccion;
import com.disenocreativo.Diseno.Entidad.producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface interaccionCRUDrepositorio extends JpaRepository<interaccion, Integer> {

    //Busca la entidad Interaccion asociada a un producto dado.
    interaccion findByProducto(producto p);

    //Lista todas las interacciones ordenadas de mayor a menor según meGusta.
    List<interaccion> findAllByOrderByMeGustaDesc();

    //Lista todas las interacciones ordenadas de mayor a menor según noGusta.
    List<interaccion> findAllByOrderByNoGustaDesc();

    //Incrementa en 1 el contador meGusta de la fila cuya FK id_producto = :idProducto.
    @Modifying
    @Transactional
    @Query("UPDATE interaccion i SET i.meGusta = i.meGusta + 1 WHERE i.producto.idProducto = :idProducto")
    void operarMeGusta(@Param("idProducto") int idProducto);

    //Incrementa en 1 el contador noGusta de la fila cuya FK id_producto = :idProducto.
    @Modifying
    @Transactional
    @Query("UPDATE interaccion i SET i.noGusta = i.noGusta + 1 WHERE i.producto.idProducto = :idProducto")
    void operarNoGusta(@Param("idProducto") int idProducto);
    
}
