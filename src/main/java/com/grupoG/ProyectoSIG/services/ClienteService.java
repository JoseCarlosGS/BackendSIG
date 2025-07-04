package com.grupoG.ProyectoSIG.services;

import com.grupoG.ProyectoSIG.dto.ClienteResponseDTO;
import com.grupoG.ProyectoSIG.dto.UbicacionDTO;
import com.grupoG.ProyectoSIG.exceptions.ResourceNotFoundException;
import com.grupoG.ProyectoSIG.models.Cliente;
import com.grupoG.ProyectoSIG.models.Ubicacion;
import com.grupoG.ProyectoSIG.repositories.ClienteRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.lang.ref.Cleaner;
import java.util.List;
import java.util.Optional;

@Service
public class ClienteService {
    @Autowired
    private ClienteRepository clienteRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @PersistenceContext
    private EntityManager em;

    public <S extends Cliente> S save(S entity) {
        entity.setPassword(passwordEncoder.encode(entity.getPassword()));
        return clienteRepository.save(entity);
    }

    public List<ClienteResponseDTO> findAll(){
        return clienteRepository.findAll().stream().map(ClienteResponseDTO::new).toList();
    }

    public Cliente findById(long id){
        em.clear();
        Cliente foundedCliente = clienteRepository.findById(id).
                orElseThrow(()-> new ResourceNotFoundException("Cliente con id "+id+" no encontrado"));
        return foundedCliente;
    }

    public UbicacionDTO getUbicacionById(Long clienteId){
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(()-> new ResourceNotFoundException("Cliente con id "+clienteId+" no encontrado"));
        Ubicacion ubicacion = cliente.getUbicacion();
        UbicacionDTO ubicacionDTO = new UbicacionDTO();
        ubicacionDTO.setLat(ubicacion.getLatitud());
        ubicacionDTO.setLon(ubicacion.getLongitud());
        return ubicacionDTO;
    }

    public Optional<Cliente> getClienteById(Long id) {
        return clienteRepository.findById(id);
    }

    public Optional<Cliente> getClienteByEmail( String email){
        Optional<Cliente> existCliente = clienteRepository.findByEmail(email);

        if (existCliente.isEmpty()){
            throw new IllegalArgumentException("El cliente no existe");
        }
        return existCliente;
    }
}
