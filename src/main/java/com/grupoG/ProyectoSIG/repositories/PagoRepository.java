package com.grupoG.ProyectoSIG.repositories;

import com.grupoG.ProyectoSIG.models.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Long> {
}
