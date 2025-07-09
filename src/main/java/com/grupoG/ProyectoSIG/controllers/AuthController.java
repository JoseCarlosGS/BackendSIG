package com.grupoG.ProyectoSIG.controllers;

import com.grupoG.ProyectoSIG.config.JwtUtil;
import com.grupoG.ProyectoSIG.dto.AuthRequest;
import com.grupoG.ProyectoSIG.models.Cliente;
import com.grupoG.ProyectoSIG.models.Distribuidor;
import com.grupoG.ProyectoSIG.repositories.ClienteRepository;
import com.grupoG.ProyectoSIG.repositories.DistribuidorRepository;
import com.grupoG.ProyectoSIG.services.ClienteService;
import com.grupoG.ProyectoSIG.services.CustomUserDetailsService;
import com.grupoG.ProyectoSIG.services.DistribuidorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final ClienteRepository clienteRepository;
    private final DistribuidorRepository distribuidorRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final ClienteService clienteService;
    private final DistribuidorService distribuidorService;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager, CustomUserDetailsService userDetailsService,
                          JwtUtil jwtUtil, ClienteRepository clienteRepository, DistribuidorRepository distribuidorRepository,
                          ClienteService clienteService, PasswordEncoder passwordEncoder, DistribuidorService distribuidorService
    ) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.clienteRepository = clienteRepository;
        this.jwtUtil = jwtUtil;
        this.distribuidorRepository = distribuidorRepository;
        this.passwordEncoder = passwordEncoder;
        this.clienteService = clienteService;
        this.distribuidorService = distribuidorService;
    }

    private ResponseEntity<Map<String, Object>> authenticateAndGenerateToken(AuthRequest authRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword())
        );

        final UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getEmail());
        final String jwt = jwtUtil.generateToken(userDetails);
        String role = userDetails.getAuthorities().iterator().next().getAuthority();
        long idUser = userDetailsService.getIdUser(authRequest.getEmail(), role);

        // Verificación de estado
        if (role.equals("ROLE_CLIENTE")) {
            Cliente cliente = clienteService.findById(idUser);
        } else if (role.equals("ROLE_DISTRIBUIDOR")) {
            Distribuidor distribuidor = distribuidorService.findById(idUser);
            if (!distribuidor.getDisponible()) distribuidorService.cambiarDisponibilidad(distribuidor.getId());
        }

        Map<String, Object> response = new HashMap<>();
        response.put("token", jwt);
        response.put("userId", idUser);
        response.put("role", role);

        return ResponseEntity.ok(response);
    }
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody AuthRequest authRequest) {
        return authenticateAndGenerateToken(authRequest);
    }
}
