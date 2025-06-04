package com.disenocreativo.Diseno.Entidad;

import jakarta.persistence.*;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
public class aviso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idAviso;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cafeteria_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private cafeteria cafeteria;

    @Lob
    @Column(name = "aviso", nullable = false, columnDefinition = "MEDIUMBLOB")
    private byte[] aviso;

    @Column(name = "fecha_publicacion", nullable = false)
    private LocalDate fechaPublicacion;

    public aviso() {
    }

    public aviso(cafeteria cafeteria, byte[] aviso, LocalDate fechaPublicacion) {
        this.cafeteria = cafeteria;
        this.aviso = aviso;
        this.fechaPublicacion = fechaPublicacion;
    }

    // Getters y Setters

    public Integer getIdAviso() {
        return idAviso;
    }

    public void setIdAviso(Integer idAviso) {
        this.idAviso = idAviso;
    }

    public cafeteria getCafeteria() {
        return cafeteria;
    }

    public void setCafeteria(cafeteria cafeteria) {
        this.cafeteria = cafeteria;
    }

    public byte[] getAviso() {
        return aviso;
    }

    public void setAviso(byte[] aviso) {
        this.aviso = aviso;
    }

    public LocalDate getFechaPublicacion() {
        return fechaPublicacion;
    }

    public void setFechaPublicacion(LocalDate fechaPublicacion) {
        this.fechaPublicacion = fechaPublicacion;
    }
}