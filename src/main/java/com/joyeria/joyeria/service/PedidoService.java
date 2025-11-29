package com.joyeria.joyeria.service;

import com.joyeria.joyeria.model.DetallePedido;
import com.joyeria.joyeria.model.Usuario;
import com.joyeria.joyeria.model.Producto;
import com.joyeria.joyeria.dto.PedidoRequest;
import com.joyeria.joyeria.model.Pedido;
import com.joyeria.joyeria.repository.DetallePedidoRepository;
import com.joyeria.joyeria.repository.PedidoRepository;
import com.joyeria.joyeria.repository.ProductoRepository;

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

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private DetallePedidoRepository detallePedidoRepository;

    @Transactional
    public Pedido crearPedidoDesdeVenta(PedidoRequest request, String emailUsuario) {
        // 1. Buscar al usuario
        Usuario usuario = usuarioService.buscarPorEmail(emailUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // 2. Crear la cabecera del pedido
        Pedido pedido = new Pedido();
        pedido.setUsuario(usuario);
        pedido.setFechaPedido(new java.util.Date());
        pedido.setEstadoPedido("Pagado"); // O "Pendiente"
        pedido.setDireccionEnvio(request.getDireccionEnvio());
        pedido.setMetodoPago(request.getMetodoPago());
        pedido.setTotalPedido(0); // Lo calculamos abajo

        // Guardamos primero para tener ID
        pedido = pedidorepository.save(pedido);

        int totalCalculado = 0;
        List<DetallePedido> detalles = new java.util.ArrayList<>();

        // 3. Procesar cada producto
        for (PedidoRequest.ProductoPedidoJson item : request.getProductos()) {
            Producto producto = productoRepository.findById(item.getIdProducto())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado ID: " + item.getIdProducto()));

            // Validar Stock
            if (producto.getStock() < item.getCantidad()) {
                throw new RuntimeException("Stock insuficiente para: " + producto.getNombreProducto());
            }

            // Descontar Stock
            producto.setStock(producto.getStock() - item.getCantidad());
            productoRepository.save(producto);

            // Crear Detalle
            com.joyeria.joyeria.model.DetallePedido detalle = new com.joyeria.joyeria.model.DetallePedido();
            detalle.setPedido(pedido);
            detalle.setProducto(producto);
            detalle.setCantidadProducto(item.getCantidad());
            detalle.setSubtotal(producto.getPrecio() * item.getCantidad()); // Precio real de la BD

            detallePedidoRepository.save(detalle);
            detalles.add(detalle);
            totalCalculado += detalle.getSubtotal();
        }

        // 4. Actualizar total final
        pedido.setTotalPedido(totalCalculado);
        pedido.setDetalles(detalles);
        
        return pedidorepository.save(pedido);
    }

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
