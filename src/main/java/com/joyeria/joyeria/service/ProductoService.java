package com.joyeria.joyeria.service;

import com.joyeria.joyeria.model.Producto;
import com.joyeria.joyeria.repository.ProductoRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    public List<Producto> findBynombre(String nombreProducto) {
        List<Producto> productos = productoRepository.buscarPorNombre(nombreProducto);
        if (productos.isEmpty()) {
            throw new RuntimeException("No se encontraron productos con nombre: " + nombreProducto);
        }
        return productos;
    }

    public List<Producto> findAll() {
        List<Producto> productos = productoRepository.findAll();
        if (productos.isEmpty()) {
            throw new RuntimeException("No existen productos registrados.");
        }
        return productos;
    }

    public Producto findById(Integer id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + id));
    }

    public Producto save(Producto cliente) {
        return productoRepository.save(cliente);
    }

    public void delete(Integer id) {
        if (!productoRepository.existsById(id)) {
            throw new RuntimeException("No se puede eliminar. Producto no encontrado con ID: " + id);
        }
        productoRepository.deleteById(id);
    }
}
