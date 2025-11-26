package com.joyeria.joyeria.security;

import com.joyeria.joyeria.model.Usuario;
import com.joyeria.joyeria.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {// busca usuario en la bd y para que spring lo entienda
    
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException{
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con email: " + email));

        //convertimos roles a "authorities" que es lo que spring security entiende
        var authorities = usuario.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getNombreRol().name()))
                .collect(Collectors.toList());

        //retornamos el objeto User de spring security
        return new User(usuario.getEmail(), usuario.getPassword(), authorities);
    }
}