package com.joyeria.joyeria.controller;

import com.joyeria.joyeria.model.Envio;
import com.joyeria.joyeria.service.EnvioService;
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

@Tag(name = "Envíos", description = "Seguimiento y gestión de despachos de pedidos")
@RestController
@RequestMapping("/api/v1/envios")
public class EnvioController {

    @Autowired
    private EnvioService envioService;

    // --- LISTAR ENVÍOS ---
    @Operation(summary = "Listar todos los envíos", description = "Muestra el historial de envíos incluyendo el pedido asociado.", responses = {
        @ApiResponse(responseCode = "200", description = "Lista encontrada",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = Envio.class),
                examples = @ExampleObject(value = """
                    [
                        {
                            "id_envio": 10,
                            "fecha_envio": "2023-11-20",
                            "estado_envio": "Entregado",
                            "pedido": {
                                "idPedido": 5,
                                "totalPedido": 50000,
                                "estadoPedido": "Pagado"
                            }
                        },
                        {
                            "id_envio": 11,
                            "fecha_envio": "2023-11-25",
                            "estado_envio": "En Camino",
                            "pedido": {
                                "idPedido": 8,
                                "totalPedido": 120000,
                                "estadoPedido": "Enviado"
                            }
                        }
                    ]
                """)
            )
        ),
        @ApiResponse(responseCode = "204", description = "No hay envíos registrados", content = @Content)
    })
    @GetMapping
    public ResponseEntity<List<Envio>> listar() {
        List<Envio> envios = envioService.findAll();
        return envios.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(envios);
    }

    // --- CREAR ENVÍO (POST) ---
    @Operation(summary = "Generar un nuevo envío", description = "Crea el registro de envío asociado a un Pedido existente (por ID).", responses = {
        @ApiResponse(responseCode = "201", description = "Envío creado exitosamente",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = Envio.class),
                examples = @ExampleObject(name = "Nuevo Envío", value = """
                    {
                        "fecha_envio": "2023-11-26",
                        "estado_envio": "En Preparación",
                        "pedido": {
                            "idPedido": 1
                        }
                    }
                """)
            )
        ),
        @ApiResponse(responseCode = "400", description = "Error: Pedido no existe o ya tiene envío asignado", content = @Content)
    })
    @PostMapping
    public ResponseEntity<Envio> guardar(@RequestBody Envio envio) {
        try {
            Envio envioNuevo = envioService.save(envio);
            return ResponseEntity.status(HttpStatus.CREATED).body(envioNuevo);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // --- BUSCAR POR ID ---
    @Operation(summary = "Buscar envío por ID", responses = {
        @ApiResponse(responseCode = "200", description = "Envío encontrado",
            content = @Content(schema = @Schema(implementation = Envio.class),
            examples = @ExampleObject(value = """
                {
                    "id_envio": 10,
                    "fecha_envio": "2023-11-20",
                    "estado_envio": "Entregado",
                    "pedido": {
                        "idPedido": 5
                    }
                }
            """))
        ),
        @ApiResponse(responseCode = "404", description = "No encontrado", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<Envio> buscar(@PathVariable("id") Integer idEnvio) {
        try {
            return ResponseEntity.ok(envioService.findById(idEnvio));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // --- ACTUALIZAR ENVÍO (PUT) ---
    @Operation(summary = "Actualizar estado o fecha", description = "Permite cambiar el estado del envío (ej: de 'En Preparación' a 'Enviado').", responses = {
        @ApiResponse(responseCode = "200", description = "Actualizado correctamente",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = Envio.class),
                examples = @ExampleObject(name = "Actualizar Estado", value = """
                    {
                        "fecha_envio": "2023-11-27",
                        "estado_envio": "Enviado"
                    }
                """)
            )
        ),
        @ApiResponse(responseCode = "404", description = "Envío no encontrado", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<Envio> actualizar(@PathVariable("id") Integer idEnvio, @RequestBody Envio envio) {
        try {
            // Nota: El servicio 'actualizarEnvio' solo actualiza fecha y estado, ignora el pedido
            return ResponseEntity.ok(envioService.actualizarEnvio(idEnvio, envio));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // --- ELIMINAR ENVÍO ---
    @Operation(summary = "Eliminar registro de envío por id", responses = {
        @ApiResponse(responseCode = "204", description = "Eliminado correctamente", content = @Content),
        @ApiResponse(responseCode = "404", description = "No encontrado", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable("id") Integer idEnvio) {
        try {
            envioService.delete(idEnvio);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}