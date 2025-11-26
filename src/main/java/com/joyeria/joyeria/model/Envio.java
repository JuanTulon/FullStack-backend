package com.joyeria.joyeria.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonBackReference;

import io.swagger.v3.oas.annotations.media.Schema;

@Entity
@Table(name = "envio")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "Envio", description = "Entidad que representa un envío de un pedido en el sistema.")
public class Envio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único del envío", example = "1")
    private Integer id_envio;

    @Column(nullable = false)
    @Temporal(TemporalType.DATE)
    @Schema(description = "Fecha de envío del pedido", example = "2023-10-01")
    private Date fecha_envio;

    @Column(nullable = false)
    @Schema(description = "Estado del envío del pedido", example = "Enviado")
    private String estado_envio;

    @OneToOne
    @JoinColumn(name = "id_pedido", referencedColumnName = "idPedido", nullable = false, unique = true)
    @JsonBackReference
    private Pedido pedido;

}