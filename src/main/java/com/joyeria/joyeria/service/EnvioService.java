package com.joyeria.joyeria.service;

import com.joyeria.joyeria.model.Envio;
import com.joyeria.joyeria.repository.EnvioRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class EnvioService {

    @Autowired
    private EnvioRepository envioRepository;

    public List<Envio> findAll() {
        List<Envio> envios = envioRepository.findAll();
        if (envios.isEmpty()) {
            throw new RuntimeException("No existen envíos registrados.");
        }
        return envios;
    }

    public Envio findById(Integer id) {
        return envioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Envío no encontrado con ID: " + id));
    }

    public Envio save(Envio envio) {
        return envioRepository.save(envio);
    }

    public void delete(Integer id) {
        if (!envioRepository.existsById(id)) {
            throw new RuntimeException("No se puede eliminar. Envío no encontrado con ID: " + id);
        }
        envioRepository.deleteById(id);
    }
}
