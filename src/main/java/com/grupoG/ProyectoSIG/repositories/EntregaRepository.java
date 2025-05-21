package com.grupoG.ProyectoSIG.repositories;

import com.grupoG.ProyectoSIG.models.Entrega;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EntregaRepository extends JpaRepository<Entrega, Long> {
    List<Entrega> findByPedidoId(Long pedidoId);
    List<Entrega> findByPedidoDistribuidorId(Long distribuidorId);
    List<Entrega> findByPedidoEstado(String estado);
}
