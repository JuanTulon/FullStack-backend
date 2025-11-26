package com.joyeria.joyeria.dto;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String nombre;
    private String role; //para que el front sepa si redirigir al BackOffice
}
