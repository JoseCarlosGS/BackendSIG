package com.grupoG.ProyectoSIG.services;

import com.grupoG.ProyectoSIG.dto.DistribuidorRequestDTO;
import com.grupoG.ProyectoSIG.dto.DistribuidorResponseDTO;
import com.grupoG.ProyectoSIG.models.Distribuidor;
import com.grupoG.ProyectoSIG.models.Ubicacion;
import com.grupoG.ProyectoSIG.repositories.ClienteRepository;
import com.grupoG.ProyectoSIG.repositories.DistribuidorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DistribuidorService {
    @Autowired
    private DistribuidorRepository distribuidorRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UbicacionService ubicacionService;

    @Transactional
    public DistribuidorResponseDTO save(DistribuidorRequestDTO entity) {
        if (clienteRepository.existsByEmail(entity.getEmail())){
            throw new IllegalArgumentException("El correo ya está en uso");
        }

        if (distribuidorRepository.existsByEmail(entity.getEmail())){
            throw new IllegalArgumentException("El correo ya está en uso");
        }
        entity.setPassword(passwordEncoder.encode(entity.getPassword()));
        Distribuidor distribuidor = new Distribuidor();
        Ubicacion ubicacion = new Ubicacion();

        distribuidor.setPassword(passwordEncoder.encode(entity.getPassword()));
        distribuidor.setEmail(entity.getEmail());
        distribuidor.setNombre(entity.getNombre());
        distribuidor.setEmail(entity.getEmail());
        distribuidor.setTelefono(entity.getTelefono());
        distribuidor.setDisponible(entity.getDisponible());
        distribuidor.setCapacidadCarga(entity.getCapacidadCarga());
        distribuidor.setTipoVehiculo(entity.getTipoVehiculo());
        ubicacion.setDireccion(entity.getDireccion());
        ubicacion.setLongitud(entity.getLongitud());
        ubicacion.setLatitud(entity.getLatitud());


        distribuidor.setUbicacionActual(ubicacion);

        return new DistribuidorResponseDTO(distribuidorRepository.save(distribuidor));
    }

    public List<DistribuidorResponseDTO> findAll() {
        return distribuidorRepository.findAll()
                .stream()
                .map(DistribuidorResponseDTO::new)
                .collect(Collectors.toList());
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
