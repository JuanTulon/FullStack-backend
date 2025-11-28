package com.joyeria.joyeria.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Entity
@Table(name= "producto")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "Producto", description = "Entidad que representa un producto del catálogo.")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único del producto", example = "1")
    private Integer idProducto;

    @Column(nullable=false)
    @Schema(description = "Nombre del producto", example = "Anillo de Diamantes")
    private String nombreProducto;

    @Column(nullable=false)
    @Schema(description = "Descripción detallada", example = "Anillo de oro blanco de 18k con diamante central.")
    private String descripcionProducto;

    @Column(nullable=true)
    @Schema(description = "Precio unitario", example = "500000")
    private Integer precio;

    @Column(nullable=false)
    @Schema(description = "Stock disponible", example = "10")
    private Integer stock;

    @Column(length = 500)
    private String foto;

    //Usamos @JsonIgnore para que al listar productos NO traiga todo el historial de ventas
    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore 
    private List<DetallePedido> detalles;

    //Usamos @JsonIgnoreProperties para ver la categoría, pero sin sus productos (evita bucle)
    @ManyToOne
    @JoinColumn(name = "idCategoria", nullable = false, foreignKey = @ForeignKey(name = "fk_categoria_producto"))
    @JsonIgnoreProperties("productos")
    private Categoria categoria;

}