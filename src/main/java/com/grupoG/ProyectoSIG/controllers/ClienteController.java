package com.grupoG.ProyectoSIG.controllers;

import com.grupoG.ProyectoSIG.models.Cliente;
import com.grupoG.ProyectoSIG.services.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
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

    @PostMapping
    public ResponseEntity<Cliente> guardarCliente(@RequestBody Cliente clienteModel) {
        Cliente clienteSaved = clienteService.save(clienteModel);
        URI location = URI.create("/cliente/" + clienteSaved.getId());
        return ResponseEntity.created(location).body(clienteSaved);
    }
}
