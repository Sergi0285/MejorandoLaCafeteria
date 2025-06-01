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
import java.util.Arrays;
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
        // El mapa ahora almacena List<Long> para los IDs de producto
        Map<String, List<Integer>> almuerzosAgrupadosPorDia = new HashMap<>();

        List<String> diasHabiles = Arrays.asList("LUN", "MAR", "MIER", "JUE", "VIER");
        for (String dia : diasHabiles) {
            almuerzosAgrupadosPorDia.put(dia, new ArrayList<Integer>()); // Inicializa con listas de Long
        }

        List<producto> todosLosProductos = repositorio.obtenerTodos();
        // 2. Filtrar productos que son "Almuerzo"
        List<producto> almuerzosDisponibles = todosLosProductos.stream()
                .filter(p -> "Almuerzo".equalsIgnoreCase(p.getNivel()))
                .collect(Collectors.toList());

        if (almuerzosDisponibles.isEmpty()) {
            dto.setIngredientesPorDia(almuerzosAgrupadosPorDia); // Mapa con listas de IDs vacías
            return dto;
        }

        // 3. Procesar cada almuerzo disponible para asignar su ID a los días correspondientes
        for (producto almuerzo : almuerzosDisponibles) {
            String descripcionDias = almuerzo.getDescripcion();
            if (descripcionDias != null && !descripcionDias.trim().isEmpty()) {
                String[] diasParaEsteAlmuerzo = descripcionDias.toUpperCase().split(",");
                for (String dia : diasParaEsteAlmuerzo) {
                    String diaTrimmed = dia.trim();
                    if (almuerzosAgrupadosPorDia.containsKey(diaTrimmed)) {
                        // Añade solo el ID del producto a la lista
                        almuerzosAgrupadosPorDia.get(diaTrimmed).add(almuerzo.getIdProducto());
                    }
                }
            }
        }

        dto.setIngredientesPorDia(almuerzosAgrupadosPorDia);
        return dto;
    }
}
