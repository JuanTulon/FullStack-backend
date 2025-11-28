package com.joyeria.joyeria.controller;

import com.joyeria.joyeria.model.Pedido;
import com.joyeria.joyeria.model.Usuario;
import com.joyeria.joyeria.service.UsuarioService;
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

@Tag(name = "Usuarios", description = "Gestión de usuarios del sistema")
@RestController
@RequestMapping("/api/v1/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    // --- LISTAR USUARIOS ---
    @Operation(summary = "Listar todos los usuarios", responses = {
        @ApiResponse(responseCode = "200", description = "Lista encontrada",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = Usuario.class),
                examples = @ExampleObject(value = """
                    [
                        {
                            "id": 1,
                            "run": "11111111",
                            "dv": "1",
                            "nombre": "Juan",
                            "apellido1": "Pérez",
                            "apellido2": "González",
                            "email": "juan.perez@example.com",
                            "telefono": 987654321,
                            "fechaNacimiento": "1990-05-15"
                        }
                    ]
                """)
            )
        ),
        @ApiResponse(responseCode = "204", description = "No hay usuarios", content = @Content)
    })
    @GetMapping
    public ResponseEntity<List<Usuario>> listar() {
        List<Usuario> usuarios = usuarioService.listarUsuarios();
        return usuarios.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(usuarios);
    }

    // --- CREAR USUARIO (POST) ---
    @Operation(summary = "Registrar nuevo usuario", description = "Crea un usuario con rol por defecto. El formato de fecha es yyyy-MM-dd.", responses = {
        @ApiResponse(responseCode = "201", description = "Usuario creado",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = Usuario.class),
                examples = @ExampleObject(name = "Usuario Nuevo", value = """
                    {
                        "run": "12345678",
                        "dv": "5",
                        "nombre": "Maria",
                        "apellido1": "López",
                        "apellido2": "Torres",
                        "fechaNacimiento": "1995-10-20",
                        "email": "maria.lopez@example.com",
                        "telefono": 912345678,
                        "password": "PasswordSegura123"
                    }
                """)
            )
        ),
        @ApiResponse(responseCode = "400", description = "Datos inválidos (RUT erróneo o email duplicado)", content = @Content)
    })
    @PostMapping
    public ResponseEntity<Usuario> guardar(@RequestBody Usuario usuario) {
        try {
            Usuario nuevo = usuarioService.registrarUsuario(usuario);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
        } catch (Exception e) {
            // Retorna un Bad Request si falla el RUT o el mail ya existe
            return ResponseEntity.badRequest().build();
        }
    }

    // --- BUSCAR POR ID ---
    @Operation(summary = "Buscar por ID", responses = {
        @ApiResponse(responseCode = "200", description = "Encontrado", content = @Content(schema = @Schema(implementation = Usuario.class))),
        @ApiResponse(responseCode = "404", description = "No encontrado", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<Usuario> buscar(@PathVariable Integer id) {
        return usuarioService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // --- BUSCAR POR RUT ---
    @Operation(summary = "Buscar por RUT completo", parameters = {
        @Parameter(name = "rut", description = "RUT con guion (ej: 12345678-5)", required = true, example = "12345678-5")
    })
    @GetMapping("/rut/{rut}")
    public ResponseEntity<List<Usuario>> buscarPorRut(@PathVariable String rut) {
        try {
            List<Usuario> usuarios = usuarioService.buscarPorRut(rut);
            return usuarios.isEmpty() ? ResponseEntity.notFound().build() : ResponseEntity.ok(usuarios);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // --- ACTUALIZAR USUARIO (PUT) ---
    @Operation(summary = "Actualizar datos", description = "Actualiza solo nombre, apellidos, teléfono y email. No actualiza RUT ni contraseña.", responses = {
        @ApiResponse(responseCode = "200", description = "Actualizado correctamente",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = Usuario.class),
                examples = @ExampleObject(name = "Datos a Actualizar", value = """
                    {
                        "nombre": "Maria Alejandra",
                        "apellido1": "López",
                        "apellido2": "Torres",
                        "email": "maria.nueva@example.com",
                        "telefono": 555666777
                    }
                """)
            )
        ),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<Usuario> actualizar(@PathVariable Integer id, @RequestBody Usuario usuario) {
        try {
            return ResponseEntity.ok(usuarioService.actualizarUsuario(id, usuario));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // --- ELIMINAR USUARIO ---
    @Operation(summary = "Eliminar usuario por id", responses = {
        @ApiResponse(responseCode = "204", description = "Usuario eliminado", content = @Content),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        try {
            usuarioService.eliminarUsuario(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // --- LISTAR PEDIDOS DE UN USUARIO ---
    @Operation(summary = "Ver pedidos del usuario por su id", responses = {
        @ApiResponse(responseCode = "200", description = "Pedidos encontrados", content = @Content(schema = @Schema(implementation = Pedido.class))),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado", content = @Content)
    })
    @GetMapping("/{id}/pedidos")
    public ResponseEntity<List<Pedido>> listarPedidosPorUsuario(@PathVariable Integer id) {
        return usuarioService.buscarPorId(id)
                .map(u -> ResponseEntity.ok(u.getPedidos()))
                .orElse(ResponseEntity.notFound().build());
    }
}