package com.grupoG.ProyectoSIG.repositories;

import com.grupoG.ProyectoSIG.models.Distribuidor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DistribuidorRepository extends JpaRepository<Distribuidor, Long> {
    Optional<Distribuidor> findByEmail(String email);
    List<Distribuidor> findByNombreContainingIgnoreCase(String nombre);
}
