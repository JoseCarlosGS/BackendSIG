package com.grupoG.ProyectoSIG.services;

import com.grupoG.ProyectoSIG.dto.RutaDTO;
import com.grupoG.ProyectoSIG.models.Distribuidor;
import com.grupoG.ProyectoSIG.models.Pedido;
import com.grupoG.ProyectoSIG.models.Ubicacion;
import com.grupoG.ProyectoSIG.repositories.PedidoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PedidoService {
    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private RutaService rutaService;

    @Autowired
    private ModelMapper modelMapper;


    @Autowired
    private DistribuidorService distribuidorService;

    public <S extends Pedido> S save(S entity) {
        Ubicacion origen = entity.getDireccion_envio();
        Distribuidor distribuidorCercano = distribuidorService.getMasCercano(origen).orElseThrow();
        entity.setDistribuidor(distribuidorCercano);
        return pedidoRepository.save(entity);
    }

    public List<Pedido> findAll(){
        return pedidoRepository.findAll();
    }

    public RutaDTO getRutaById(Long pedidoId, Long distribuidorId, String to) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new EntityNotFoundException("Pedido con ID " + pedidoId + " no encontrado"));

        Ubicacion destino;
        switch (to) {
            case "o":
                destino = pedido.getDireccion_origen();
                break;
            case "d":
                destino = pedido.getDireccion_envio();
                break;
            default:
                throw new IllegalArgumentException("Valor de 'to' inválido: debe ser 'o' (origen) o 'd' (destino)");
        }

        Ubicacion distribuidorUbicacion = distribuidorService.getUbicacionById(distribuidorId);

        if (destino == null || distribuidorUbicacion == null) {
            throw new IllegalStateException("Las ubicaciones no pueden ser nulas");
        }

        return rutaService.calcularRuta(destino, distribuidorUbicacion);
    }
}
