package com.disenocreativo.Diseno.DTO;

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
    private int meGusta;
    private int noGusta;
    private int idProducto;
    private String nombreProducto;
    private String descripcionProducto;
    private String precioProducto;
    private String nombreCafeteria;
    @Lob
    @Column(columnDefinition="LONGBLOB")
    private byte[] imagenProducto;
}
