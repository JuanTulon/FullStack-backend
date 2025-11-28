package com.joyeria.joyeria.service;

import com.joyeria.joyeria.model.DetallePedido;
import com.joyeria.joyeria.repository.DetallePedidoRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class DetallePedidoService {
    @Autowired
    private DetallePedidoRepository detallPedidoRepository;

    public DetallePedido findById(Integer id) {
        return detallPedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("DetallePedido no encontrado con ID: " + id));
    }

    public List<DetallePedido> findAll() {
        List<DetallePedido> detalles = detallPedidoRepository.findAll();
        if (detalles.isEmpty()) {
            throw new RuntimeException("No existen detalles de pedido registrados.");
        }
        return detalles;
    }

    @Transactional
    public DetallePedido save(DetallePedido detalle) {
        return detallPedidoRepository.save(detalle);
    }

    @Transactional
    public DetallePedido actualizarDetallePedido(Integer id, DetallePedido detallePedidoDetalles) throws Exception {
        DetallePedido detallePedido = detallPedidoRepository.findById(id)
                .orElseThrow(() -> new Exception("DetallePedido no encontrado"));

        // Actualizamos solo los campos permitidos
        detallePedido.setCantidadProducto(detallePedidoDetalles.getCantidadProducto());
        detallePedido.setSubtotal(detallePedidoDetalles.getSubtotal());        
        detallePedido.setProducto(detallePedidoDetalles.getProducto());
        detallePedido.setPedido(detallePedidoDetalles.getPedido());

        return detallPedidoRepository.save(detallePedido);
    }

    @Transactional
    public void delete(Integer id) {
        if (!detallPedidoRepository.existsById(id)) {
            throw new RuntimeException("No se puede eliminar. DetallePedido no encontrado con ID: " + id);
        }
        detallPedidoRepository.deleteById(id);
    }
}
