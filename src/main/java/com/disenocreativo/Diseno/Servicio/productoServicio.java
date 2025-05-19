package com.disenocreativo.Diseno.Servicio;

import com.disenocreativo.Diseno.Entidad.producto;
import com.disenocreativo.Diseno.Entidad.cafeteria; // Necesario para buscarCafeteria
import com.disenocreativo.Diseno.Repositorio.productoRepositorio;
import com.disenocreativo.Diseno.Repositorio.cafeteriaRepositorio; // Para obtener la entidad Cafeteria
import com.disenocreativo.Diseno.DTO.diaDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class productoServicio {

    @Autowired
    private productoRepositorio repositorio;

    @Autowired
    private cafeteriaRepositorio cafeteriaRepo; // Para buscar la cafetería por ID

    @Transactional
    public producto guardarProducto(producto p) {
        // Validación: Asegurar que la cafetería asociada exista
        if (p.getCafeteria() != null && p.getCafeteria().getIdCafeteria() != 0) {
            Optional<cafeteria> caf = cafeteriaRepo.buscarPorId(p.getCafeteria().getIdCafeteria());
            if (caf.isEmpty()) {
                // Considera lanzar una excepción personalizada aquí
                throw new RuntimeException("Cafetería no encontrada con ID: " + p.getCafeteria().getIdCafeteria());
            }
            p.setCafeteria(caf.get()); // Asignar la entidad Cafeteria gestionada
        } else if (p.getCafeteria() == null || p.getCafeteria().getIdCafeteria() == 0) {
             throw new RuntimeException("El producto debe estar asociado a una cafetería válida.");
        }
        return repositorio.guardar(p);
    }

    @Transactional
    public void eliminarProductoPorId(int id) { // Cambiado a void como en el diagrama
        if (!repositorio.eliminarPorId(id)) {
            // Considera lanzar una excepción si el producto no se encuentra para eliminar
            // throw new RuntimeException("Producto no encontrado con ID: " + id);
        }
    }

    @Transactional(readOnly = true)
    public List<producto> listarProductos() {
        return repositorio.obtenerTodos();
    }

    @Transactional(readOnly = true)
    public List<producto> buscarProductoPorNombre(String nombre) {
        return repositorio.buscarPorNombre(nombre);
    }

    @Transactional(readOnly = true)
    public Optional<producto> buscarProductoPorId(int id) { // Cambiado a Optional<producto> para consistencia
        return repositorio.buscarPorId(id);
    }

    @Transactional(readOnly = true)
    public List<producto> buscarProductosPorCafeteria(int idCafeteria) {
        Optional<cafeteria> caf = cafeteriaRepo.buscarPorId(idCafeteria);
        if (caf.isPresent()) {
            return repositorio.buscarPorIdCafeteria(idCafeteria);
        }
        return List.of(); // Devuelve lista vacía si la cafetería no existe
    }

    @Transactional(readOnly = true)
    public List<producto> buscarProductosPorTipo(String tipo) {
        return repositorio.buscarPorTipo(tipo);
    }

    @Transactional(readOnly = true)
    public List<producto> buscarProductosPorNivel(String nivel) {
        return repositorio.buscarPorNivel(nivel);
    }

    @Transactional(readOnly = true)
    public List<producto> buscarProductosQueSonBowl() {
        return repositorio.buscarSiEsBowl();
    }

    @Transactional
    public void actualizarDia(List<producto> productosActualizar) {
        if (productosActualizar != null && !productosActualizar.isEmpty()) {
             repositorio.guardarTodos(productosActualizar);
        }
    }
    
    @Transactional(readOnly = true)
    public diaDTO ingredientesPorDia() {
        diaDTO dto = new diaDTO();
        Map<String, List<producto>> agrupacionProductosDelDia = new HashMap<>();

        // Lógica para agrupar productos disponibles para el día sin modificar la entidad Producto.
        // Se basa en categorizar los productos usando sus atributos existentes.

        // Obtenemos todos los productos. Para optimizar, podrías hacer consultas más específicas
        // al repositorio si ya tienes métodos para filtrar por tipo y disponibilidad.
        List<producto> todosLosProductosDisponibles = repositorio.obtenerTodos().stream()
                .filter(producto::isDisponible) // Solo productos disponibles
                .collect(Collectors.toList()); // Convertimos el Stream a List

        if (todosLosProductosDisponibles.isEmpty()) {
            // Si no hay productos disponibles, devolver DTO con mapa vacío.
            // El controlador ya maneja NO_CONTENT para este caso.
            dto.setIngredientesPorDia(agrupacionProductosDelDia);
            return dto;
        }

        // Ejemplo de Categoría 1: "BOWLS"
        // Productos que son bowls.
        List<producto> bowlsDelDia = todosLosProductosDisponibles.stream()
                .filter(producto::isEsBowl) // Usamos el campo 'esBowl'
                .collect(Collectors.toList());

        if (!bowlsDelDia.isEmpty()) {
            agrupacionProductosDelDia.put("BOWLS_DEL_DIA", bowlsDelDia);
        }

        // Ejemplo de Categoría 2: "PLATOS_PRINCIPALES"
        // Productos cuyo tipo sea "PlatoPrincipal" (o como lo tengas definido).
        // Es importante que el campo 'tipo' se use de forma consistente en tus datos.
        final String TIPO_PLATO_PRINCIPAL = "PlatoPrincipal"; // Ajusta este valor a tu data
        List<producto> platosPrincipalesDelDia = todosLosProductosDisponibles.stream()
                .filter(p -> TIPO_PLATO_PRINCIPAL.equalsIgnoreCase(p.getTipo()))
                .collect(Collectors.toList());

        if (!platosPrincipalesDelDia.isEmpty()) {
            agrupacionProductosDelDia.put("PLATOS_PRINCIPALES_HOY", platosPrincipalesDelDia);
        }

        // Ejemplo de Categoría 3: "BEBIDAS"
        final String TIPO_BEBIDA = "Bebida"; // Ajusta este valor
        List<producto> bebidasDelDia = todosLosProductosDisponibles.stream()
                .filter(p -> TIPO_BEBIDA.equalsIgnoreCase(p.getTipo()))
                .collect(Collectors.toList());

        if (!bebidasDelDia.isEmpty()) {
            agrupacionProductosDelDia.put("BEBIDAS_DISPONIBLES", bebidasDelDia);
        }

        // Ejemplo de Categoría 4: "POSTRES"
        final String TIPO_POSTRE = "Postre"; // Ajusta este valor
        List<producto> postresDelDia = todosLosProductosDisponibles.stream()
                .filter(p -> TIPO_POSTRE.equalsIgnoreCase(p.getTipo()))
                .collect(Collectors.toList());
        
        if (!postresDelDia.isEmpty()) {
            agrupacionProductosDelDia.put("POSTRES_RECOMENDADOS", postresDelDia);
        }
        
        // Puedes añadir más categorías según tus necesidades:

        dto.setIngredientesPorDia(agrupacionProductosDelDia);
        return dto;
    }
}
