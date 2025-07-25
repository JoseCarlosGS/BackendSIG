package com.grupoG.ProyectoSIG.controllers;

import com.grupoG.ProyectoSIG.dto.DistribuidorRequestDTO;
import com.grupoG.ProyectoSIG.dto.DistribuidorResponseDTO;
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
@RestController
@CrossOrigin("*")
public class DistribuidorController {
    @Autowired
    private DistribuidorService distribuidorService;

    @GetMapping
    public ResponseEntity<List<DistribuidorResponseDTO>>getAll(){
        return ResponseEntity.ok(distribuidorService.findAll());
    }

    @PostMapping
    public ResponseEntity<DistribuidorResponseDTO> guardarCliente(@RequestBody DistribuidorRequestDTO distribuidor) {
        DistribuidorResponseDTO distribuidorSaved = distribuidorService.save(distribuidor);
        URI location = URI.create("/cliente/" + distribuidorSaved.getId());
        return ResponseEntity.created(location).body(distribuidorSaved);
    }
    @GetMapping("/actualizar-localizacion")
    public ResponseEntity<Void> actualizarUbicaciones(){
        try{
            distribuidorService.updateAllLocations();
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @PutMapping("/logout/{id}")
    public ResponseEntity<Void> logout(@PathVariable Long id){
        Distribuidor distribuidor = distribuidorService.findById(id);
        if (distribuidor.getDisponible()) return ResponseEntity.ok().build();
        else {
            distribuidorService.cambiarDisponibilidad(id);
            return ResponseEntity.ok().build();
        }
    }

}
