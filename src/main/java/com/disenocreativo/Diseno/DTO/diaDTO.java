package com.disenocreativo.Diseno.DTO;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class diaDTO {

    private Map<String, List<Integer>> ingredientesPorDia;

    public diaDTO() {
        this.ingredientesPorDia = new HashMap<>();
    }

    public diaDTO(Map<String, List<Integer>> ingredientesPorDia) {
        this.ingredientesPorDia = ingredientesPorDia;
    }

    public Map<String, List<Integer>> getIngredientesPorDia() {
        return ingredientesPorDia;
    }

    public void setIngredientesPorDia(Map<String, List<Integer>> ingredientesPorDia) {
        this.ingredientesPorDia = ingredientesPorDia;
    }

    /*
    // Método útil para añadir productos (ingredientes) para un producto principal específico
    public void addIngredientes(String nombreProductoPrincipal, List<producto> listaProductosIngredientes) {
        if (this.ingredientesPorDia == null) {
            this.ingredientesPorDia = new HashMap<>();
        }
        this.ingredientesPorDia.put(nombreProductoPrincipal, listaProductosIngredientes);
    }
    */
}