package com.disenocreativo.Diseno.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class registroPeticion {
   
    String nombre;
    String correo;
    String telefono;
    String password;
    
}
