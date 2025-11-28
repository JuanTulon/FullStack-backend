package com.joyeria.joyeria.service;

import com.joyeria.joyeria.model.Categoria;
import com.joyeria.joyeria.repository.CategoriaRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class CategoriaService {

    @Autowired
    private CategoriaRepository categoriaRepository;

    public List<Categoria> findByNombre(String nombreCategoria) {
        List<Categoria> categorias = categoriaRepository.buscarPorNombre(nombreCategoria);
        if (categorias.isEmpty()) {
            throw new RuntimeException("No se encontraron categorías con nombre: " + nombreCategoria);
        }
        return categorias;
    }

    public List<Categoria> findAll() {
        List<Categoria> categorias = categoriaRepository.findAll();
        if (categorias.isEmpty()) {
            throw new RuntimeException("No existen categorías registradas.");
        }
        return categorias;
    }

    public Categoria findById(Integer id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada con ID: " + id));
    }

    @Transactional
    public Categoria save(Categoria categoria) {
        return categoriaRepository.save(categoria);
    }

    @Transactional
    public Categoria actualizarCategoria(Integer id, Categoria categoriaDetalles) throws Exception {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new Exception("Categoría no encontrada"));

        // Actualizamos solo los campos permitidos
        categoria.setNombreCategoria(categoriaDetalles.getNombreCategoria());
        

        return categoriaRepository.save(categoria);
    }

    @Transactional
    public void delete(Integer id) {
        if (!categoriaRepository.existsById(id)) {
            throw new RuntimeException("No se puede eliminar. Categoría no encontrada con ID: " + id);
        }
        categoriaRepository.deleteById(id);
    }
}
