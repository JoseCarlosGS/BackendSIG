package com.grupoG.ProyectoSIG.dto;

import lombok.Data;

import java.sql.Time;
import java.util.Date;

@Data
public class EntregaRequestDTO {
    private Date fecha;
    private Time hora;
    private String observaciones;
    private String direccionEntrega;
    private Double latitud;
    private Double longitud;
    private String nombreCliente;
    private Double costo;
    private String descripcionPedido;
}
