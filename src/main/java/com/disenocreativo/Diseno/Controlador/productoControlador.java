package com.disenocreativo.Diseno.Controlador;

import com.disenocreativo.Diseno.Entidad.producto;
import com.disenocreativo.Diseno.Servicio.productoServicio;
import com.disenocreativo.Diseno.DTO.diaDTO;
import com.disenocreativo.Diseno.DTO.productoDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/productos") // Endpoint base para productos
public class productoControlador {

    @Autowired
    private productoServicio servicio;

    @PostMapping
    public ResponseEntity<producto> guardar(@RequestBody productoDTO p, Authentication auth) {
        try {
            producto productoGuardado = servicio.guardarProducto(p, auth);
            return new ResponseEntity<>(productoGuardado, HttpStatus.CREATED);
        } catch (RuntimeException e) { // Captura excepciones como la de cafetería no encontrada
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST); // O INTERNAL_SERVER_ERROR según el caso
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarId(@PathVariable int id) { // ResponseEntity<Void> es común para delete
        try {
            // Verificar si existe antes de intentar eliminar para devolver NOT_FOUND apropiadamente
            if (servicio.buscarProductoPorId(id).isEmpty()){
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            servicio.eliminarProductoPorId(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping
    public ResponseEntity<List<producto>> listar() {
        try {
            List<producto> productos = servicio.listarProductos();
            if (productos.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(productos, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<producto> buscarId(@PathVariable int id) {
        try {
            Optional<producto> productoEncontrado = servicio.buscarProductoPorId(id);
            return productoEncontrado.map(prod -> new ResponseEntity<>(prod, HttpStatus.OK))
                                    .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/buscarPorNombre")
    public ResponseEntity<List<producto>> buscarNombre(@RequestParam String nombre) {
        try {
            List<producto> productos = servicio.buscarProductoPorNombre(nombre);
            if (productos.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND); // O NO_CONTENT si se prefiere
            }
            return new ResponseEntity<>(productos, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/porCafeteria/{idCafeteria}")
    public ResponseEntity<List<producto>> buscarPorCafeteria(@PathVariable int idCafeteria) {
        try {
            List<producto> productos = servicio.buscarProductosPorCafeteria(idCafeteria);
            if (productos.isEmpty()) {
                // Podría ser NOT_FOUND si la cafetería no existe o NO_CONTENT si existe pero no tiene productos.
                // La lógica en el servicio actualmente devuelve lista vacía si la cafetería no existe.
                return new ResponseEntity<>(HttpStatus.NO_CONTENT); 
            }
            return new ResponseEntity<>(productos, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/buscarPorTipo")
    public ResponseEntity<List<producto>> buscarPorTipo(@RequestParam String tipo) {
        try {
            List<producto> productos = servicio.buscarProductosPorTipo(tipo);
            if (productos.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(productos, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/buscarPorNivel")
    public ResponseEntity<List<producto>> buscarPorNivel(@RequestParam String nivel) {
        try {
            List<producto> productos = servicio.buscarProductosPorNivel(nivel);
            if (productos.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(productos, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/esBowl")
    public ResponseEntity<List<producto>> esBowl() {
        try {
            List<producto> productos = servicio.buscarProductosQueSonBowl();
            if (productos.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(productos, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/actualizarDia") // Usamos PUT para actualizaciones
    public ResponseEntity<Void> actualizarDia(@RequestBody List<producto> productos) { // ResponseEntity<Void>
        try {
            servicio.actualizarDia(productos);
            return new ResponseEntity<>(HttpStatus.OK); // O NO_CONTENT si no hay cuerpo de respuesta
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/ingredientesPorDia")
    public ResponseEntity<diaDTO> ingredientesPorDia() {
        try {
            diaDTO dto = servicio.ingredientesPorDia();
            if (dto.getIngredientesPorDia() == null || dto.getIngredientesPorDia().isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(dto, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    
    // Endpoint de actualización para un producto específico (más RESTful que el guardar para actualizar)
    @PutMapping("/{id}")
    public ResponseEntity<producto> actualizarProducto(@PathVariable int id, @RequestBody producto productoActualizado, Authentication auth) {
        try {
            Optional<producto> productoExistente = servicio.buscarProductoPorId(id);
            if (productoExistente.isPresent()) {
                producto p = productoExistente.get();
                // Actualizar campos, asegurándose de mantener el ID y la cafetería si no se cambian explícitamente
                p.setNombreProducto(productoActualizado.getNombreProducto());
                p.setDescripcion(productoActualizado.getDescripcion());
                p.setPrecio(productoActualizado.getPrecio());
                p.setTipo(productoActualizado.getTipo());
                p.setNivel(productoActualizado.getNivel());
                p.setEsBowl(productoActualizado.isEsBowl());
                p.setImagenProducto(productoActualizado.getImagenProducto()); // Considerar cómo manejar la imagen
                p.setCantidad(productoActualizado.getCantidad());
                
                // Si la cafetería puede cambiar, se necesitaría lógica similar a la de guardarProducto
                // para validar y asignar la nueva cafetería.
                // p.setCafeteria(productoActualizado.getCafeteria()); 

                producto pActualizado = servicio.guardarProducto(p); // El método guardar actualiza si el ID existe
                return new ResponseEntity<>(pActualizado, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}