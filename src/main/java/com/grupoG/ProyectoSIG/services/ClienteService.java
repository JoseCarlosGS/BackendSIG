package com.grupoG.ProyectoSIG.services;

import com.grupoG.ProyectoSIG.dto.ClienteRequestDTO;
import com.grupoG.ProyectoSIG.dto.ClienteResponseDTO;
import com.grupoG.ProyectoSIG.dto.UbicacionDTO;
import com.grupoG.ProyectoSIG.exceptions.ResourceNotFoundException;
import com.grupoG.ProyectoSIG.models.Cliente;
import com.grupoG.ProyectoSIG.models.Ubicacion;
import com.grupoG.ProyectoSIG.repositories.ClienteRepository;
import com.grupoG.ProyectoSIG.repositories.DistribuidorRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.ref.Cleaner;
import java.util.List;
import java.util.Optional;

@Service
public class ClienteService {
    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private DistribuidorRepository distribuidorRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PersistenceContext
    private EntityManager em;

    @Transactional
    public ClienteResponseDTO save(ClienteRequestDTO entity) {

        if (clienteRepository.existsByEmail(entity.getEmail())){
            throw new IllegalArgumentException("El correo ya está en uso");
        }

        if (distribuidorRepository.existsByEmail(entity.getEmail())){
            throw new IllegalArgumentException("El correo ya está en uso");
        }


        Cliente cliente = new Cliente();
        Ubicacion ubicacion = new Ubicacion();
        cliente.setEmail(entity.getEmail());
        cliente.setPassword(passwordEncoder.encode(entity.getPassword()));
        cliente.setNombre(entity.getNombre());
        cliente.setTelefono(entity.getTelefono());
        ubicacion.setDireccion(entity.getDireccion());
        ubicacion.setLongitud(entity.getLongitud());
        ubicacion.setLatitud(entity.getLatitud());
        cliente.setUbicacion(ubicacion);
        return new ClienteResponseDTO(clienteRepository.save(cliente));
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
