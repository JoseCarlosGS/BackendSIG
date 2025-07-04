package com.grupoG.ProyectoSIG.dto;

import com.grupoG.ProyectoSIG.models.Cliente;
import lombok.Data;

@Data
public class ClienteResponseDTO {
    private Long id;
    private String nombre;
    private String email;
    private String telefono;
    private String direccion;
    private Double latitud;
    private Double longitud;
    public ClienteResponseDTO(Cliente cliente){
        this.id = cliente.getId();
        this.nombre = cliente.getNombre();
        this.email = cliente.getEmail();
        this.telefono = cliente.getTelefono();
        this.direccion = cliente.getUbicacion().getDireccion();
        this.latitud = cliente.getUbicacion().getLatitud();
        this.longitud = cliente.getUbicacion().getLongitud();
    }
}
