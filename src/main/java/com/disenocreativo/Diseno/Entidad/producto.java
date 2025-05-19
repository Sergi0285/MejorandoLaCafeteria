package com.disenocreativo.Diseno.Entidad;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Column;

@Entity
public class producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idProducto;

    private String nombreProducto;
    private String descripcion;
    private double precio;
    private String tipo;
    private String nivel;
    
    @Column(name = "es_bowl")
    private boolean esBowl;

    @Lob
    @Column(columnDefinition="LONGBLOB")
    private byte[] imagenProducto;

    private boolean disponible = true;

    @ManyToOne
    @JoinColumn(name = "cafeteria_id", nullable = false)
    private cafeteria cafeteria;

    // Constructores
    public producto() {
    }

    public producto(String nombreProducto, String descripcion, double precio, String tipo, String nivel, boolean esBowl, byte[] imagenProducto, boolean disponible, cafeteria cafeteria) {
        this.nombreProducto = nombreProducto;
        this.descripcion = descripcion;
        this.precio = precio;
        this.tipo = tipo;
        this.nivel = nivel;
        this.esBowl = esBowl;
        this.imagenProducto = imagenProducto;
        this.disponible = disponible;
        this.cafeteria = cafeteria;
    }

    // Getters y Setters
    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getNivel() {
        return nivel;
    }

    public void setNivel(String nivel) {
        this.nivel = nivel;
    }

    public boolean isEsBowl() {
        return esBowl;
    }

    public void setEsBowl(boolean esBowl) {
        this.esBowl = esBowl;
    }

    public byte[] getImagenProducto() {
        return imagenProducto;
    }

    public void setImagenProducto(byte[] imagenProducto) {
        this.imagenProducto = imagenProducto;
    }

    public boolean isDisponible() {
        return disponible;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }

    public cafeteria getCafeteria() {
        return cafeteria;
    }

    public void setCafeteria(cafeteria cafeteria) {
        this.cafeteria = cafeteria;
    }
}