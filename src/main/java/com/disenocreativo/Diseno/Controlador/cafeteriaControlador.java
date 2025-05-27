package com.disenocreativo.Diseno.Controlador;

import com.disenocreativo.Diseno.Entidad.cafeteria;
import com.disenocreativo.Diseno.Servicio.cafeteriaServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/cafeterias")
public class cafeteriaControlador {

    @Autowired
    private cafeteriaServicio servicio;

    // Guardar una nueva cafetería
    @PostMapping
    public ResponseEntity<cafeteria> guardar(@RequestBody cafeteria c) {
        try {
            cafeteria cafeteriaGuardada = servicio.guardarCafeteria(c);
            return new ResponseEntity<>(cafeteriaGuardada, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Eliminar una cafetería por ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarId(@PathVariable int id) {
        try {
            boolean eliminado = servicio.eliminarCafeteriaPorId(id);
            if (eliminado) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Sacar una lista de todas las cafeterías
    @GetMapping
    public ResponseEntity<List<cafeteria>> listar() {
        try {
            List<cafeteria> cafeterias = servicio.listarCafeterias();
            if (cafeterias.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(cafeterias, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Buscar una cafetería por nombre
    @GetMapping("/buscarPorNombre")
    public ResponseEntity<cafeteria> buscarNombre(@RequestParam String nombreCafeteria) {
        try {
            Optional<cafeteria> cafeteriaEncontrada = servicio.buscarCafeteriaPorNombre(nombreCafeteria);
            return cafeteriaEncontrada.map(cafeteria -> new ResponseEntity<>(cafeteria, HttpStatus.OK))
                    .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Buscar cafetería por ID
    @GetMapping("/{id}")
    public ResponseEntity<cafeteria> buscarId(@PathVariable int id) {
        try {
            Optional<cafeteria> cafeteriaEncontrada = servicio.buscarCafeteriaPorId(id);
            return cafeteriaEncontrada.map(cafeteria -> new ResponseEntity<>(cafeteria, HttpStatus.OK))
                    .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Buscar cafeterías por ID del administrador
    @GetMapping("/porAdministrador/{idAdministrador}")
    public ResponseEntity<List<cafeteria>> buscarCafeteriasPorAdministrador(@PathVariable Long idAdministrador) {
        try {
            List<cafeteria> cafeterias = servicio.buscarCafeteriasPorAdministrador(idAdministrador);
            if (cafeterias.isEmpty()) {
                // Devuelve NO_CONTENT si el administrador existe pero no tiene cafeterías,
                // o una lista vacía con OK.
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(cafeterias, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Actualizar una cafetería, por si acaso
    /*
    @PutMapping("/{id}")
    public ResponseEntity<cafeteria> actualizar(@PathVariable int id, @RequestBody cafeteria cafeteriaActualizada) {
        try {
            Optional<cafeteria> cafeteriaExistente = servicio.buscarCafeteriaPorId(id);
            if (cafeteriaExistente.isPresent()) {
                // Actualiza los campos necesarios
                cafeteriaExistente.get().setNombreCafeteria(cafeteriaActualizada.getNombreCafeteria());
                cafeteriaExistente.get().setHorarioApertura(cafeteriaActualizada.getHorarioApertura());
                cafeteriaExistente.get().setHorarioCierre(cafeteriaActualizada.getHorarioCierre());
                cafeteriaExistente.get().setUbicacion(cafeteriaActualizada.getUbicacion());
                cafeteriaExistente.get().setAdministrador(cafeteriaActualizada.getAdministrador()); // Asegúrate de manejar la entidad administrador correctamente
                cafeteriaExistente.get().setLogoCafeteria(cafeteriaActualizada.getLogoCafeteria());

                cafeteria cActualizada = servicio.guardarCafeteria(cafeteriaExistente.get()); // El método guardar puede servir para actualizar si el ID ya existe
                return new ResponseEntity<>(cActualizada, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    */
}
