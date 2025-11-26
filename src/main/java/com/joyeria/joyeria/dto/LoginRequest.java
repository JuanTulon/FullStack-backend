package com.joyeria.joyeria.dto;

import lombok.Data;
// DTO(data transfer objects) para la solicitud de inicio de sesión
@Data
public class LoginRequest {
    private String email;
    private String password;
}
