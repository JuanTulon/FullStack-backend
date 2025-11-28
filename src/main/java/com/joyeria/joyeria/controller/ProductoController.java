package com.joyeria.joyeria.controller;

import com.joyeria.joyeria.model.Producto;
import com.joyeria.joyeria.service.ProductoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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

@Tag(name = "Productos", description = "Gestión del catálogo de joyas")
@RestController
@RequestMapping("/api/v1/productos")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    // --- LISTAR PRODUCTOS ---
    @Operation(summary = "Listar catálogo completo", description = "Devuelve la lista de productos con su categoría asociada.", responses = {
        @ApiResponse(responseCode = "200", description = "Lista encontrada",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = Producto.class),
                examples = @ExampleObject(value = """
                    [
                        {
                            "idProducto": 1,
                            "nombreProducto": "Anillo de Oro",
                            "descripcionProducto": "18k con incrustaciones",
                            "precio": 150000,
                            "stock": 5,
                            "categoria": {
                                "idCategoria": 1,
                                "nombreCategoria": "Anillos"
                            }
                        },
                        {
                            "idProducto": 2,
                            "nombreProducto": "Collar de Perlas",
                            "descripcionProducto": "Perlas naturales",
                            "precio": 80000,
                            "stock": 10,
                            "categoria": {
                                "idCategoria": 2,
                                "nombreCategoria": "Collares"
                            }
                        }
                    ]
                """)
            )
        ),
        @ApiResponse(responseCode = "204", description = "No hay productos", content = @Content)
    })
    @GetMapping
    public ResponseEntity<List<Producto>> listar() {
        List<Producto> productos = productoService.findAll();
        return productos.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(productos);
    }

    // --- CREAR PRODUCTO (POST) ---
    @Operation(summary = "Crear nuevo producto", description = "Crea un producto asignándole una categoría existente por ID.", responses = {
        @ApiResponse(responseCode = "201", description = "Producto creado",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = Producto.class),
                examples = @ExampleObject(name = "Nuevo Producto", value = """
                    {
                        "nombreProducto": "Pulsera de Plata",
                        "descripcionProducto": "Plata fina 925",
                        "precio": 45000,
                        "stock": 20,
                        "categoria": {
                            "idCategoria": 1
                        }
                    }
                """)
            )
        ),
        @ApiResponse(responseCode = "400", description = "Error: Categoría no existe o datos inválidos", content = @Content)
    })
    @PostMapping
    public ResponseEntity<Producto> guardar(@RequestBody Producto producto) {
        try {
            Producto nuevo = productoService.save(producto);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // --- BUSCAR POR ID ---
    @Operation(summary = "Buscar producto por ID", responses = {
        @ApiResponse(responseCode = "200", description = "Encontrado",
            content = @Content(schema = @Schema(implementation = Producto.class),
            examples = @ExampleObject(value = """
                {
                    "idProducto": 1,
                    "nombreProducto": "Anillo de Oro",
                    "descripcionProducto": "18k con incrustaciones",
                    "precio": 150000,
                    "stock": 5,
                    "categoria": {
                        "idCategoria": 1,
                        "nombreCategoria": "Anillos"
                    }
                }
            """))
        ),
        @ApiResponse(responseCode = "404", description = "No encontrado", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<Producto> buscar(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(productoService.findById(id));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // --- BUSCAR POR NOMBRE ---
    @Operation(summary = "Buscar por nombre", description = "Busca productos que coincidan exactamente con el nombre.", parameters = {
        @Parameter(name = "nom", description = "Nombre exacto del producto", required = true, example = "Anillo de Oro")
    })
    @GetMapping("/nombre/{nom}")
    public ResponseEntity<List<Producto>> buscarPorNombre(@PathVariable String nom) {
        List<Producto> productos = productoService.findBynombre(nom);
        return productos.isEmpty() ? ResponseEntity.notFound().build() : ResponseEntity.ok(productos);
    }

    // --- ACTUALIZAR PRODUCTO (PUT) ---
    @Operation(summary = "Actualizar producto", description = "Actualiza stock, precio, descripción o categoría.", responses = {
        @ApiResponse(responseCode = "200", description = "Actualizado correctamente",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = Producto.class),
                examples = @ExampleObject(name = "Actualizar Stock y Precio", value = """
                    {
                        "nombreProducto": "Pulsera de Plata",
                        "descripcionProducto": "Plata fina 925 (Oferta)",
                        "precio": 40000,
                        "stock": 50,
                        "categoria": {
                            "idCategoria": 1
                        }
                    }
                """)
            )
        ),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<Producto> actualizar(@PathVariable Integer id, @RequestBody Producto producto) {
        try {
            return ResponseEntity.ok(productoService.actualizarProducto(id, producto));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // --- ELIMINAR PRODUCTO ---
    @Operation(summary = "Eliminar producto por su id", responses = {
        @ApiResponse(responseCode = "204", description = "Eliminado correctamente", content = @Content),
        @ApiResponse(responseCode = "404", description = "No encontrado", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        try {
            productoService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}