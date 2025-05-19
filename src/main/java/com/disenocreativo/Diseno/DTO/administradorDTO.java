package com.disenocreativo.Diseno.DTO; // O el paquete que prefieras para DTOs

// Este DTO se usará para las respuestas, omitiendo la contraseña.
public class administradorDTO {

    private int idAdministrador;
    private String nombre;
    private String celular;
    private String correo;
    // No incluimos 'contrasena' por seguridad.
    // Tampoco 'cafeteriasGestionadas' para mantener simple el DTO principal;
    // esa información se puede obtener a través de otros endpoints si es necesario.

    public administradorDTO() {
    }

    public administradorDTO(int idAdministrador, String nombre, String celular, String correo) {
        this.idAdministrador = idAdministrador;
        this.nombre = nombre;
        this.celular = celular;
        this.correo = correo;
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
}