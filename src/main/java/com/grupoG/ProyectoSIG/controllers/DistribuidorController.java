package com.grupoG.ProyectoSIG.controllers;

import com.grupoG.ProyectoSIG.models.Distribuidor;
import com.grupoG.ProyectoSIG.services.DistribuidorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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
}
