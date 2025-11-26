package com.joyeria.joyeria.model;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import io.swagger.v3.oas.annotations.media.Schema;

@Entity  // Marca esta clase como una entidad JPA.
@Table(name= "producto")  // Especifica el nombre de la tabla en la base de datos.
@Data  // Genera automáticamente getters, setters, equals, hashCode y toString.
@NoArgsConstructor  // Genera un constructor sin argumentos.
@AllArgsConstructor  // Genera un constructor con un argumento por cada campo en la clase.
@Schema(name = "Producto", description = "Entidad que representa un producto en el sistema.")
public class Producto {

    @Id  // Especifica el identificador primario.
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // El valor del ID se generará automáticamente.
    @Schema(description = "Identificador único del producto", example = "1")
    private Integer idProducto;

    @Column(nullable=false)  // Esta columna no puede ser nula.
    @Schema(description = "Nombre del producto", example = "Perfume Floral")
    private String nombreProducto;

    @Column(nullable=false)  // Esta columna no puede ser nula.
    @Schema(description = "Descripción del producto", example = "Un perfume floral fresco y encantador.")
    private String descripcionProducto;

    @Column(nullable=true)  // Esta columna puede ser nula.
    @Schema(description = "precio del producto", example = "50000")
    private Integer precio;

    @Column(nullable=false)  // Esta columna no puede ser nula.
    @Schema(description = "Cantidad de stock disponible del producto", example = "100")
    private Integer stock;

    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("producto-detalles")
    private List<DetallePedido> detalles;

    @ManyToOne
    @JoinColumn(name = "idCategoria", nullable = false,foreignKey = @ForeignKey (name = "fk_categoria_producto"))
    @JsonBackReference
    private Categoria categoria;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_envio", nullable = false)
    @JsonBackReference("producto-envio")
    private Envio envio;
}