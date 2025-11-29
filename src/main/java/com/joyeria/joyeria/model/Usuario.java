package com.joyeria.joyeria.model;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name= "usuario")  
@Data  
@NoArgsConstructor  
@AllArgsConstructor  
@Schema(name = "Usuario", description = "Entidad que representa a un usuario del sistema.")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Usuario {
    @Id  // Especifica el identificador primario.
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // El valor del ID se generará automáticamente.
    @Schema(description = "Identificador único del usuario", example = "1")
    private Integer id;

    @Column(unique=true, length = 13, nullable=false)  // Define las restricciones para la columna en la tabla.
    @Schema(description = "RUN del usuario (sin dígito verificador)", example = "12345678")
    private String run;

    @Column(nullable=false)  // Define las restricciones para la columna en la tabla.
    @Schema(description = "Dígito verificador del RUN", example = "9")
    private String dv;

    @Column(nullable=false)  // Define las restricciones para la columna en la tabla.
    @Schema(description = "Fecha de nacimiento del usuario", example = "1990-01-01")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date fechaNacimiento;
    
    @Column(nullable=false)  // Define las restricciones para la columna en la tabla.
    @Schema(description = "Nombre del usuario", example = "Juan")
    private String nombre; 

    @Column(nullable=false)  // Define las restricciones para la columna en la tabla.
    @Schema(description = "Primer apellido del usuario", example = "Pérez")
    private String apellido1;
    
    @Column(nullable=false)  // Define las restricciones para la columna en la tabla.
    @Schema(description = "Segundo apellido del usuario", example = "González")
    private String apellido2; 

    @Column(nullable=false)  // Esta columna puede ser nula.
    @Schema(description = "Correo electrónico del usuario", example = "juan.perez@gmail.com")
    private String email;

    @Column(nullable=false)  // Esta columna no puede ser nula.
    @Schema(description = "Teléfono de contacto del usuario", example = "987654321")
    private Integer telefono;
    
    @Column(nullable=false)  // Define las restricciones para la columna en la tabla.
    @Schema(description = "contraseña del usuario", example = "password123")
    private String password;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("usuario-pedidos")
    @Schema(description = "Lista de pedidos asociados al usuario")
    private List<Pedido> pedidos;

    // Relación Muchos a Muchos: CORRECTA y NECESARIA profesionalmente
    @ManyToMany(fetch = FetchType.EAGER) // EAGER es importante para que al hacer login traiga los roles de inmediato
    @JoinTable(
            name = "usuario_roles",
            joinColumns = @JoinColumn(name = "usuario_id"),
            inverseJoinColumns = @JoinColumn(name = "rol_id")
    )
    private Set<RolUsuario> roles = new HashSet<>();
}