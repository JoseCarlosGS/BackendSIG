package com.grupoG.ProyectoSIG.dto;

import com.grupoG.ProyectoSIG.models.*;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
public class PedidoRequestDTO {
    private Date fecha;
    private String producto;
    private String descripcion;
    private String direccionOrigen;
    private Double latitudOrigen;
    private Double longitudOrigen;
    private String direccionEnvio;
    private Double latitudEnvio;
    private Double longitudEnvio;
    private String estado;
    private Long clienteId;
}
