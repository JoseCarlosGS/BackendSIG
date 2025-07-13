package com.grupoG.ProyectoSIG.services;

import com.grupoG.ProyectoSIG.dto.*;
import com.grupoG.ProyectoSIG.exceptions.ResourceNotFoundException;
import com.grupoG.ProyectoSIG.models.*;
import com.grupoG.ProyectoSIG.repositories.ClienteRepository;
import com.grupoG.ProyectoSIG.repositories.DistribuidorRepository;
import com.grupoG.ProyectoSIG.repositories.EntregaRepository;
import com.grupoG.ProyectoSIG.repositories.PedidoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
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

    @Autowired
    private EntregaRepository entregaRepository;

    public PedidoResponseDTO save(PedidoRequestDTO entity) {
        Cliente cliente = clienteService.findById(entity.getClienteId());

        Pedido pedido = new Pedido();
        pedido.setFecha(entity.getFecha());
        pedido.setProducto(entity.getProducto());
        pedido.setDescripcion(entity.getDescripcion());

        Ubicacion origen = new Ubicacion();
        Ubicacion destino = new Ubicacion();
        origen.setDireccion(entity.getDireccionOrigen());
        origen.setLongitud(entity.getLongitudOrigen());
        origen.setLatitud(entity.getLatitudOrigen());

        destino.setDireccion(entity.getDireccionEnvio());
        destino.setLongitud(entity.getLongitudEnvio());
        destino.setLatitud(entity.getLatitudEnvio());

        pedido.setDireccion_envio(destino);
        pedido.setDireccion_origen(origen);
        pedido.setCliente(cliente);


        //Distribuidor distribuidorCercano = distribuidorService.getMasCercano(origen).orElseThrow();
        //pedido.setDistribuidor(distribuidorCercano);
        Pedido pedidosaved = pedidoRepository.save(pedido);
        try {
            return new PedidoResponseDTO(asignarDistribuidorAlPedido(pedidosaved));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<PedidoResponseDTO> findAll(){

        return pedidoRepository.findAll().stream().map(PedidoResponseDTO::new).toList();
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

        RutaDTO ped_ruta = rutaService.calcularRuta(pedido.getDireccion_origen(), pedido.getDireccion_envio());
        RutaDTO response = rutaService.calcularRuta(destino, distribuidorUbicacion);
        response.setCoordenadasToCliente(ped_ruta.getCoordenadas());
        return response;
    }

    public RutaDTO recalcularPedido(Long pedidoId, Long distribuidorId){
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new EntityNotFoundException("Pedido con ID " + pedidoId + " no encontrado"));
        pedido.setEstado(EstadoPedido.EN_CAMINO);
        pedidoRepository.save(pedido);

        return rutaService.calcularRuta(pedido.getDireccion_origen(), pedido.getDireccion_envio());
    }

    public void cambiarEstado(Long pedidoId, EstadoPedido estado){
        Pedido pedido = verificarPedido(pedidoId);
        pedido.setEstado(estado);
        pedidoRepository.save(pedido);
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

    public List<PedidoResponseDTO> obtenerPorDistribuidorId(Long distribuidorId){
        if (distribuidorRepository.findById(distribuidorId).isEmpty()){
            throw new RuntimeException("No se encontró un distribuidor con id: "+distribuidorId);
        }
        return pedidoRepository.findByDistribuidorId(distribuidorId)
                .stream()
                .map(PedidoResponseDTO::new)
                .toList();
    }

    public List<PedidoResponseDTO> obtenerPorDistribuidorYEstadoId(Long distribuidorId, EstadoPedido estado){
        if (distribuidorRepository.findById(distribuidorId).isEmpty()){
            throw new RuntimeException("No se encontró un distribuidor con id: "+distribuidorId);
        }
        return pedidoRepository.findByDistribuidorIdAndEstado(distribuidorId, estado)
                .stream()
                .map(PedidoResponseDTO::new)
                .toList();
    }

    public List<PedidoResponseDTO> obtenerActivosPorDistribuidor(Long distribuidorId){
        if (distribuidorRepository.findById(distribuidorId).isEmpty()){
            throw new RuntimeException("No se encontró un distribuidor con id: "+distribuidorId);
        }
        List<Pedido> response = new ArrayList<>();
        response.addAll(pedidoRepository.findByDistribuidorIdAndEstado(distribuidorId, EstadoPedido.ACEPTADO));
        response.addAll(pedidoRepository.findByDistribuidorIdAndEstado(distribuidorId, EstadoPedido.EN_CAMINO));
        return response.stream().map(PedidoResponseDTO::new).toList();
    }
    public List<PedidoResponseDTO> obtenerActivosPorCliente(Long clienteId){
        if (distribuidorRepository.findById(clienteId).isEmpty()){
            throw new RuntimeException("No se encontró un distribuidor con id: "+clienteId);
        }
        List<Pedido> response = new ArrayList<>();
        response.addAll(pedidoRepository.findByClienteIdAndEstado(clienteId, EstadoPedido.ACEPTADO));
        response.addAll(pedidoRepository.findByClienteIdAndEstado(clienteId, EstadoPedido.EN_CAMINO));
        return response.stream().map(PedidoResponseDTO::new).toList();
    }

    public List<PedidoResponseDTO> obtenerPorClienteId(Long clienteId){
        if (distribuidorRepository.findById(clienteId).isEmpty()){
            throw new RuntimeException("No se encontró un cliente con id: "+clienteId);
        }
        return pedidoRepository.findByClienteId(clienteId)
                .stream()
                .map(PedidoResponseDTO::new)
                .toList();
    }

    public void finalizarPedido(Long pedidoId){
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(()-> new ResourceNotFoundException("Pedido con id: "+pedidoId+" no econtrado"));
        pedido.setEstado(EstadoPedido.ENTREGADO);
        Entrega entrega = new Entrega();
        entrega.setPedido(pedido);
        entrega.setUbicacion(pedido.getDireccion_envio());
        entrega.setFecha(pedido.getFecha());
        entrega.setHora(Time.valueOf(LocalTime.now()));
        entregaRepository.save(entrega);
        pedidoRepository.save(pedido);
    }

    private Pedido verificarPedido(Long pedidoId){
        return pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new EntityNotFoundException("Pedido con ID " + pedidoId + " no encontrado"));
    }
}
