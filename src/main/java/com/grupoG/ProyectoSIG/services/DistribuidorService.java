package com.grupoG.ProyectoSIG.services;

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

    @Autowired
    private UbicacionService ubicacionService;


    public <S extends Distribuidor> S save(S entity) {
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

    public Optional<Distribuidor> getMasCercano(Ubicacion ubicacion){
        Distribuidor cercano = distribuidorRepository.findAll().getFirst();
        return distribuidorRepository.findById(cercano.getId());
    }

    public Ubicacion getUbicacionById(Long id){
        Distribuidor distribuidor = findById(id).orElseThrow();
        Ubicacion ubicacion = new Ubicacion();
        ubicacion.setLatitud(-17.800576745238438);
        ubicacion.setLongitud(-63.18439910641412);
        Ubicacion saved = ubicacionService.save(ubicacion);
        return saved;
    }
}
