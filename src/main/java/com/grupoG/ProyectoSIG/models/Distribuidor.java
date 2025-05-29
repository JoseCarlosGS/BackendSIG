package com.grupoG.ProyectoSIG.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "distribuidor")
public class Distribuidor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String nombre;
    private String telefono;
    private String tipoVehiculo;
    private Integer capacidadCarga;

    private Boolean disponible;

    @OneToOne(cascade = CascadeType.ALL)
    private Ubicacion ubicacionActual;
}
