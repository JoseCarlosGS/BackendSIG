package com.grupoG.ProyectoSIG.services;

import com.grupoG.ProyectoSIG.models.Entrega;
import com.grupoG.ProyectoSIG.repositories.EntregaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EntregaService {
    
    @Autowired
    private EntregaRepository entregaRepository;

    public List<Entrega> findAll() {
        return entregaRepository.findAll();
    }

    public Optional<Entrega> findById(Long id) {
        return entregaRepository.findById(id);
    }

    public Entrega save(Entrega entrega) {
        return entregaRepository.save(entrega);
    }

    public Optional<Entrega> actualizarEstado(Long id, String estado) {
        return entregaRepository.findById(id)
                .map(entrega -> {
                    // Aquí podrías agregar validaciones del estado
                    entrega.setObservaciones(entrega.getObservaciones() + "\nEstado actualizado a: " + estado);
                    return entregaRepository.save(entrega);
                });
    }

    public Optional<Entrega> agregarObservaciones(Long id, String observaciones) {
        return entregaRepository.findById(id)
                .map(entrega -> {
                    String observacionesActuales = entrega.getObservaciones() != null ? 
                        entrega.getObservaciones() + "\n" : "";
                    entrega.setObservaciones(observacionesActuales + observaciones);
                    return entregaRepository.save(entrega);
                });
    }

    public List<Entrega> findByPedidoId(Long pedidoId) {
        return entregaRepository.findByPedidoId(pedidoId);
    }

    public List<Entrega> findByDistribuidorId(Long distribuidorId) {
        return entregaRepository.findByPedidoDistribuidorId(distribuidorId);
    }

    public List<Entrega> findEntregasPendientes() {
        return entregaRepository.findByPedidoEstado("PENDIENTE");
    }

    public List<Entrega> findEntregasCompletadas() {
        return entregaRepository.findByPedidoEstado("COMPLETADO");
    }
} 