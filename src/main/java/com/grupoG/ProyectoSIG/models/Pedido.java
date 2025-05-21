package com.grupoG.ProyectoSIG.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "pedido")
public class Pedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Date fecha;

    @Column(nullable = false)
    private String producto;
    private String descripcion;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_direccion_origen", nullable = false)
    private Ubicacion direccion_origen;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_direccion_envio", nullable = false)
    private Ubicacion direccion_envio;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(20) default 'PENDIENTE'")
    private EstadoPedido estado = EstadoPedido.PENDIENTE;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_cliente", nullable = false)
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "id_distribuidor")
    private Distribuidor distribuidor;

    @ManyToOne
    @JoinColumn(name = "id_pago")
    private Pago pago;
}
