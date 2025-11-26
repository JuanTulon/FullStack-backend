package com.joyeria.joyeria.model;

import java.util.Date;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "reclamo")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "Reclamo", description = "Entidad que representa un reclamo o contacto de un usuario en el sistema.")
public class Reclamo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único del reclamo", example = "1")
    private Integer idReclamo;

    @Column(nullable = false)
    @Schema(description = "Nombre de la persona que realiza el reclamo", example = "María González")
    private String nombre;

    @Column(nullable = false, length = 12)
    @Schema(description = "RUT de la persona que realiza el reclamo", example = "12.345.678-9")
    private String rut;

    @Column(nullable = false)
    @Schema(description = "Correo electrónico de contacto", example = "maria.gonzalez@email.com")
    private String correo;

    @Column(nullable = false)
    @Schema(description = "Número de teléfono de contacto", example = "+56912345678")
    private String telefono;

    @Column(nullable = false)
    @Schema(description = "Tipo de problema o motivo del contacto", example = "Problemas con el envío")
    private String problema;

    @Column(nullable = false, columnDefinition = "TEXT")
    @Schema(description = "Descripción detallada de la duda o reclamo", example = "Mi pedido no ha llegado en la fecha estimada.")
    private String duda;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @Schema(description = "Fecha y hora en que se creó el reclamo", example = "2023-10-25T10:30:00")
    private Date fechaCreacion;

    // Asigna la fecha automáticamente antes de guardar en la BD
    @PrePersist
    protected void onCreate() {
        this.fechaCreacion = new Date();
    }
}