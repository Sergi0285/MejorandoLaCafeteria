package com.disenocreativo.Diseno.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class productoDTO {
    private String nombreProducto;
    private int cantidad;
    private String descripcion;
    private double precio;
    private String tipo;
    private String nivel;
    private boolean esBowl;
    private byte[] imagenProducto;
}
