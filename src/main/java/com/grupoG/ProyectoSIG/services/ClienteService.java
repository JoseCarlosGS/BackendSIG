package com.grupoG.ProyectoSIG.services;

import com.grupoG.ProyectoSIG.models.Cliente;
import com.grupoG.ProyectoSIG.repositories.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClienteService {
    @Autowired
    private ClienteRepository clienteRepository;


    public <S extends Cliente> S save(S entity) {
        //entity.setPassword(passwordEncoder.encode(entity.getPassword()));
        return clienteRepository.save(entity);
    }

    public List<Cliente> findAll(){
        return clienteRepository.findAll();
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
