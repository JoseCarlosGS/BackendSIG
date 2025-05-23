package com.grupoG.ProyectoSIG.controllers;

import com.grupoG.ProyectoSIG.models.Ubicacion;
import com.grupoG.ProyectoSIG.services.UbicacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ubicacion")
@CrossOrigin("*")
public class UbicacionController {

    @Autowired
    private UbicacionService ubicacionService;


    @GetMapping("/listar")
    public ResponseEntity<List<Ubicacion>> listarUbicaciones(){
        List<Ubicacion> ubicaciones = ubicacionService.findAll();
        return ResponseEntity.ok(ubicaciones);
    }

    @PostMapping()
    public ResponseEntity<?> registrarUbicacion(@RequestBody Ubicacion ubicacion){
        Ubicacion nuevaUbicacion = ubicacionService.save(ubicacion);
        return ResponseEntity.ok(nuevaUbicacion);
    }

}
