package com.joyeria.joyeria.controller;

import com.joyeria.joyeria.model.RolUsuario;
import com.joyeria.joyeria.model.RolUsuario.NombreRol;
import com.joyeria.joyeria.service.RolService;
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

@Tag(name = "Roles", description = "Gestión de roles de usuario (ADMIN, EMPLEADO, USUARIO)")
@RestController
@RequestMapping("/api/v1/roles")
public class RolController {

    @Autowired
    private RolService rolService;

    // --- LISTAR ROLES ---
    @Operation(summary = "Listar roles disponibles", responses = {
        @ApiResponse(responseCode = "200", description = "Lista encontrada",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = RolUsuario.class),
                examples = @ExampleObject(value = """
                    [
                        {
                            "id": 1,
                            "nombreRol": "ROLE_ADMIN"
                        },
                        {
                            "id": 2,
                            "nombreRol": "ROLE_USUARIO"
                        }
                    ]
                """)
            )
        ),
        @ApiResponse(responseCode = "204", description = "No hay roles registrados", content = @Content)
    })
    @GetMapping
    public ResponseEntity<List<RolUsuario>> listar() {
        List<RolUsuario> roles = rolService.findAll();
        return roles.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(roles);
    }

    // --- CREAR ROL (POST) ---
    @Operation(summary = "Registrar rol", description = "Solo se permiten valores válidos del sistema: ROLE_ADMIN, ROLE_EMPLEADO, ROLE_USUARIO.", responses = {
        @ApiResponse(responseCode = "201", description = "Rol creado",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = RolUsuario.class),
                examples = @ExampleObject(name = "Nuevo Rol", value = """
                    {
                        "nombreRol": "ROLE_EMPLEADO"
                    }
                """)
            )
        ),
        @ApiResponse(responseCode = "400", description = "Rol inválido", content = @Content)
    })
    @PostMapping
    public ResponseEntity<RolUsuario> guardar(@RequestBody RolUsuario rol) {
        try {
            RolUsuario nuevo = rolService.save(rol);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // --- BUSCAR POR ID ---
    @Operation(summary = "Buscar rol por ID", responses = {
        @ApiResponse(responseCode = "200", description = "Encontrado",
            content = @Content(schema = @Schema(implementation = RolUsuario.class),
            examples = @ExampleObject(value = """
                {
                    "id": 1,
                    "nombreRol": "ROLE_ADMIN"
                }
            """))
        ),
        @ApiResponse(responseCode = "404", description = "No encontrado", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<RolUsuario> buscar(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(rolService.findById(id));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // --- ELIMINAR ROL ---
    @Operation(summary = "Eliminar rol por su id", responses = {
        @ApiResponse(responseCode = "204", description = "Eliminado correctamente", content = @Content),
        @ApiResponse(responseCode = "404", description = "No encontrado", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        try {
            rolService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // Opcional: Buscar por nombre (Ej: ROLE_ADMIN)
    @Operation(summary = "Buscar rol por nombre", responses = {
        @ApiResponse(responseCode = "200", description = "Encontrado",
            content = @Content(schema = @Schema(implementation = RolUsuario.class),
            examples = @ExampleObject(value = """
                {
                    "id": 1,
                    "nombreRol": "ROLE_ADMIN"
                }
            """))
        ),
        @ApiResponse(responseCode = "400", description = "Nombre de rol inválido", content = @Content),
        @ApiResponse(responseCode = "404", description = "No encontrado", content = @Content)
    })
    @GetMapping("/nombre/{nombre}")
    public ResponseEntity<RolUsuario> buscarPorNombre(@PathVariable String nombre) {
        try {
            // Convertimos el string a Enum
            NombreRol enumRol = NombreRol.valueOf(nombre.toUpperCase());
            return rolService.findByNombre(enumRol)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build(); // Si el nombre no existe en el Enum
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}