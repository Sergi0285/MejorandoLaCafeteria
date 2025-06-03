package com.disenocreativo.Diseno.DTO;

import com.disenocreativo.Diseno.Entidad.interaccion;

import jakarta.persistence.Column;
import jakarta.persistence.Lob;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class productoFavoritoDTO {
    private interaccion i;
    private String nombreProducto;
    private String descripcionProducto;
    private String precioProducto;
    private String nombreCafeteria;
    @Lob
    @Column(columnDefinition="LONGBLOB")
    private byte[] imagenProducto;
}
