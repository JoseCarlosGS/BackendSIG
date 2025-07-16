package com.grupoG.ProyectoSIG.dto;

import com.grupoG.ProyectoSIG.models.Ubicacion;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PedidoDTO {
    Date fecha;
    String producto;
    String descripcion;
    UbicacionDTO direccion_origen;
    Ubicacion direccion_envio;
    Long clienteId;
}
