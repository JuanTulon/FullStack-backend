package com.joyeria.joyeria.model;
import java.util.Date;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity  
@Table(name= "pedido")
@Data 
@NoArgsConstructor  
@AllArgsConstructor  
@Schema(name = "Pedido", description = "Entidad que representa un pedido realizado por un cliente en el sistema.")
public class Pedido {

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    @Schema(description = "Identificador único del pedido", example = "1")
    private Integer idPedido;

    @Column(nullable = false)
    @Temporal(TemporalType.DATE)
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "Fecha de realización del pedido", example = "2023-10-25")
    private Date fechaPedido;

    @Column(nullable = false)
    @Schema(description = "Estado del pedido", example = "Pendiente")
    private String estadoPedido;

    @Column(nullable = false)
    @Schema(description = "Total del pedido en pesos chilenos", example = "50000")
    private Integer totalPedido;

    @Column(nullable = false)
    @Schema(description = "Dirección de envío del pedido", example = "Avenida Siempre Viva 1234, Santiago")
    private String direccionEnvio;

    @Column(nullable = false)
    @Schema(description = "Método de pago utilizado para el pedido", example = "Tarjeta de crédito")
    private String metodoPago;

    // CAMBIO CLAVE: Usamos @JsonIgnoreProperties
    // Permite ver el usuario que compró, pero ignora su lista de 'pedidos' para no hacer bucle.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    @JsonIgnoreProperties({"pedidos", "hibernateLazyInitializer", "handler", "password", "roles"}) 
    private Usuario usuario;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("pedido") // Para evitar bucle al mostrar detalles
    private List<DetallePedido> detalles;

    @OneToOne(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("pedido")
    private Envio envio;
}
