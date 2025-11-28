package com.joyeria.joyeria.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity  // Marca esta clase como una entidad JPA.
@Table(name= "detallePedido")  // Especifica el nombre de la tabla en la base de datos.
@Data  // Genera automáticamente getters, setters, equals, hashCode y toString.
@NoArgsConstructor  // Genera un constructor sin argumentos.
@AllArgsConstructor  // Genera un constructor con un argumento por cada campo en la clase.
@Schema(name = "DetallePedido", description = "Entidad que representa los detalles de un pedido en el sistema.")
public class DetallePedido {
    @Id  // Especifica el identificador primario.
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // El valor del ID se generará automáticamente.
    @Schema(description = "Identificador único del detalle de pedido", example = "1")
    private Integer id;

    @Column(nullable = false)
    @Schema(description = "Cantidad del producto en el detalle del pedido", example = "2")
    private Integer cantidadProducto;

    @Column(nullable = false)
    @Schema(description = "Subtotal del detalle del pedido", example = "50000")
    private Integer subtotal;

    // CAMBIO IMPORTANTE: Usamos @JsonIgnoreProperties
    // Esto dice: "Trae el Pedido completo, pero ignora su campo 'detalles' para no hacer bucle"
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pedido", nullable = false)
    @JsonIgnoreProperties({"detalles", "hibernateLazyInitializer", "handler"}) 
    private Pedido pedido;

    // CAMBIO IMPORTANTE: Usamos @JsonIgnoreProperties
    // Esto dice: "Trae el Producto completo, pero ignora su campo 'detalles' para no hacer bucle"
    @ManyToOne
    @JoinColumn(name = "idProducto", nullable = false, foreignKey = @ForeignKey(name = "fk_detalle_producto"))
    @JsonIgnoreProperties({"detalles", "hibernateLazyInitializer", "handler"})
    private Producto producto;

}