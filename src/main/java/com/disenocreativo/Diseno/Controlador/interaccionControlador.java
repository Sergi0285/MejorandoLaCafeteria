package com.disenocreativo.Diseno.Controlador;

import com.disenocreativo.Diseno.Entidad.interaccion;
import com.disenocreativo.Diseno.Servicio.interaccionServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/interaccion")
public class interaccionControlador {
    
    @Autowired
    private interaccionServicio servicio;

    // Crea o actualiza una interaccion (normalmente no se usa, pues se maneja automáticamenteal pedir me gusta / no me gusta). 
    // Pero lo dejamos disponible en caso de que se quiera insertar manualmente un registro.
    @PostMapping
    public ResponseEntity<interaccion> guardarIn(@RequestBody interaccion i) {
        interaccion guardada = servicio.guardarInter(i);
        return new ResponseEntity<>(guardada, HttpStatus.CREATED);
    }

    // Elimina una interaccion por su idInteraccion.
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarInId(@PathVariable int id) {
        try {
            servicio.eliminarInterId(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Lista todas las interacciones.
    @GetMapping
    public ResponseEntity<List<interaccion>> listarIn() {
        List<interaccion> lista = servicio.listarInter();
        return new ResponseEntity<>(lista, HttpStatus.OK);
    }

    // Busca la interaccion por idInteraccion.
    @GetMapping("/{id}")
    public ResponseEntity<interaccion> buscarInPorId(@PathVariable int id) {
        try {
            interaccion inter = servicio.buscarInterId(id);
            return new ResponseEntity<>(inter, HttpStatus.OK);
        } catch (IllegalArgumentException ex) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Busca (o crea, si no existe) la interaccion asociada a un producto.
    // Esto retorna siempre la fila con contadores actuales.
    @GetMapping("/producto/{idProducto}")
    public ResponseEntity<interaccion> buscarInPorProducto(@PathVariable int idProducto) {
        try {
            interaccion inter = servicio.buscarInterProducto(idProducto);
            return new ResponseEntity<>(inter, HttpStatus.OK);
        } catch (IllegalArgumentException ex) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Lista todas las interacciones ordenadas de mayor a menor según meGusta.
    @GetMapping("/lista-megusta")
    public ResponseEntity<List<interaccion>> listarInPorMeGusta() {
        List<interaccion> lista = servicio.listarInterMeGusta();
        return new ResponseEntity<>(lista, HttpStatus.OK);
    }

    // Lista todas las interacciones ordenadas de mayor a menor según noGusta.
    @GetMapping("/lista-nogusta")
    public ResponseEntity<List<interaccion>> listarInPorNoGusta() {
        List<interaccion> lista = servicio.listarInterNoGusta();
        return new ResponseEntity<>(lista, HttpStatus.OK);
    }

    // Agrega un “Me gusta” a la interaccion asociada a idProducto.
    @PostMapping("/{idProducto}/megusta")
    public ResponseEntity<Void> anadirMeGusta(@PathVariable int idProducto) {
        try {
            servicio.anadirMeGusta(idProducto);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (IllegalArgumentException ex) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Agrega un “No me gusta” a la interaccion asociada a idProducto.
    @PostMapping("/{idProducto}/nogusta")
    public ResponseEntity<Void> anadirNoGusta(@PathVariable int idProducto) {
        try {
            servicio.anadirNoGusta(idProducto);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (IllegalArgumentException ex) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

}
