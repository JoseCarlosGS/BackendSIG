package com.grupoG.ProyectoSIG.services;

import com.grupoG.ProyectoSIG.exceptions.ResourceNotFoundException;
import com.grupoG.ProyectoSIG.models.Cliente;
import com.grupoG.ProyectoSIG.models.Distribuidor;
import com.grupoG.ProyectoSIG.repositories.ClienteRepository;
import com.grupoG.ProyectoSIG.repositories.DistribuidorRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final ClienteRepository clienteRepository;
    private final DistribuidorRepository distribuidorRepository;
    public CustomUserDetailsService(DistribuidorRepository distribuidorRepository, ClienteRepository clienteRepository
    ) {
        this.distribuidorRepository = distribuidorRepository;
        this.clienteRepository = clienteRepository;
    }
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException{
        Optional<Distribuidor> distribuidor = distribuidorRepository.findByEmail(email);
        if (distribuidor.isPresent()){
            Distribuidor usuario = distribuidor.get();
            return User.builder()
                    .username(usuario.getEmail())
                    .password(usuario.getPassword())
                    .roles("DISTRIBUIDOR")
                    .build();
        }

        Optional<Cliente> cliente = clienteRepository.findByEmail(email);
        if (cliente.isPresent()) {
            Cliente usuario = cliente.get();
            return User.builder()
                    .username(usuario.getEmail())
                    .password(usuario.getPassword())
                    .roles("CLIENTE")
                    .build();
        }
        throw new UsernameNotFoundException("Usuario no encontrado");
    }
    public long getIdUser(String email, String role) {
        if(role.equals("ROLE_DISTRIBUIDOR")){
            Optional<Distribuidor> userDistribuidor = distribuidorRepository.findByEmail(email);
            if (userDistribuidor.isPresent()) return userDistribuidor.get().getId();
        } else if (role.equals("ROLE_CLIENTE")) {
            Optional<Cliente> userCliente = clienteRepository.findByEmail(email);
            if (userCliente.isPresent()) return userCliente.get().getId();
        }
        return -1;
    }
    public void deleteUser(UserDetails user){
        String role = user.getAuthorities().iterator().next().getAuthority();
        if (role.equals("ROLE_CLIENTE")){
            Cliente clienteModel = clienteRepository.findByEmail(user.getUsername())
                    .orElseThrow(()-> new ResourceNotFoundException("Cliente no encontrado"));
            clienteRepository.save(clienteModel);
        }
        else if (role.equals("ROLE_DISTRIBUIDOR")){
            Distribuidor distribuidorModel = distribuidorRepository.findByEmail(user.getUsername())
                    .orElseThrow(()->new ResourceNotFoundException("Distribuidor no encontrado"));
            distribuidorRepository.delete(distribuidorModel);
        }else {
            throw new RuntimeException("Usuario no valido");
        }
    }
}
