package com.grupoG.ProyectoSIG.controllers;

import com.grupoG.ProyectoSIG.dto.RutaDTO;
import com.grupoG.ProyectoSIG.dto.UbicacionDTO;
import com.grupoG.ProyectoSIG.models.Entrega;
import com.grupoG.ProyectoSIG.models.Pedido;
import com.grupoG.ProyectoSIG.models.Ubicacion;
import com.grupoG.ProyectoSIG.services.EntregaService;
import com.grupoG.ProyectoSIG.services.PedidoService;
import com.grupoG.ProyectoSIG.services.RutaService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/entregas")
@CrossOrigin("*")
@RequiredArgsConstructor
public class EntregasController {

    @Autowired
    private EntregaService entregaService;

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private RutaService rutaService;

    @GetMapping
    public ResponseEntity<List<Entrega>> listarEntregas() {
        return ResponseEntity.ok(entregaService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Entrega> obtenerEntrega(@PathVariable Long id) {
        return entregaService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Entrega> registrarEntrega(@RequestBody Entrega entrega) {
        Entrega entregaGuardada = entregaService.save(entrega);
        URI location = URI.create("/api/entregas/" + entregaGuardada.getId());
        return ResponseEntity.created(location).body(entregaGuardada);
    }

    @PutMapping("/{id}/estado")
    public ResponseEntity<Entrega> actualizarEstadoEntrega(
            @PathVariable Long id,
            @RequestParam String estado) {
        return entregaService.actualizarEstado(id, estado)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/observaciones")
    public ResponseEntity<Entrega> agregarObservaciones(
            @PathVariable Long id,
            @RequestBody String observaciones) {
        return entregaService.agregarObservaciones(id, observaciones)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/pedido/{pedidoId}")
    public ResponseEntity<List<Entrega>> obtenerEntregasPorPedido(@PathVariable Long pedidoId) {
        return ResponseEntity.ok(entregaService.findByPedidoId(pedidoId));
    }

    @GetMapping("/distribuidor/{distribuidorId}")
    public ResponseEntity<List<Entrega>> obtenerEntregasPorDistribuidor(@PathVariable Long distribuidorId) {
        return ResponseEntity.ok(entregaService.findByDistribuidorId(distribuidorId));
    }

    @GetMapping("/{id}/ruta")
    public ResponseEntity<RutaDTO> obtenerRutaEntrega(@PathVariable Long id) {
        return entregaService.findById(id)
                .map(entrega -> {
                    try {
                        Ubicacion origen = new Ubicacion(
                            entrega.getPedido().getDireccion_envio().getLatitud(),
                            entrega.getPedido().getDireccion_envio().getLongitud()
                        );
                        Ubicacion destino = new Ubicacion(
                            entrega.getUbicacion().getLatitud(),
                            entrega.getUbicacion().getLongitud()
                        );
                        destino.setLatitud(entrega.getUbicacion().getLatitud());
                        destino.setLongitud(entrega.getUbicacion().getLongitud());
                        return ResponseEntity.ok(rutaService.calcularRuta(origen, destino));
                    } catch (Exception e) {
                        return ResponseEntity.badRequest().<RutaDTO>build();
                    }
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/pendientes")
    public ResponseEntity<List<Entrega>> obtenerEntregasPendientes() {
        return ResponseEntity.ok(entregaService.findEntregasPendientes());
    }

    @GetMapping("/completadas")
    public ResponseEntity<List<Entrega>> obtenerEntregasCompletadas() {
        return ResponseEntity.ok(entregaService.findEntregasCompletadas());
    }
} 