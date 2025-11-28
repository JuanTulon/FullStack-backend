package com.joyeria.joyeria.controller;

import com.joyeria.joyeria.model.Categoria;
import com.joyeria.joyeria.service.CategoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Tag(name = "Categorías", description = "Operaciones relacionadas con las categorías de productos")
@RestController
@RequestMapping("/api/v1/categorias")
public class CategoriaController {

    @Autowired
    private CategoriaService categoriaService;

    @Operation(summary = "Listar todas las cateogorias", responses = {
            @ApiResponse(responseCode = "200", description = "Lista de categorias obtenidas correctamente",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Categoria.class),
                    examples = @ExampleObject(value = "")
                )
            ),
            @ApiResponse(responseCode = "204", description = "No hay categorias registradas",
                content = @Content)
    })
    @GetMapping
    public ResponseEntity<List<Categoria>> listar() {
        List<Categoria> categorias = categoriaService.findAll();
        if (categorias.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(categorias);
    }

    @Operation(summary = "Guardar una nueva categoria", responses = {
            @ApiResponse(responseCode = "201", description = "categoria creada correctamente",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Categoria.class),
                    examples = @ExampleObject(value = "")
                )
            ),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida",
                content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = "{ 'error': 'algun dato es inválido'}")
                )
            )
    })
    @PostMapping
    public ResponseEntity<Categoria> guardar(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Categoria a crear",
            required = true,
            content = @Content(schema = @Schema(implementation = Categoria.class),
                examples = @ExampleObject(value = "" )
            )
        )
        @RequestBody Categoria categoria) {
        Categoria nuevaCategoria = categoriaService.save(categoria);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaCategoria);
    }

    @Operation(summary = "Buscar categorias por id", responses = {
            @ApiResponse(responseCode = "200", description = "categoria encontrada",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Categoria.class),
                    examples = @ExampleObject(value = "")
                )
            ),
            @ApiResponse(responseCode = "404", description = "categoria no encontrada",
                content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = "{ 'error': 'categoria no encontrada' }")
                )
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<Categoria> buscar(
        @Parameter(description = "ID de la categoría a buscar", required = true, example = "1")
        @PathVariable("id") Integer idCategoria) {
        try {
            Categoria categoria = categoriaService.findById(idCategoria);
            return ResponseEntity.ok(categoria);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Buscar categorias por nombre", responses = {
            @ApiResponse(responseCode = "200", description = "categoria encontrada",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Categoria.class),
                    examples = @ExampleObject(value = "")
                )
            ),
            @ApiResponse(responseCode = "404", description = "categoria no encontrada",
                content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = "{ 'error': 'categoria no encontrada' }")
                )
            )
    })
    @GetMapping("/nombre/{nom}")
    public ResponseEntity<List<Categoria>> buscarPorNombre(
        @Parameter(description = "Nombre de la categoría a buscar", required = true, example = "Amaderado")
        @PathVariable String nom) {
        try {
            List<Categoria> categorias = categoriaService.findByNombre(nom);
            return ResponseEntity.ok(categorias);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Actualizar una categoría", responses = {
            @ApiResponse(responseCode = "200", description = "categoría actualizada correctamente",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Categoria.class),
                    examples = @ExampleObject(value = "{ 'idCategoria': 1, 'nombreCategoria': 'Amaderado', 'productos': [] }"))
            ),
            @ApiResponse(responseCode = "404", description = "categoría no encontrada",
                content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = "{ 'error': 'categoría no encontrada' }"))
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<Categoria> actualizar(
        @Parameter(description = "ID de la categoría a actualizar", required = true, example = "1")
        @PathVariable Integer idCategoria,
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Datos de la categoria a actualizar",
            required = true,
            content = @Content(schema = @Schema(implementation = Categoria.class),
                examples = @ExampleObject(value = "{ 'idCategoria': 1, 'nombreCategoria': 'Amaderado', 'productos': [] }")
            )
        )
        @RequestBody Categoria categoria) {
        try {
            Categoria cat = categoriaService.findById(idCategoria);
            cat.setNombreCategoria(categoria.getNombreCategoria());
            cat.setProductos(categoria.getProductos());
            categoriaService.save(cat);
            return ResponseEntity.ok(cat);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Eliminar una categoría", responses = {
            @ApiResponse(responseCode = "204", description = "categoría eliminada correctamente",
                content = @Content),
            @ApiResponse(responseCode = "404", description = "categoría no encontrada",
                content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = "{ 'error': 'categoría no encontrada' }"))
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
        @Parameter(description = "ID de la categoría a eliminar", required = true, example = "1")
        @PathVariable("id") Integer idCategoria) {
        try {
            categoriaService.delete(idCategoria);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
