package com.grupoG.ProyectoSIG.controllers;

import com.grupoG.ProyectoSIG.models.Cliente;
import com.grupoG.ProyectoSIG.models.Distribuidor;
import com.grupoG.ProyectoSIG.services.DistribuidorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RequestMapping("api/distribuidor")
@Controller
@CrossOrigin("*")
public class DistribuidorController {
    @Autowired
    private DistribuidorService distribuidorService;

    @GetMapping
    public ResponseEntity<List<Distribuidor>>getAll(){
        return ResponseEntity.ok(distribuidorService.findAll());
    }

    @PostMapping
    public ResponseEntity<Distribuidor> guardarCliente(@RequestBody Distribuidor distribuidor) {
        Distribuidor distribuidorSaved = distribuidorService.save(distribuidor);
        URI location = URI.create("/cliente/" + distribuidorSaved.getId());
        return ResponseEntity.created(location).body(distribuidorSaved);
    }
}
