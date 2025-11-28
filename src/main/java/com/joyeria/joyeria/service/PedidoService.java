package com.joyeria.joyeria.service;

import com.joyeria.joyeria.model.Pedido;
import com.joyeria.joyeria.repository.PedidoRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@Transactional
public class PedidoService {

    @Autowired
    private PedidoRepository pedidorepository;

    public List<Pedido> findAll() {
        List<Pedido> pedidos = pedidorepository.findAll();
        if (pedidos.isEmpty()) {
            throw new RuntimeException("No existen pedidos registrados.");
        }
        return pedidos;
    }

    public Pedido findById(Integer id) {
        return pedidorepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado con ID: " + id));
    }

    @Transactional
    public Pedido save(Pedido pedido) {
        return pedidorepository.save(pedido);
    }

    @Transactional
    public Pedido actualizarPedido(Integer id, Pedido pedidoDetalles) throws Exception {
        Pedido pedido = pedidorepository.findById(id)
                .orElseThrow(() -> new Exception("Pedido no encontrado"));

        // Actualizamos solo los campos permitidos
        pedido.setFechaPedido(pedidoDetalles.getFechaPedido());
        pedido.setTotalPedido(pedidoDetalles.getTotalPedido());
        pedido.setEstadoPedido(pedidoDetalles.getEstadoPedido());
        pedido.setDireccionEnvio(pedidoDetalles.getDireccionEnvio());
        pedido.setMetodoPago(pedidoDetalles.getMetodoPago());
        

        return pedidorepository.save(pedido);
    }

    @Transactional
    public void delete(Integer id) {
        if (!pedidorepository.existsById(id)) {
            throw new RuntimeException("No se puede eliminar. Pedido no encontrado con ID: " + id);
        }
        pedidorepository.deleteById(id);
    }

    @Transactional
    public List<Pedido> findByFechaPedidoBetween(Date fechaInicio, Date fechaFin) {
        List<Pedido> pedidos = pedidorepository.findByFechaPedidoBetween(fechaInicio, fechaFin);
        if (pedidos.isEmpty()) {
            throw new RuntimeException("No existen pedidos en el rango de fechas especificado.");
        }
        return pedidos;
    }

}
