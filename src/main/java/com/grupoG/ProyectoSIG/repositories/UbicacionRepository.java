package com.grupoG.ProyectoSIG.repositories;

import com.grupoG.ProyectoSIG.models.Ubicacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UbicacionRepository extends JpaRepository<Ubicacion, Long> {
}
