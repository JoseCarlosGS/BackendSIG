package com.grupoG.ProyectoSIG.controllers;

import com.grupoG.ProyectoSIG.dto.PedidoDTO;
import com.grupoG.ProyectoSIG.dto.PedidoRequestDTO;
import com.grupoG.ProyectoSIG.dto.PedidoResponseDTO;
import com.grupoG.ProyectoSIG.dto.RutaDTO;
import com.grupoG.ProyectoSIG.models.EstadoPedido;
import com.grupoG.ProyectoSIG.models.Pedido;
import com.grupoG.ProyectoSIG.models.Ubicacion;
import com.grupoG.ProyectoSIG.services.FirebaseDataService;
import com.grupoG.ProyectoSIG.services.PedidoService;
import com.grupoG.ProyectoSIG.services.RutaService;
import org.hibernate.sql.exec.ExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pedido")
@CrossOrigin("*")
public class PedidoController {
    @Autowired
    private RutaService rutaService;
    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private FirebaseDataService firebaseDataService;

    @PostMapping
    public ResponseEntity<PedidoResponseDTO> create(@RequestBody PedidoRequestDTO pedido){
        PedidoResponseDTO pedidoSaved = pedidoService.save(pedido);
        URI location = URI.create("/pedido/" + pedidoSaved.getId());
        return ResponseEntity.created(location).body(pedidoSaved);
    }

    @GetMapping
    public ResponseEntity<List<PedidoResponseDTO>> getAll(){
        return ResponseEntity.ok(pedidoService.findAll());
    }

    @GetMapping("/ruta")
    public ResponseEntity<RutaDTO> calularRuta(@RequestParam Long distribuidorId,
                                               @RequestParam Long pedidoId,
                                               @RequestParam String to){
        return ResponseEntity.ok(pedidoService.getRutaById(pedidoId, distribuidorId,to));
    }

    @GetMapping("/ruta-camino")
    public ResponseEntity<RutaDTO> calcularNuevaRuta(@RequestParam Long distribuidorId,
                                                     @RequestParam Long pedidoId){
        return ResponseEntity.ok(pedidoService.recalcularPedido(pedidoId, distribuidorId));
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

    @GetMapping("/{pedidoId}")
    public ResponseEntity<?> pedidoEntregado(@PathVariable Long pedidoId) {
        Pedido pedido = pedidoService.pedidoEntregado(pedidoId);
        return ResponseEntity.ok(pedido);
    }

    @GetMapping("/distribuidor/{distribuidorId}")
    public ResponseEntity<List<PedidoResponseDTO>> obtenerPorDistribuidor(@PathVariable Long distribuidorId,
                                                                          @RequestParam(required = false) EstadoPedido estado){
        if (estado != null) return ResponseEntity.ok(pedidoService.obtenerPorDistribuidorYEstadoId(distribuidorId, estado));
        else return ResponseEntity.ok(pedidoService.obtenerPorDistribuidorId(distribuidorId));
    }

    @GetMapping("/distribuidor/disponibles/{distribuidorId}")
    public ResponseEntity<List<PedidoResponseDTO>> obtenerDisponiblesPorDistribuidor(@PathVariable Long distribuidorId){

        return ResponseEntity.ok(pedidoService.obtenerActivosPorDistribuidor(distribuidorId));
    }


    @GetMapping("/cliente/{clienteId}")
        public ResponseEntity<List<PedidoResponseDTO>> obtenerPorCliente(@PathVariable Long clienteId){
            return ResponseEntity.ok(pedidoService.obtenerPorClienteId(clienteId));
        }

    @GetMapping("/distribuidores/activos")
    public ResponseEntity<?> getAllDistributorLocations() {
        try {
            Map<String, Map<String, Object>> allLocations = firebaseDataService.getAllActiveDistributorLocations();
            return ResponseEntity.ok(allLocations);
        } catch (InterruptedException | ExecutionException e) {
            return ResponseEntity.status(500).body(Map.of("error", "Error al obtener todos los datos: " + e.getMessage()));
        }
    }
    @PutMapping("/finalizar/{pedidoId}")
    public ResponseEntity<Void> finalizarPedido(@PathVariable Long pedidoId){
        pedidoService.finalizarPedido(pedidoId);
        return ResponseEntity.ok().build();
    }
}
