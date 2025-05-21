package com.grupoG.ProyectoSIG.services;

import com.grupoG.ProyectoSIG.dto.UbicacionDTO;
import com.grupoG.ProyectoSIG.models.Distribuidor;
import com.grupoG.ProyectoSIG.models.Ubicacion;
import com.grupoG.ProyectoSIG.repositories.DistribuidorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DistribuidorService {
    @Autowired
    private DistribuidorRepository distribuidorRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    public <S extends com.grupoG.ProyectoSIG.models.Distribuidor> S save(S entity) {
        entity.setPassword(passwordEncoder.encode(entity.getPassword()));
        return distribuidorRepository.save(entity);
    }

    public List<Distribuidor> findAll(){
        return distribuidorRepository.findAll();
    }

    public Optional<Distribuidor> findById(long id){
        return distribuidorRepository.findById(id);
    }

    public Optional<Distribuidor> getClienteById(Long id) {
        return distribuidorRepository.findById(id);
    }

    public Optional<Distribuidor> getMasCercano(UbicacionDTO ubicacion){
        Distribuidor cercano = distribuidorRepository.findAll().getFirst();
        return distribuidorRepository.findById(cercano.getId());
    }

    public UbicacionDTO getUbicacionById(Long id){
        Distribuidor distribuidor = findById(id).orElseThrow();
        UbicacionDTO ubicacion = new UbicacionDTO(-15.878787,-64.2323);
        return ubicacion;
    }
}
