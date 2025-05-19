package com.disenocreativo.Diseno.Entidad;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany; 
import jakarta.persistence.CascadeType; 
import java.util.List;

@Entity
public class administrador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idAdministrador;

    private String nombre;
    private String celular;
    private String correo;
    private String contrasena;

    // Relación uno-a-muchos con Cafeteria.
    // Un administrador puede gestionar varias cafeterías.
    // "mappedBy = "administrador"" indica que el campo 'administrador' en la entidad Cafeteria
    // es el dueño de esta relación.
    // CascadeType.ALL significa que las operaciones (persistir, eliminar, etc.) en Administrador
    // se propagarán a las Cafeterias asociadas.
    // orphanRemoval = true asegura que si una Cafeteria es removida de la lista 'cafeteriasGestionadas',
    // será eliminada de la base de datos.
    @OneToMany(mappedBy = "administrador", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<cafeteria> cafeteriasGestionadas;

    // Constructor por defecto (requerido por JPA)
    public administrador() {
    }

    // Constructor con campos principales (puedes ajustarlo según tus necesidades)
    // Usualmente, el ID es generado automáticamente y las listas de relaciones se manejan por separado.
    public administrador(String nombre, String celular, String correo, String constrasena) {
        this.nombre = nombre;
        this.celular = celular;
        this.correo = correo;
        this.contrasena = constrasena;
    }

    // Getters y Setters
    public int getIdAdministrador() {
        return idAdministrador;
    }

    public void setIdAdministrador(int idAdministrador) {
        this.idAdministrador = idAdministrador;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getConstrasena() {
        return contrasena;
    }

    public void setConstrasena(String constrasena) {
        this.contrasena = constrasena;
    }

    public List<cafeteria> getCafeteriasGestionadas() {
        return cafeteriasGestionadas;
    }

    public void setCafeteriasGestionadas(List<cafeteria> cafeteriasGestionadas) {
        this.cafeteriasGestionadas = cafeteriasGestionadas;
    }
}