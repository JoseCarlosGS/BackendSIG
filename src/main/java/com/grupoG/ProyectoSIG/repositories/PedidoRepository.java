package com.grupoG.ProyectoSIG.repositories;

import com.grupoG.ProyectoSIG.models.EstadoPedido;
import com.grupoG.ProyectoSIG.models.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    List<Pedido> findByDistribuidorId(Long distribuidorId);
    List<Pedido> findByDistribuidorIdAndEstado(Long distribuidorId, EstadoPedido estado);
    List<Pedido> findByClienteId(Long clienteId);
    List<Pedido> findByClienteIdAndEstado(Long clienteId, EstadoPedido estado);
}
