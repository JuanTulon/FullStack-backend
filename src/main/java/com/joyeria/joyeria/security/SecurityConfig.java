package com.joyeria.joyeria.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);        
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, AuthenticationProvider authProvider) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // 1. Rutas Públicas (Todo el mundo puede entrar sin token)
                .requestMatchers("/api/v1/auth/**").permitAll() // Login y Registro
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/v1/productos/**").permitAll() // Ver productos
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/v1/categorias/**").permitAll() // Ver categorías
                .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/v1/reclamos").permitAll() // Crear reclamo (cualquiera puede reclamar)
                .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/v1/usuarios").permitAll() // Registro de usuario normal
                
                // Documentación Swagger (Público para facilitar pruebas, o puedes restringirlo)
                .requestMatchers("/doc/**", "/v3/api-docs/**").permitAll()

                // 2. Rutas Protegidas (Requieren Token y Rol Específico)
                // PRODUCTOS: Solo ADMIN puede crear, editar o borrar
                .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/v1/productos/**").hasRole("ADMIN")
                .requestMatchers(org.springframework.http.HttpMethod.PUT, "/api/v1/productos/**").hasRole("ADMIN")
                .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/api/v1/productos/**").hasRole("ADMIN")

                // CATEGORÍAS: Solo ADMIN
                .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/v1/categorias/**").hasRole("ADMIN")
                .requestMatchers(org.springframework.http.HttpMethod.PUT, "/api/v1/categorias/**").hasRole("ADMIN")
                .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/api/v1/categorias/**").hasRole("ADMIN")

                // RECLAMOS: Solo ADMIN o EMPLEADO pueden VER la lista de reclamos
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/v1/reclamos/**").hasAnyRole("ADMIN", "EMPLEADO")

                // USUARIOS: Solo ADMIN puede ver la lista de usuarios
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/v1/usuarios/**").hasRole("ADMIN")

                // 3. Cualquier otra cosa requiere al menos estar autenticado (Token válido)
                .anyRequest().authenticated()
            )
            .authenticationProvider(authProvider)
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:5173","http://hoseki.s3-website.us-east-2.amazonaws.com"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        configuration.setAllowCredentials(false);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}