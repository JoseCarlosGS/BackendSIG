package com.grupoG.ProyectoSIG.dto;

import com.grupoG.ProyectoSIG.models.Pedido;
import lombok.Data;

import java.util.Date;

@Data
public class PedidoResponseDTO {
    private Long id;
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
    private Long distribuidorId;
    private String distribuidorNombre;
    private String clienteNombre;
    public PedidoResponseDTO(Pedido pedido){
        this.id = pedido.getId();
        this.fecha = pedido.getFecha();
        this.producto = pedido.getProducto();
        this.descripcion = pedido.getDescripcion();
        this.direccionOrigen = pedido.getDireccion_origen().getDireccion();
        this.latitudOrigen = pedido.getDireccion_origen().getLatitud();
        this.longitudOrigen = pedido.getDireccion_origen().getLongitud();
        this.direccionEnvio = pedido.getDireccion_envio().getDireccion();
        this.latitudEnvio = pedido.getDireccion_envio().getLatitud();
        this.longitudEnvio = pedido.getDireccion_envio().getLongitud();
        this.estado = pedido.getEstado().toString();
        this.clienteId = pedido.getCliente().getId();
        this.clienteNombre = pedido.getCliente().getNombre();
        this.distribuidorId = pedido.getDistribuidor().getId();
        this.distribuidorNombre = pedido.getDistribuidor().getNombre();
    }
}
