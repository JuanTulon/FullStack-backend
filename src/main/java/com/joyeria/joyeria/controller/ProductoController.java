package com.joyeria.joyeria.controller;

import com.joyeria.joyeria.model.Producto;
import com.joyeria.joyeria.service.ProductoService;
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

@Tag(name = "productos", description = "operaciones relacionadas con los productos")
@RestController
@RequestMapping("/api/v1/productos")
public class ProductoController {
@Autowired

    private ProductoService productoService;

    @Operation(summary = "Listar todos los productos", responses = {
            @ApiResponse(responseCode = "200", description = "Lista de productos obtenidos correctamente",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Producto.class),
                    examples = @ExampleObject(value = "[{ \"id\": 1, \"nombre\": \"Producto 1\", \"descripcion\": \"Descripción del Producto 1\", \"precio\": 100.0, \"stock\": 10 }, { \"id\": 2, \"nombre\": \"Producto 2\", \"descripcion\": \"Descripción del Producto 2\", \"precio\": 200.0, \"stock\": 5 }]")
                )
            ),
            @ApiResponse(responseCode = "204", description = "No hay productos registrados",
                content = @Content)
    })
    @GetMapping
    public ResponseEntity<List<Producto>> listar() {
        List<Producto> productos  = productoService.findAll();
        if (productos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(productos);
    }

    @Operation(summary = "Guardar un nuevo producto", responses = {
            @ApiResponse(responseCode = "201", description = "producto creado correctamente",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Producto.class),
                    examples = @ExampleObject(value = "{ 'id': 1, 'nombre': 'Producto 1', 'descripcion': 'Descripción del Producto 1', 'precio': 100.0, 'stock': 10 }")
                )
            ),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida",
                content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = "{ 'error': 'algun dato es inválido'}")
                )
            )
    })
    @PostMapping
    public ResponseEntity<Producto> guardar(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "producto a crear",
            required = true,
            content = @Content(schema = @Schema(implementation = Producto.class),
                examples = @ExampleObject(value = "{ 'id': 1, 'nombre': 'Producto 1', 'descripcion': 'Descripción del Producto 1', 'precio': 100.0, 'stock': 10 }" )
            )
        )
        @RequestBody Producto producto) {
        Producto productoNuevo = productoService.save(producto);
        return ResponseEntity.status(HttpStatus.CREATED).body(productoNuevo);
    }

    @Operation(summary = "Buscar Producto por id", responses = {
            @ApiResponse(responseCode = "200", description = "Producto encontrado",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Producto.class),
                    examples = @ExampleObject(value = "{ 'id': 1, 'nombre': 'Producto 1', 'descripcion': 'Descripción del Producto 1', 'precio': 100.0, 'stock': 10 }")
                )
            ),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado",
                content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = "{ 'error': 'Producto no encontrado' }")
                )
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<Producto> buscar(
        @Parameter(description = "ID del producto a buscar", required = true, example = "1")
        @PathVariable("id") Integer idProducto) {
        try {
            Producto producto = productoService.findById(idProducto);
            return ResponseEntity.ok(producto);
        } catch ( Exception e ) {
            return  ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Buscar producto por nombre", responses = {
            @ApiResponse(responseCode = "200", description = "producto encontrado",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Producto.class),
                    examples = @ExampleObject(value = "{ 'id': 1, 'nombre': 'Producto1', 'descripcion': 'Descripción del Producto1', 'precio': 100.0, 'stock': 10 }")
                )
            ),
            @ApiResponse(responseCode = "404", description = "producto no encontrado",
                content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = "{ 'error': 'producto no encontrado' }")
                )
            )
    })
    @GetMapping("/nombre/{nom}")
    public ResponseEntity<List<Producto>> buscarPorNombre(
        @Parameter(description = "Nombre del producto a buscar", required = true, example = "Producto1")
        @PathVariable String nom) {
        try {
            List<Producto> productos = productoService.findBynombre(nom);
            return ResponseEntity.ok(productos);
        } catch ( Exception e ) {
            return  ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Actualizar un Producto por id", responses = {
            @ApiResponse(responseCode = "200", description = "Producto actualizado correctamente",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Producto.class),
                    examples = @ExampleObject(value = "{ 'id': 1, 'nombre': 'Producto 1', 'descripcion': 'Descripción del Producto 1', 'precio': 100.0, 'stock': 10 }"))
            ),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado",
                content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = "{ 'error': 'Producto no encontrado' }"))
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<Producto> actualizar(
        @Parameter(description = "ID del producto a actualizar", required = true, example = "1")
        @PathVariable Integer idProducto,
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Datos del Producto a actualizar",
            required = true,
            content = @Content(schema = @Schema(implementation = Producto.class),
                examples = @ExampleObject(value = "{ 'id': 1, 'nombre': 'Producto 1', 'descripcion': 'Descripción del Producto 1', 'precio': 100.0, 'stock': 10 }")
            )
        ) 
        @RequestBody Producto producto) {
        try {
            Producto pro = productoService.findById(idProducto);
            pro.setDescripcionProducto(producto.getDescripcionProducto());
            pro.setIdProducto(producto.getIdProducto());
            pro.setNombreProducto(producto.getNombreProducto());
            pro.setPrecio(producto.getPrecio());
            pro.setStock(producto.getStock());

            productoService.save(pro);
            return ResponseEntity.ok(pro);
        } catch ( Exception e ) {
            return  ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Eliminar un Producto", responses = {
            @ApiResponse(responseCode = "204", description = " Producto eliminado correctamente",
                content = @Content),
            @ApiResponse(responseCode = "404", description = " Producto no encontrado",
                content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = "{ 'error': ' Producto no encontrado' }"))
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(
        @Parameter(description = "ID del producto a eliminar", required = true, example = "1")
        @PathVariable Integer idProducto) {
        try {
            productoService.delete(idProducto);
            return ResponseEntity.noContent().build();
        } catch ( Exception e ) {
            return  ResponseEntity.notFound().build();
        }
    }
}
