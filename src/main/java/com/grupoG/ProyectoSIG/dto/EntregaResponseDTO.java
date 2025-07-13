package com.grupoG.ProyectoSIG.dto;

import com.grupoG.ProyectoSIG.models.Entrega;
import lombok.Data;

import java.sql.Time;
import java.time.LocalTime;
import java.util.Date;

@Data
public class EntregaResponseDTO {
    private Long id;
    private Date fecha;
    private Time hora;
    private String observaciones;
    private String direccionEntrega;
    private Double latitud;
    private Double longitud;
    private String nombreCliente;
    private Double costo;
    private String descripcionPedido;
    public EntregaResponseDTO(Entrega entrega){
        this.id = entrega.getId();
        this.fecha = entrega.getFecha();
        this.hora = entrega.getHora();
        this.observaciones = entrega.getObservaciones();
        this.direccionEntrega = entrega.getUbicacion().getDireccion();
        this.latitud = entrega.getUbicacion().getLatitud();
        this.longitud = entrega.getUbicacion().getLongitud();
        this.nombreCliente = entrega.getPedido().getCliente().getNombre();
        this.descripcionPedido = entrega.getPedido().getDescripcion();
    }

}
