package com.grupoG.ProyectoSIG.dto;

import com.grupoG.ProyectoSIG.models.Distribuidor;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DistribuidorResponseDTO {
    private long id;
    private String email;
    private String nombre;
    private String telefono;
    private String tipoVehiculo;
    private Integer capacidadCarga;
    private Boolean disponible;
    private String direccion;
    private Double latitud;
    private Double longitud;
    public DistribuidorResponseDTO(Distribuidor distribuidor){
        this.id = distribuidor.getId();
        this.email = distribuidor.getEmail();
        this.nombre = distribuidor.getNombre();
        this.telefono = distribuidor.getTelefono();
        this.tipoVehiculo = distribuidor.getTipoVehiculo();
        this.capacidadCarga = distribuidor.getCapacidadCarga();
        this.disponible = distribuidor.getDisponible();
        this.direccion = distribuidor.getUbicacionActual().getDireccion();
        this.latitud = distribuidor.getUbicacionActual().getLatitud();
        this.longitud = distribuidor.getUbicacionActual().getLongitud();
    }
}
