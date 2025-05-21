package com.grupoG.ProyectoSIG.services;

import com.grupoG.ProyectoSIG.models.Pedido;
import com.grupoG.ProyectoSIG.repositories.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PedidoService {
    @Autowired
    private PedidoRepository pedidoRepository;

    public <S extends Pedido> S save(S entity) {
        return pedidoRepository.save(entity);
    }

    public List<Pedido> findAll(){
        return pedidoRepository.findAll();
    }
}
