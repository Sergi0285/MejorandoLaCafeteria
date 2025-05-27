package com.disenocreativo.Diseno.DTO; // O el paquete que prefieras para DTOs

// Este DTO se usará para las respuestas, omitiendo la contraseña.
public class administradorDTO {

    private Long id;
    private String nombre;
    private String telefono;
    private String correo;
    // No incluimos 'contrasena' por seguridad.
    // Tampoco 'cafeteriasGestionadas' para mantener simple el DTO principal;
    // esa información se puede obtener a través de otros endpoints si es necesario.

    public administradorDTO() {
    }

    public administradorDTO(Long id, String nombre, String telefono, String correo) {
        this.id = id;
        this.nombre = nombre;
        this.telefono = telefono;
        this.correo = correo;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }
}