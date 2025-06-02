package com.disenocreativo.Diseno.Entidad;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.CascadeType;

@Entity
public class interaccion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idInteraccion;

    /**
     * Relación uno a uno con Producto.
     * En la tabla 'interaccion' habrá una columna 'id_producto' que referencia a producto(id_producto).
     */

     // Si se borra el producto, la interacción asociada también se borra, y viceversa
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "id_producto", nullable = false, unique = true)
    private producto producto;

    @Column(name = "me_gusta", nullable = false)
    private int meGusta = 0;

    @Column(name = "no_gusta", nullable = false)
    private int noGusta = 0;

    //Constructores
    public interaccion(){
    }
    
    /**
     * Constructor que recibe un producto para inicializar la interacción en cero.
     */
    public interaccion(producto producto) {
        this.producto = producto;
        this.meGusta = 0;
        this.noGusta = 0;
    }

    public int getIdInteraccion() {
        return idInteraccion;
    }

    public void setIdInteraccion(int idInteraccion) {
        this.idInteraccion = idInteraccion;
    }

    public producto getProducto() {
        return producto;
    }

    public void setProducto(producto producto) {
        this.producto = producto;
    }

    public int getMeGusta() {
        return meGusta;
    }

    public void setMeGusta(int meGusta) {
        this.meGusta = meGusta;
    }

    public int getNoGusta() {
        return noGusta;
    }

    public void setNoGusta(int noGusta) {
        this.noGusta = noGusta;
    }
}
