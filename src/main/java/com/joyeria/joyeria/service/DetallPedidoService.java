package com.joyeria.joyeria.service;

import com.joyeria.joyeria.model.DetallePedido;
import com.joyeria.joyeria.repository.DetallPedidoRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class DetallPedidoService {
    @Autowired
    private DetallPedidoRepository detallPedidoRepository;

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

    public DetallePedido save(DetallePedido detalle) {
        return detallPedidoRepository.save(detalle);
    }

    public void delete(Integer id) {
        if (!detallPedidoRepository.existsById(id)) {
            throw new RuntimeException("No se puede eliminar. DetallePedido no encontrado con ID: " + id);
        }
        detallPedidoRepository.deleteById(id);
    }
}
