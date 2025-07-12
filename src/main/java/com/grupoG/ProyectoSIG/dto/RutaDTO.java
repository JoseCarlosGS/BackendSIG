package com.grupoG.ProyectoSIG.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RutaDTO {
    private List<UbicacionDTO> coordenadas;
    private List<UbicacionDTO> coordenadasToCliente;
    private double distanciaKm;
    private double duracionMin;

    public void addCoordenadasToEnd(List<UbicacionDTO> nuevasCoord) {
        if (this.coordenadas == null) {
            this.coordenadas = new ArrayList<>();
        }
        if (nuevasCoord != null) {
            this.coordenadas.addAll(nuevasCoord);
        }
    }
}
