package com.joyeria.joyeria.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name= "rol_usuario")  
@Data  
@NoArgsConstructor  
@AllArgsConstructor  
@Schema(name = "RolUsuario", description = "Entidad que representa a un rol de usuario en el sistema.")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RolUsuario {
    @Id  // Especifica el identificador primario.
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // El valor del ID se generará automáticamente.
    @Schema(description = "Identificador único del rol de usuario", example = "1")
    private int id;

    // Usamos un Enum para evitar errores de tipeo como "Admin" vs "admin"
    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    @Schema(description = "Nombre del rol de usuario", example = "Administrador")
    private NombreRol nombreRol;

    // Enum interno para definir los roles permitidos
    public enum NombreRol {
        ROLE_ADMIN,
        ROLE_EMPLEADO,
        ROLE_USUARIO
    }
}
