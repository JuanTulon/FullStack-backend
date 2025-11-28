package com.joyeria.joyeria.controller;

import com.joyeria.joyeria.model.Pedido;
import com.joyeria.joyeria.model.DetallePedido;
import com.joyeria.joyeria.service.PedidoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@Tag(name = "Pedidos", description = "Gestión de órdenes de compra")
@RestController
@RequestMapping("/api/v1/pedidos")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    // --- LISTAR PEDIDOS ---
    @Operation(summary = "Listar historial de pedidos", responses = {
        @ApiResponse(responseCode = "200", description = "Pedidos encontrados",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = Pedido.class),
                examples = @ExampleObject(value = """
                    [
                        {
                            "idPedido": 1,
                            "fechaPedido": "2023-11-25",
                            "estadoPedido": "Pagado",
                            "totalPedido": 50000,
                            "direccionEnvio": "Av. Siempre Viva 742",
                            "metodoPago": "WebPay",
                            "usuario": {
                                "id": 1,
                                "nombre": "Juan",
                                "email": "juan@example.com"
                            },
                            "detalles": []
                        }
                    ]
                """)
            )
        ),
        @ApiResponse(responseCode = "204", description = "No hay pedidos registrados", content = @Content)
    })
    @GetMapping
    public ResponseEntity<List<Pedido>> listar() {
        List<Pedido> pedidos = pedidoService.findAll();
        return pedidos.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(pedidos);
    }

    // --- CREAR PEDIDO (POST) ---
    @Operation(summary = "Crear nuevo pedido", description = "Registra un pedido asociándolo a un usuario existente por su ID.", responses = {
        @ApiResponse(responseCode = "201", description = "Pedido creado",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = Pedido.class),
                examples = @ExampleObject(name = "Nuevo Pedido", value = """
                    {
                        "fechaPedido": "2023-11-26",
                        "estadoPedido": "Pendiente",
                        "totalPedido": 0,
                        "direccionEnvio": "Calle Falsa 123, Santiago",
                        "metodoPago": "Transferencia",
                        "usuario": {
                            "id": 1
                        }
                    }
                """)
            )
        ),
        @ApiResponse(responseCode = "400", description = "Error: Usuario no existe o datos inválidos", content = @Content)
    })
    @PostMapping
    public ResponseEntity<Pedido> guardar(@RequestBody Pedido pedido) {
        try {
            Pedido nuevo = pedidoService.save(pedido);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // --- BUSCAR POR ID ---
    @Operation(summary = "Buscar pedido por ID", responses = {
        @ApiResponse(responseCode = "200", description = "Encontrado",
            content = @Content(schema = @Schema(implementation = Pedido.class),
            examples = @ExampleObject(value = """
                {
                    "idPedido": 1,
                    "fechaPedido": "2023-11-26",
                    "estadoPedido": "Pendiente",
                    "totalPedido": 25000,
                    "usuario": { "id": 1, "nombre": "Juan" }
                }
            """))
        ),
        @ApiResponse(responseCode = "404", description = "No encontrado", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<Pedido> buscar(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(pedidoService.findById(id));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // --- ACTUALIZAR PEDIDO (PUT) ---
    @Operation(summary = "Actualizar pedido", description = "Permite actualizar estado, dirección, total, etc.", responses = {
        @ApiResponse(responseCode = "200", description = "Actualizado correctamente",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = Pedido.class),
                examples = @ExampleObject(name = "Actualizar Estado", value = """
                    {
                        "fechaPedido": "2023-11-26",
                        "estadoPedido": "Enviado",
                        "totalPedido": 25000,
                        "direccionEnvio": "Calle Nueva 456",
                        "metodoPago": "Transferencia"
                    }
                """)
            )
        ),
        @ApiResponse(responseCode = "404", description = "Pedido no encontrado", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<Pedido> actualizar(@PathVariable Integer id, @RequestBody Pedido pedido) {
        try {
            // Nota: El servicio actualiza fecha, total, estado, direccion, pago.
            // No actualiza el usuario dueño del pedido (lógica de negocio habitual).
            return ResponseEntity.ok(pedidoService.actualizarPedido(id, pedido));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // --- ELIMINAR PEDIDO ---
    @Operation(summary = "Eliminar pedido por su id", responses = {
        @ApiResponse(responseCode = "204", description = "Eliminado correctamente", content = @Content),
        @ApiResponse(responseCode = "404", description = "No encontrado", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        try {
            pedidoService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // --- ENDPOINTS ADICIONALES ---

    @Operation(summary = "Ver productos (detalles) de un pedido por su ID")
    @GetMapping("/{id}/detalles")
    public ResponseEntity<List<DetallePedido>> listarDetalles(@PathVariable Integer id) {
        try {
            Pedido pedido = pedidoService.findById(id);
            return ResponseEntity.ok(pedido.getDetalles());
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Buscar pedidos por rango de fechas")
    @GetMapping("/fecha")
    public ResponseEntity<List<Pedido>> getPedidosPorFecha(
            @Parameter(description = "Fecha inicio (yyyy-MM-dd)", example = "2023-01-01") 
            @RequestParam("inicio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date inicio,
            @Parameter(description = "Fecha fin (yyyy-MM-dd)", example = "2023-12-31") 
            @RequestParam("fin") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date fin) {
        List<Pedido> pedidos = pedidoService.findByFechaPedidoBetween(inicio, fin);
        return pedidos.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(pedidos);
    }
}