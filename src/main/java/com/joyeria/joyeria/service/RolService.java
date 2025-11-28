package com.joyeria.joyeria.service;

import com.joyeria.joyeria.model.RolUsuario;
import com.joyeria.joyeria.model.RolUsuario.NombreRol;
import com.joyeria.joyeria.repository.RolUsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class RolService {

    @Autowired
    private RolUsuarioRepository rolUsuarioRepository;

    public Optional<RolUsuario> findByNombre(NombreRol nombreRol) {
        Optional<RolUsuario> roles = rolUsuarioRepository.findByNombreRol(nombreRol);
        if (roles.isEmpty()) {
            throw new RuntimeException("No se encontraron roles con nombre: " + nombreRol);
        }
        return roles;
    }

    public List<RolUsuario> findAll() {
        List<RolUsuario> roles = rolUsuarioRepository.findAll();
        if (roles.isEmpty()) {
            throw new RuntimeException("No existen roles registrados.");
        }
        return roles;
    }
    public RolUsuario findById(Integer id) {
        return rolUsuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado con ID: " + id));
    }

    @Transactional
    public RolUsuario save(RolUsuario rolUsuario) {
        return rolUsuarioRepository.save(rolUsuario);
    }

    @Transactional
    public void delete(Integer id) {
        if (!rolUsuarioRepository.existsById(id)) {
            throw new RuntimeException("No se puede eliminar. Rol no encontrado con ID: " + id);
        }
        rolUsuarioRepository.deleteById(id);
    }
    
}
