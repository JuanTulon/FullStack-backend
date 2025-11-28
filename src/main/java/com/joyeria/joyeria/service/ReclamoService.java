package com.joyeria.joyeria.service;

import com.joyeria.joyeria.model.Reclamo;
import com.joyeria.joyeria.repository.ReclamoRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class ReclamoService {

    @Autowired
    private ReclamoRepository reclamoRepository;

    public Reclamo buscarPorProblema(String problema) {
        Reclamo reclamo = reclamoRepository.buscarPorProblema(problema);
        if (reclamo == null) {
            throw new RuntimeException("No se encontraron reclamos con el siguiente problema: " + problema);
        }
        return reclamo;
    }

    public List<Reclamo> findAll() {
        List<Reclamo> reclamos = reclamoRepository.findAll();
        if (reclamos.isEmpty()) {
            throw new RuntimeException("No existen reclamos registrados.");
        }
        return reclamos;
    }

    @Transactional
    public Reclamo save(Reclamo reclamo) {
        return reclamoRepository.save(reclamo);
    }

    @Transactional
    public Reclamo actualizarReclamo(Integer id, Reclamo reclamoDetalles) throws Exception {
        Reclamo reclamo = reclamoRepository.findById(id)
                .orElseThrow(() -> new Exception("Reclamo no encontrado"));

        // Actualizamos solo los campos permitidos
        reclamo.setNombre(reclamoDetalles.getNombre());
        reclamo.setRut(reclamoDetalles.getRut());
        reclamo.setCorreo(reclamoDetalles.getCorreo());
        reclamo.setTelefono(reclamoDetalles.getTelefono());
        reclamo.setProblema(reclamoDetalles.getProblema());
        reclamo.setDuda(reclamoDetalles.getDuda());

        return reclamoRepository.save(reclamo);
    }

    @Transactional
    public void delete(Integer id) {
        if (!reclamoRepository.existsById(id)) {
            throw new RuntimeException("No se puede eliminar. Reclamo no encontrado con ID: " + id);
        }
        reclamoRepository.deleteById(id);
    }
}