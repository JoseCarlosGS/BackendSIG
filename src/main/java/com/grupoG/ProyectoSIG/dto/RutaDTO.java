package com.grupoG.ProyectoSIG.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RutaDTO {
    private List<UbicacionDTO> coordenadas;
    private double distanciaKm;
    private double duracionMin;
}
