package com.disenocreativo.Diseno.Controlador;

import com.disenocreativo.Diseno.Entidad.aviso;
import com.disenocreativo.Diseno.Entidad.cafeteria;
import com.disenocreativo.Diseno.Servicio.avisoServicio;
import com.disenocreativo.Diseno.Repositorio.cafeteriaRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;


@RestController
@RequestMapping("/api/avisos")
public class avisosControlador {

    @Autowired
    private avisoServicio avisoServicio;

    @Autowired
    private cafeteriaRepositorio cafeteriaRepositorio;

    /**
     * 1) Guardar un aviso (con imagen). Recibe cafeteriaId, MultipartFile "aviso" y opcional fecha.
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<aviso> guardarAviso(
            @RequestParam("cafeteriaId") Integer cafeteriaId,
            @RequestParam("aviso") MultipartFile archivoImagen,
            @RequestParam(value = "fecha", required = false) String fechaStr
    ) throws IOException {
        // 1. Validar que exista la cafetería
        cafeteria cafetería = cafeteriaRepositorio.buscarPorId(cafeteriaId)
                .orElseThrow(() -> new RuntimeException("Cafetería no encontrada con id=" + cafeteriaId));

        // 2. Obtener bytes del MultipartFile
        byte[] datosImagen = archivoImagen.getBytes();

        // 3. Fecha de publicación (si no se envia, se toma LocalDate.now())
        LocalDate fechaPub = (fechaStr == null || fechaStr.isBlank())
                ? LocalDate.now()
                : LocalDate.parse(fechaStr); // se espera formato ISO (yyyy-MM-dd)

        // 4. Construir objeto Aviso y guardarlo
        aviso nuevoAviso = new aviso(cafetería, datosImagen, fechaPub);
        aviso guardado = avisoServicio.guardarAvis(nuevoAviso);
        return new ResponseEntity<>(guardado, HttpStatus.CREATED);
    }

    /**
     * 2) Eliminar un aviso por su ID
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarAviso(@PathVariable("id") Integer id) {
        avisoServicio.eliminarAvisId(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * 3) Listar todos los avisos
     */
    @GetMapping
    public ResponseEntity<List<aviso>> listarAvisos() {
        return new ResponseEntity<>(avisoServicio.listarAvis(), HttpStatus.OK);
    }

    /**
     * 4) Buscar avisos por cafetería
     */
    @GetMapping("/cafeteria/{idCafeteria}")
    public ResponseEntity<List<aviso>> buscarAvPorCafeteria(@PathVariable("idCafeteria") Integer idCafeteria) {
        return new ResponseEntity<>(avisoServicio.buscarAvisCafeteria(idCafeteria), HttpStatus.OK);
    }

    /**
     * 5) Buscar aviso por su ID (metadatos, sin servir la imagen)
     */
    @GetMapping("/{id}")
    public ResponseEntity<aviso> buscarAvPorId(@PathVariable("id") Integer id) {
        aviso av = avisoServicio.buscarAvisId(id);
        if (av == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(av, HttpStatus.OK);
    }

    /**
     * 6) Endpoint específico para devolver el contenido BLOB (imagen) de un Aviso:
     *    GET /api/avisos/imagen/{idAviso}  → retorna bytes de la imagen
     */
    @GetMapping("/imagen/{idAviso}")
    public ResponseEntity<byte[]> obtenerAvImagen(@PathVariable("idAviso") Integer idAviso) {
        aviso av = avisoServicio.buscarAvisId(idAviso);
        if (av == null || av.getAviso() == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG); // o IMAGE_PNG según corresponda
        headers.setContentLength(av.getAviso().length);
        return new ResponseEntity<>(av.getAviso(), headers, HttpStatus.OK);
    }
    
}