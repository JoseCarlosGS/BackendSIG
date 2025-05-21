package com.grupoG.ProyectoSIG.services;

import com.grupoG.ProyectoSIG.dto.RutaDTO;
import com.grupoG.ProyectoSIG.dto.UbicacionDTO;
import com.grupoG.ProyectoSIG.models.Distribuidor;
import com.grupoG.ProyectoSIG.models.Pedido;
import com.grupoG.ProyectoSIG.models.Ubicacion;
import com.grupoG.ProyectoSIG.repositories.PedidoRepository;
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
        UbicacionDTO origen = modelMapper.map(entity.getDireccion_envio(),UbicacionDTO.class);
        Distribuidor distribuidorCercano = distribuidorService.getMasCercano(origen).orElseThrow();
        RutaDTO ruta = rutaService.calcularRuta(origen, distribuidorService.getUbicacionById(distribuidorCercano.getId()));
        return pedidoRepository.save(entity);
    }

    public List<Pedido> findAll(){
        return pedidoRepository.findAll();
    }

    public RutaDTO asignarDistribuidor(Pedido pedido, Distribuidor distribuidor){

        UbicacionDTO origen = modelMapper.map(pedido.getDireccion_envio(),UbicacionDTO.class);
        Distribuidor distribuidorCercano = distribuidorService.getMasCercano(origen).orElseThrow();

        return rutaService.calcularRuta(origen, distribuidorService.getUbicacionById(distribuidorCercano.getId()));
    }


}
