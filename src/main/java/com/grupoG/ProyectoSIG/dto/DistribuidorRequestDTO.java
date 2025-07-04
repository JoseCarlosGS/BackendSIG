package com.grupoG.ProyectoSIG.dto;

import com.grupoG.ProyectoSIG.models.Distribuidor;
import lombok.Data;

@Data
public class DistribuidorRequestDTO {
    private String password;
    private String email;
    private String nombre;
    private String telefono;
    private String tipoVehiculo;
    private Integer capacidadCarga;
    private Boolean disponible;
    private String direccion;
    private Double latitud;
    private Double longitud;
}
