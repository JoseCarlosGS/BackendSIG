package com.grupoG.ProyectoSIG.controllers;

import com.grupoG.ProyectoSIG.dto.RutaDTO;
import com.grupoG.ProyectoSIG.dto.UbicacionDTO;
import com.grupoG.ProyectoSIG.models.Pedido;
import com.grupoG.ProyectoSIG.models.Ubicacion;
import com.grupoG.ProyectoSIG.services.PedidoService;
import com.grupoG.ProyectoSIG.services.RutaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
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
    public ResponseEntity<Pedido> create(Pedido pedido){
        Pedido pedidoSaved = pedidoService.save(pedido);
        URI location = URI.create("/pedido/" + pedidoSaved.getId());
        return ResponseEntity.created(location).body(pedidoSaved);
    }

    @GetMapping
    public ResponseEntity<List<Pedido>> getAll(){
        return ResponseEntity.ok(pedidoService.findAll());
    }

    @GetMapping("/ruta")
    public ResponseEntity<RutaDTO> calularRuta(@RequestParam Long distribuidorId,
                                               @RequestParam Long pedidoId,
                                               @RequestParam String to){
        return ResponseEntity.ok(pedidoService.getRutaById(pedidoId, distribuidorId,to));
    }

    @PostMapping("/calcular-ruta")
    public RutaDTO obtenerRuta(@RequestBody List<Ubicacion> ubicaciones) throws Exception {
        if (ubicaciones.size() != 2) {
            throw new IllegalArgumentException("Se requieren dos ubicaciones: origen y destino.");
        }
        return rutaService.calcularRuta(ubicaciones.get(0), ubicaciones.get(1));
    }

    @PostMapping("/asignar")
    public ResponseEntity<Pedido> asignarDistribuidor(@RequestBody Pedido pedido) throws Exception {
        Pedido pedidoAsignado = pedidoService.asignarDistribuidorAlPedido(pedido);
        return ResponseEntity.ok(pedidoAsignado);
    }
}
