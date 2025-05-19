package com.disenocreativo.Diseno.Entidad;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany; 
import jakarta.persistence.CascadeType; 
import java.time.LocalTime;
import java.util.List; 

@Entity
public class cafeteria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idCafeteria;

    private String nombreCafeteria; 
    private LocalTime horarioApertura; 
    private LocalTime horarioCierre;   
    private String ubicacion;         

    @ManyToOne
    @JoinColumn(name = "administrador_id", nullable = false)
    private administrador administrador;

    @Lob
    private byte[] logoCafeteria;

    @OneToMany(mappedBy = "cafeteria", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<producto> productos;

    public cafeteria() {
    }

    public cafeteria(String nombreCafeteria, LocalTime horarioApertura, LocalTime horarioCierre, String ubicacion, administrador administrador, byte[] logoCafeteria) {
        this.nombreCafeteria = nombreCafeteria;
        this.horarioApertura = horarioApertura;
        this.horarioCierre = horarioCierre;
        this.ubicacion = ubicacion;
        this.administrador = administrador;
        this.logoCafeteria = logoCafeteria;
    }

    public int getIdCafeteria() {
        return idCafeteria;
    }

    public void setIdCafeteria(int idCafeteria) {
        this.idCafeteria = idCafeteria;
    }

    public String getNombreCafeteria() {
        return nombreCafeteria;
    }

    public void setNombreCafeteria(String nombreCafeteria) {
        this.nombreCafeteria = nombreCafeteria;
    }

    public LocalTime getHorarioApertura() {
        return horarioApertura;
    }

    public void setHorarioApertura(LocalTime horarioApertura) {
        this.horarioApertura = horarioApertura;
    }

    public LocalTime getHorarioCierre() {
        return horarioCierre;
    }

    public void setHorarioCierre(LocalTime horarioCierre) {
        this.horarioCierre = horarioCierre;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public administrador getAdministrador() {
        return administrador;
    }

    public void setAdministrador(administrador administrador) {
        this.administrador = administrador;
    }

    public byte[] getLogoCafeteria() {
        return logoCafeteria;
    }

    public void setLogoCafeteria(byte[] logoCafeteria) {
        this.logoCafeteria = logoCafeteria;
    }

    public List<producto> getProductos() {
        return productos;
    }

    public void setProductos(List<producto> productos) {
        this.productos = productos;
    }
}