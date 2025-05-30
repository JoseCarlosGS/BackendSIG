package com.grupoG.ProyectoSIG.services;

import com.grupoG.ProyectoSIG.dto.PedidoDTO;
import com.grupoG.ProyectoSIG.dto.RutaDTO;
import com.grupoG.ProyectoSIG.exceptions.ResourceNotFoundException;
import com.grupoG.ProyectoSIG.models.*;
import com.grupoG.ProyectoSIG.repositories.ClienteRepository;
import com.grupoG.ProyectoSIG.repositories.DistribuidorRepository;
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
    private DistribuidorRepository distribuidorRepository;


    @Autowired
    private DistribuidorService distribuidorService;
    @Autowired
    private ClienteService clienteService;

    public Pedido save(PedidoDTO entity) {
        Cliente cliente = clienteService.findById(entity.getClienteId());

        Pedido pedido = new Pedido();
        pedido.setFecha(entity.getFecha());
        pedido.setProducto(entity.getProducto());
        pedido.setDescripcion(entity.getDescripcion());
        pedido.setDireccion_envio(entity.getDireccion_envio());
        pedido.setDireccion_origen(entity.getDireccion_origen());
        pedido.setCliente(cliente);

        Ubicacion origen = entity.getDireccion_envio();
        Distribuidor distribuidorCercano = distribuidorService.getMasCercano(origen).orElseThrow();
        pedido.setDistribuidor(distribuidorCercano);
        return pedidoRepository.save(pedido);
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

    public Pedido asignarDistribuidorAlPedido(Pedido pedido) throws Exception {

        Ubicacion origen = pedido.getDireccion_origen();

        List<Distribuidor> disponibles = distribuidorRepository.findByDisponibleTrue();

        if (disponibles.isEmpty()) {
            throw new RuntimeException("No hay distribuidores disponibles");
        }

        Distribuidor mejorDistribuidor = null;
        double menorDistancia = Double.MAX_VALUE;

        for (Distribuidor dist : disponibles) {
            RutaDTO ruta = rutaService.calcularRuta(origen, dist.getUbicacionActual());

            if (ruta.getDistanciaKm() < menorDistancia) {
                menorDistancia = ruta.getDistanciaKm();
                mejorDistribuidor = dist;
            }
        }

        if (mejorDistribuidor == null) {
            throw new RuntimeException("No se encontró un distribuidor cercano");
        }

        // Asegúrate de que `mejorDistribuidor` esté dentro del contexto de persistencia
        mejorDistribuidor = distribuidorRepository.findById(mejorDistribuidor.getId())
                .orElseThrow(() -> new RuntimeException("El distribuidor no se encuentra en la base de datos"));

        Cliente cliente = clienteService.findById(pedido.getCliente().getId());

        pedido.setDistribuidor(mejorDistribuidor);
        pedido.setEstado(EstadoPedido.ACEPTADO);

        pedido.setCliente(cliente);

        mejorDistribuidor.setDisponible(false);
        distribuidorRepository.save(mejorDistribuidor);

        return pedidoRepository.save(pedido);

    }


    // pedidoEntregado (cambiar el estado del pedido a entregado y cambiar el distribuidor a disponible)
    public Pedido pedidoEntregado(Long pedidoId) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new EntityNotFoundException("Pedido con ID " + pedidoId + " no encontrado"));

        Distribuidor distribuidor = pedido.getDistribuidor();
        if (distribuidor != null) {
            distribuidor.setDisponible(true);
            distribuidorRepository.save(distribuidor);
        }

        pedido.setEstado(EstadoPedido.ENTREGADO);
        return pedidoRepository.save(pedido);
    }
}
