package com.grupoG.ProyectoSIG.dto;

import lombok.Data;

@Data
public class ClienteRequestDTO {
    private String nombre;
    private String password;
    private String email;
    private String telefono;
    private String direccion;
    private Double latitud;
    private Double longitud;
}
