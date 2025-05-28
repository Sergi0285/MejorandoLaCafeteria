package com.disenocreativo.Diseno.Entidad;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="administrador", uniqueConstraints = {@UniqueConstraint(columnNames = {"correo"})})
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class administrador implements UserDetails{

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;

    @Column(nullable = false)
    String nombre;

    @Basic
    String correo;

    String telefono;

    String password;

    @Enumerated(EnumType.STRING) 
    role rol;

    @OneToMany(mappedBy = "administrador", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<cafeteria> cafeteriasGestionadas;

    // Constructor con campos principales (puedes ajustarlo según tus necesidades)
    // Usualmente, el ID es generado automáticamente y las listas de relaciones se manejan por separado.
    public administrador(String nombre, String telefono, String correo, String password) {
        this.nombre = nombre;
        this.telefono = telefono;
        this.correo = correo;
        this.password = password;
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

    public List<cafeteria> getCafeteriasGestionadas() {
        return cafeteriasGestionadas;
    }

    public void setCafeteriasGestionadas(List<cafeteria> cafeteriasGestionadas) {
        this.cafeteriasGestionadas = cafeteriasGestionadas;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (rol == null) {
            System.err.println("ERROR: El rol es null para el administrador con correo: " + this.correo);
            // Considera lanzar una excepción o devolver una lista de autoridades vacía/por defecto si esto es un estado inesperado
            // return Collections.emptyList(); // o lanzar una excepción más informativa
            throw new IllegalStateException("El rol del administrador " + this.correo + " no puede ser null.");
        }
        System.out.println("Asignando autoridad: " + rol.name() + " para el administrador: " + this.correo);
        return List.of(new SimpleGrantedAuthority(rol.name()));
    }

    @Override
    public boolean isAccountNonExpired() {
       return true;
    }
    @Override
    public boolean isAccountNonLocked() {
       return true;
    }
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    @Override
    public boolean isEnabled() {
        return true;
    }
     @Override
    public String getUsername() {
        return correo;
    }

}