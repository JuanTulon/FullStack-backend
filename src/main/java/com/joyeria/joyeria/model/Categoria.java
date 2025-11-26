package com.joyeria.joyeria.model;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "categoria")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "categoria", description = "Entidad que representa a una categoria de perfumes del sistema.")
public class Categoria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // El valor del ID se generará automáticamente.
    @Schema(description = "Identificador único del cliente", example = "1")
    private Integer idCategoria;

    @Schema(description = "Nombre de la categoría", example = "amaderado")
    private String nombreCategoria;

    @OneToMany(mappedBy = "categoria", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Producto> productos;
}