package com.joyeria.joyeria.controller;

import com.joyeria.joyeria.model.DetallePedido;
import com.joyeria.joyeria.service.DetallePedidoService; 
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

@CrossOrigin("http://hoseki.s3-website.us-east-2.amazonaws.com")
@Tag(name = "Detalle de Pedidos", description = "Gestión de los productos dentro de cada pedido")
@RestController
@RequestMapping("/api/v1/detalle-pedidos")
public class DetallePedidoController {

    @Autowired
    private DetallePedidoService detallePedidoService;

    // --- LISTAR DETALLES ---
    @Operation(summary = "Listar todos los detalles", description = "Devuelve la lista de detalles incluyendo la información básica del Producto y Pedido asociado.", responses = {
        @ApiResponse(responseCode = "200", description = "Lista encontrada",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = DetallePedido.class),
                examples = @ExampleObject(value = """
                    [
                        {
                            "id": 1,
                            "cantidadProducto": 2,
                            "subtotal": 50000,
                            "producto": {
                                "idProducto": 1,
                                "nombreProducto": "Anillo de Oro",
                                "precio": 25000
                            },
                            "pedido": {
                                "idPedido": 10,
                                "fechaPedido": "2023-11-25"
                            }
                        }
                    ]
                """)
            )
        ),
        @ApiResponse(responseCode = "204", description = "No hay detalles registrados", content = @Content)
    })
    @GetMapping
    public ResponseEntity<List<DetallePedido>> listar() {
        List<DetallePedido> detalles = detallePedidoService.findAll();
        return detalles.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(detalles);
    }

    // --- CREAR DETALLE (POST) ---
    @Operation(summary = "Agregar detalle a un pedido", description = "Crea un detalle vinculando un Producto y un Pedido por sus IDs.", responses = {
        @ApiResponse(responseCode = "201", description = "Detalle creado exitosamente",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = DetallePedido.class),
                examples = @ExampleObject(name = "Nuevo Detalle", value = """
                    {
                        "cantidadProducto": 3,
                        "subtotal": 45000,
                        "producto": {
                            "idProducto": 1
                        },
                        "pedido": {
                            "idPedido": 10
                        }
                    }
                """)
            )
        ),
        @ApiResponse(responseCode = "400", description = "Error: IDs de producto o pedido no existen", content = @Content)
    })
    @PostMapping
    public ResponseEntity<DetallePedido> guardar(@RequestBody DetallePedido detalle) {
        try {
            DetallePedido nuevo = detallePedidoService.save(detalle);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // --- BUSCAR POR ID ---
    @Operation(summary = "Buscar detalle por ID", responses = {
        @ApiResponse(responseCode = "200", description = "Encontrado",
            content = @Content(schema = @Schema(implementation = DetallePedido.class),
            examples = @ExampleObject(value = """
                {
                    "id": 1,
                    "cantidadProducto": 2,
                    "subtotal": 50000,
                    "producto": {
                        "idProducto": 1,
                        "nombreProducto": "Anillo de Oro"
                    },
                    "pedido": {
                        "idPedido": 10
                    }
                }
            """))
        ),
        @ApiResponse(responseCode = "404", description = "No encontrado", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<DetallePedido> buscar(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(detallePedidoService.findById(id));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // --- ACTUALIZAR DETALLE (PUT) ---
    @Operation(summary = "Actualizar detalle", description = "Permite modificar la cantidad, subtotal o reasignar producto/pedido.", responses = {
        @ApiResponse(responseCode = "200", description = "Actualizado correctamente",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = DetallePedido.class),
                examples = @ExampleObject(name = "Actualizar Datos", value = """
                    {
                        "cantidadProducto": 5,
                        "subtotal": 75000,
                        "producto": {
                            "idProducto": 2
                        },
                        "pedido": {
                            "idPedido": 10
                        }
                    }
                """)
            )
        ),
        @ApiResponse(responseCode = "404", description = "Detalle no encontrado", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<DetallePedido> actualizar(@PathVariable Integer id, @RequestBody DetallePedido detalle) {
        try {
            return ResponseEntity.ok(detallePedidoService.actualizarDetallePedido(id, detalle));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // --- ELIMINAR DETALLE ---
    @Operation(summary = "Eliminar detalle por id de detalle", responses = {
        @ApiResponse(responseCode = "204", description = "Eliminado correctamente", content = @Content),
        @ApiResponse(responseCode = "404", description = "No encontrado", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        try {
            detallePedidoService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}