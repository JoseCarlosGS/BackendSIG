package com.grupoG.ProyectoSIG.controllers;

import com.grupoG.ProyectoSIG.dto.RutaDTO;
import com.grupoG.ProyectoSIG.dto.UbicacionDTO;
import com.grupoG.ProyectoSIG.services.PedidoService;
import com.grupoG.ProyectoSIG.services.RutaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pedido")
@CrossOrigin("*")
public class PedidoController {
    @Autowired
    private RutaService rutaService;
    @Autowired
    private PedidoService pedidoService;

    @PostMapping
    public RutaDTO obtenerRuta(@RequestBody List<UbicacionDTO> ubicaciones) throws Exception {
        if (ubicaciones.size() != 2) {
            throw new IllegalArgumentException("Se requieren dos ubicaciones: origen y destino.");
        }
        return rutaService.calcularRuta(ubicaciones.get(0), ubicaciones.get(1));
    }
}
