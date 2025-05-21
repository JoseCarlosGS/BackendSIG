package com.grupoG.ProyectoSIG.services;


import com.grupoG.ProyectoSIG.models.Ubicacion;
import com.grupoG.ProyectoSIG.repositories.UbicacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UbicacionService {
    @Autowired
    private UbicacionRepository ubicacionRepository;

    public <S extends Ubicacion> S save(S entity) {
        return ubicacionRepository.save(entity);
    }

    public List<Ubicacion> findAll(){
        return ubicacionRepository.findAll();
    }

    public Optional<Ubicacion> findById(Long id){
        return ubicacionRepository.findById(id);
    }
}
