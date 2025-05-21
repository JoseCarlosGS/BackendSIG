package com.grupoG.ProyectoSIG.repositories;

import com.grupoG.ProyectoSIG.models.Entrega;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EntregaRepository extends JpaRepository<Entrega, Long> {
}
