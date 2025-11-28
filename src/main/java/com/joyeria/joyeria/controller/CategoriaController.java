package com.joyeria.joyeria.controller;

import com.joyeria.joyeria.model.Categoria;
import com.joyeria.joyeria.service.CategoriaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Categorías", description = "Gestión de tipos de joyas (anillos, collares, etc.)")
@RestController
@RequestMapping("/api/v1/categorias")
public class CategoriaController {

    @Autowired
    private CategoriaService categoriaService;

    // --- LISTAR CATEGORÍAS ---
    @Operation(summary = "Listar todas las categorías", responses = {
        @ApiResponse(responseCode = "200", description = "Lista encontrada",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = Categoria.class),
                examples = @ExampleObject(value = """
                    [
                        {
                            "idCategoria": 1,
                            "nombreCategoria": "Anillos de Compromiso"
                        },
                        {
                            "idCategoria": 2,
                            "nombreCategoria": "Relojes de Lujo"
                        }
                    ]
                """)
            )
        ),
        @ApiResponse(responseCode = "204", description = "No hay categorías registradas", content = @Content)
    })
    @GetMapping
    public ResponseEntity<List<Categoria>> listar() {
        List<Categoria> categorias = categoriaService.findAll();
        return categorias.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(categorias);
    }

    // --- CREAR CATEGORÍA (POST) ---
    @Operation(summary = "Crear nueva categoría", description = "Crea una nueva categoría. Solo se requiere el nombre.", responses = {
        @ApiResponse(responseCode = "201", description = "Categoría creada",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = Categoria.class),
                examples = @ExampleObject(name = "Nueva Categoría", value = """
                    {
                        "nombreCategoria": "Pulseras de Plata"
                    }
                """)
            )
        ),
        @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content)
    })
    @PostMapping
    public ResponseEntity<Categoria> guardar(@RequestBody Categoria categoria) {
        try {
            Categoria nuevaCategoria = categoriaService.save(categoria);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevaCategoria);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // --- BUSCAR POR ID ---
    @Operation(summary = "Buscar categoría por ID", responses = {
        @ApiResponse(responseCode = "200", description = "Encontrada",
            content = @Content(schema = @Schema(implementation = Categoria.class),
            examples = @ExampleObject(value = """
                {
                    "idCategoria": 1,
                    "nombreCategoria": "Anillos de Compromiso",
                    "productos": []
                }
            """))
        ),
        @ApiResponse(responseCode = "404", description = "No encontrada", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<Categoria> buscar(@PathVariable("id") Integer idCategoria) {
        try {
            Categoria categoria = categoriaService.findById(idCategoria);
            return ResponseEntity.ok(categoria);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // --- ACTUALIZAR CATEGORÍA (PUT) ---
    @Operation(summary = "Actualizar nombre de categoría", description = "Actualiza el nombre de una categoría existente.", responses = {
        @ApiResponse(responseCode = "200", description = "Actualizada correctamente",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = Categoria.class),
                examples = @ExampleObject(name = "Actualizar Nombre", value = """
                    {
                        "nombreCategoria": "Pulseras de Oro y Plata"
                    }
                """)
            )
        ),
        @ApiResponse(responseCode = "404", description = "Categoría no encontrada", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<Categoria> actualizar(@PathVariable Integer id, @RequestBody Categoria categoria) {
        try {
            Categoria actualizada = categoriaService.actualizarCategoria(id, categoria);
            return ResponseEntity.ok(actualizada);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // --- ELIMINAR CATEGORÍA ---
    @Operation(summary = "Eliminar categoría", description = "Elimina la categoría por su ID.", responses = {
        @ApiResponse(responseCode = "204", description = "Categoría eliminada", content = @Content),
        @ApiResponse(responseCode = "404", description = "Categoría no encontrada", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable("id") Integer idCategoria) {
        try {
            categoriaService.delete(idCategoria);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}