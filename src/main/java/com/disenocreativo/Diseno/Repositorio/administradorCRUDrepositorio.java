package com.disenocreativo.Diseno.Repositorio;

import com.disenocreativo.Diseno.Entidad.administrador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface administradorCRUDrepositorio extends JpaRepository<administrador, Integer> {

    // Método para buscar administradores por nombre.
    // La imagen de administradorRepositorio indica List<Administrador>.
    List<administrador> findByNombreContainingIgnoreCase(String nombre); // Búsqueda flexible por nombre

    // Método para buscar un administrador por su número de celular.
    // La imagen de administradorRepositorio indica Administrador.
    // Spring Data JPA generará la implementación. Usamos Optional para manejar nulidad.
    Optional<administrador> findByCelular(String celular);

    // Método para buscar un administrador por su correo (útil para logins o unicidad)
    Optional<administrador> findByCorreo(String correo);
}