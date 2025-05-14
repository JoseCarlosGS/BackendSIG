package com.grupoG.ProyectoSIG.controllers;

import com.grupoG.ProyectoSIG.models.Cliente;
import com.grupoG.ProyectoSIG.services.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("api/cliente")
@RestController
@CrossOrigin("*")
public class ClienteController {
    @Autowired
    private ClienteService clienteService;

    @GetMapping
    public ResponseEntity<List<Cliente>> listarClientes(){
        return ResponseEntity.ok(clienteService.findAll());
    }
}
