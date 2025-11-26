package com.joyeria.joyeria.repository;

import com.joyeria.joyeria.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Integer> {

    List<Pedido> findByFechaPedidoBetween(Date fechaInicio, Date fechaFin);

    // Método para buscar pedidos por id de cliente
    List<Pedido> findByUsuarioId(Integer usuarioId);
}
