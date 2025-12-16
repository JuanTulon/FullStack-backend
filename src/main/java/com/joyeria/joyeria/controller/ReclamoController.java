package com.joyeria.joyeria.controller;

import com.joyeria.joyeria.model.Reclamo;
import com.joyeria.joyeria.service.ReclamoService;
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

@CrossOrigin("http://hoseki.s3-website.us-east-2.amazonaws.com")
@Tag(name = "Reclamos", description = "Gestión de contacto, dudas y reclamos de clientes")
@RestController
@RequestMapping("/api/v1/reclamos")
public class ReclamoController {

    @Autowired
    private ReclamoService reclamoService;

    // --- LISTAR RECLAMOS ---
    @Operation(summary = "Listar todos los reclamos", responses = {
        @ApiResponse(responseCode = "200", description = "Lista encontrada",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = Reclamo.class),
                examples = @ExampleObject(value = """
                    [
                        {
                            "idReclamo": 1,
                            "nombre": "María González",
                            "rut": "12.345.678-9",
                            "correo": "maria.gonzalez@email.com",
                            "telefono": "+56912345678",
                            "problema": "Envío atrasado",
                            "duda": "Mi pedido debía llegar ayer y no ha llegado.",
                            "fechaCreacion": "2023-10-25T10:30:00.000+00:00"
                        }
                    ]
                """)
            )
        ),
        @ApiResponse(responseCode = "204", description = "No hay reclamos registrados", content = @Content)
    })
    @GetMapping
    public ResponseEntity<List<Reclamo>> listar() {
        List<Reclamo> reclamos = reclamoService.findAll();
        return reclamos.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(reclamos);
    }

    // --- CREAR RECLAMO (POST) ---
    @Operation(summary = "Crear nuevo reclamo", description = "Registra un nuevo contacto o reclamo. La fecha se asigna automáticamente.", responses = {
        @ApiResponse(responseCode = "201", description = "Reclamo registrado",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = Reclamo.class),
                examples = @ExampleObject(name = "Nuevo Reclamo", value = """
                    {
                        "nombre": "Carlos Pérez",
                        "rut": "9.876.543-2",
                        "correo": "carlos.perez@email.com",
                        "telefono": "+56987654321",
                        "problema": "Producto defectuoso",
                        "duda": "El anillo llegó con una piedra suelta."
                    }
                """)
            )
        ),
        @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content)
    })
    @PostMapping
    public ResponseEntity<Reclamo> guardar(@RequestBody Reclamo reclamo) {
        try {
            Reclamo nuevo = reclamoService.save(reclamo);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // --- BUSCAR POR ID ---
    @Operation(summary = "Buscar reclamo por ID", responses = {
        @ApiResponse(responseCode = "200", description = "Encontrado",
            content = @Content(schema = @Schema(implementation = Reclamo.class),
            examples = @ExampleObject(value = """
                {
                    "idReclamo": 1,
                    "nombre": "María González",
                    "rut": "12.345.678-9",
                    "correo": "maria.gonzalez@email.com",
                    "telefono": "+56912345678",
                    "problema": "Envío atrasado",
                    "duda": "Mi pedido debía llegar ayer.",
                    "fechaCreacion": "2023-10-25T10:30:00.000+00:00"
                }
            """))
        ),
        @ApiResponse(responseCode = "404", description = "No encontrado", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<Reclamo> buscar(@PathVariable("id") Integer idReclamo) {
        try {
            return ResponseEntity.ok(reclamoService.findById(idReclamo));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // --- BUSCAR POR TIPO DE PROBLEMA ---
    @Operation(summary = "Buscar por tipo de problema", description = "Busca reclamos que coincidan con el tipo de problema descrito.", parameters = {
        @Parameter(name = "problema", description = "Descripción del problema (ej: Envío atrasado)", required = true, example = "Envío atrasado")
    })
    @GetMapping("/problema/{problema}")
    public ResponseEntity<Reclamo> buscarPorProblema(@PathVariable String problema) {
        try {
            return ResponseEntity.ok(reclamoService.buscarPorProblema(problema));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // --- ACTUALIZAR RECLAMO (PUT) ---
    @Operation(summary = "Actualizar datos del reclamo", description = "Permite corregir información de contacto o actualizar el detalle de la duda.", responses = {
        @ApiResponse(responseCode = "200", description = "Actualizado correctamente",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = Reclamo.class),
                examples = @ExampleObject(name = "Actualizar Datos", value = """
                    {
                        "nombre": "Carlos Pérez",
                        "rut": "9.876.543-2",
                        "correo": "carlos.nuevo@email.com",
                        "telefono": "+56911112222",
                        "problema": "Producto defectuoso",
                        "duda": "ACTUALIZACIÓN: Ya envié las fotos del producto dañado."
                    }
                """)
            )
        ),
        @ApiResponse(responseCode = "404", description = "Reclamo no encontrado", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<Reclamo> actualizar(@PathVariable Integer id, @RequestBody Reclamo reclamo) {
        try {
            return ResponseEntity.ok(reclamoService.actualizarReclamo(id, reclamo));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // --- ELIMINAR RECLAMO ---
    @Operation(summary = "Eliminar reclamo por su id", responses = {
        @ApiResponse(responseCode = "204", description = "Eliminado correctamente", content = @Content),
        @ApiResponse(responseCode = "404", description = "No encontrado", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable("id") Integer idReclamo) {
        try {
            reclamoService.delete(idReclamo);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}