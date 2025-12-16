package com.joyeria.joyeria.controller;

import com.joyeria.joyeria.dto.AuthResponse;
import com.joyeria.joyeria.dto.LoginRequest;
import com.joyeria.joyeria.model.Usuario;
import com.joyeria.joyeria.repository.UsuarioRepository;
import com.joyeria.joyeria.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin(origins = "http://hoseki.s3-website.us-east-2.amazonaws.com")
public class AuthController {
    
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        //1. autenticar con spring security(verifica email y pass encriptada)
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        //2. si la autenticacion pasa, buscamos al usuario para generar el token
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail()).orElseThrow();

        /*3. Generar Token
        // Convertimos nuestra entidad Usuario a UserDetails (podrías hacerlo más limpio en un servicio)
        // Pero para simplificar, delegamos en JwtService generando un UserDetails "al vuelo" o ajustando JwtService
        // Lo ideal es usar el CustomUserDetailsService aquí o simplemente pasarle los datos necesarios.
        // Ajuste rápido: usaremos el userDetails del contexto o lo cargamos.
        
        // Generamos el token (JwtService espera un UserDetails)
        // Un truco rápido es crear un UserDetails básico aquí o llamar al CustomUserDetailsService
        // Pero mejor aún, vamos a generar el token
        // NOTA: Para hacerlo simple, asumimos que JwtService puede manejarlo si le pasamos el UserDetails correcto
        
        ... Mejor opción:*/
        String jwtToken = jwtService.generateToken(new org.springframework.security.core.userdetails.User(
                usuario.getEmail(), 
                usuario.getPassword(), 
                // Convertir roles... (mismo código que en CustomUserDetailsService)
                java.util.Collections.emptyList() // Simplificado por ahora, idealmente pasar los roles reales
        ));

        // 4. Obtener el rol principal para el frontend (ej: "admin" o "usuario")
        String rolFrontend = usuario.getRoles().stream()
                .anyMatch(r -> r.getNombreRol().name().equals("ROLE_ADMIN")) ? "admin" :
                usuario.getRoles().stream().anyMatch(r -> r.getNombreRol().name().equals("ROLE_EMPLEADO")) ? "empleado" : "usuario";

        return ResponseEntity.ok(new AuthResponse(jwtToken, usuario.getNombre(), rolFrontend));
    }

}
