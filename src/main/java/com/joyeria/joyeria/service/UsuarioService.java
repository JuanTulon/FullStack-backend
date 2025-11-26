package com.joyeria.joyeria.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import com.joyeria.joyeria.model.RolUsuario;
import com.joyeria.joyeria.model.Usuario;
import com.joyeria.joyeria.repository.RolUsuarioRepository;
import com.joyeria.joyeria.repository.UsuarioRepository;
import com.joyeria.joyeria.util.RutUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolUsuarioRepository rolUsuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // --- LISTAR Y BUSCAR ---

    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }

    public Optional<Usuario> buscarPorId(Integer id) {
        return usuarioRepository.findById(id);
    }

    public Optional<Usuario> buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    public List<Usuario> buscarPorRut(String rutCompleto) {
        if (!RutUtils.validarRut(rutCompleto)) {
            throw new IllegalArgumentException("El RUT ingresado no es válido: " + rutCompleto);
        }
        
        // Separamos el RUT para buscarlo en la BD (donde run y dv están separados)
        String[] partes = rutCompleto.split("-");
        String run = partes[0];
        String dv = partes[1].toUpperCase();
        
        return usuarioRepository.buscarPorRut(run, dv);
    }

    // CREACIÓN (REGISTRO)
    @Transactional
    public Usuario registrarUsuario(Usuario usuario) throws Exception {
        // 1. Validaciones de negocio
        if (usuarioRepository.findByEmail(usuario.getEmail()).isPresent()) {
            throw new Exception("El correo electrónico ya está registrado.");
        }

        // 2. Validación de RUT usando tu RutUtils
        String rutCompleto = usuario.getRun() + "-" + usuario.getDv();
        if (!RutUtils.validarRut(rutCompleto)) {
            throw new Exception("El RUT ingresado no es válido.");
        }
        if (usuarioRepository.existsByRun(usuario.getRun())) {
            throw new Exception("El RUT ya está registrado en el sistema.");
        }

        // 3. Asignar Rol por defecto (CLIENTE / USUARIO)
        // Buscamos el rol en la BD para asignárselo
        RolUsuario rolUsuario = rolUsuarioRepository.findByNombreRol(RolUsuario.NombreRol.ROLE_USUARIO)
                .orElseThrow(() -> new Exception("Error de sistema: No existe el rol de usuario base."));
        
        usuario.setRoles(Collections.singleton(rolUsuario));

        // 4. Encriptar contraseña 
        String passEncriptada = passwordEncoder.encode(usuario.getPassword());
        usuario.setPassword(passEncriptada);
        
        return usuarioRepository.save(usuario);
    }

    // --- ACTUALIZACIÓN ---
    @Transactional
    public Usuario actualizarUsuario(Integer id, Usuario usuarioDetalles) throws Exception {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new Exception("Usuario no encontrado"));

        // Actualizamos solo los campos permitidos
        usuario.setNombre(usuarioDetalles.getNombre());
        usuario.setApellido1(usuarioDetalles.getApellido1());
        usuario.setApellido2(usuarioDetalles.getApellido2());
        usuario.setTelefono(usuarioDetalles.getTelefono());
        
        // El email solo se cambia si es distinto y no existe otro igual
        if (!usuario.getEmail().equals(usuarioDetalles.getEmail()) && 
            usuarioRepository.findByEmail(usuarioDetalles.getEmail()).isPresent()) {
            throw new Exception("El nuevo correo ya está en uso.");
        }
        usuario.setEmail(usuarioDetalles.getEmail());

        return usuarioRepository.save(usuario);
    }

    // --- ELIMINAR ---

    @Transactional
    public void eliminarUsuario(Integer id) throws Exception {
        if (!usuarioRepository.existsById(id)) {
            throw new Exception("Usuario no encontrado con ID: " + id);
        }
        usuarioRepository.deleteById(id);
    }
}