package com.grupoG.ProyectoSIG.repositories;

import com.grupoG.ProyectoSIG.models.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
}
