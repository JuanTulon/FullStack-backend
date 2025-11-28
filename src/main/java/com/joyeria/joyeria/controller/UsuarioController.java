package com.joyeria.joyeria.controller;

import com.joyeria.joyeria.model.Usuario;
import com.joyeria.joyeria.model.Pedido;
import com.joyeria.joyeria.service.UsuarioService;
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
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import java.util.List;
import java.util.Optional;

@Tag(name = "Usuarios", description = "operaciones relacionadas con el Usuario")
@RestController
@RequestMapping("/api/v1/Usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Operation(summary = "Listar todos los usuarios", responses = {
            @ApiResponse(responseCode = "200", description = "Lista de usuarios obtenidos correctamente",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(type = "array", implementation = Usuario.class),
                    examples = @ExampleObject(value = "[{ \"id\": 1, \"run\": \"12345678\", \"dv\": \"9\", \"fechaNacimiento\": \"1990-01-01\", \"nombre\": \"Juan\", \"apellido1\": \"Pérez\", \"apellido2\": \"González\", \"email\": \"juan.perez@gmail.com\", \"telefono\": 987654321 }]"))
            ),
            @ApiResponse(responseCode = "204", description = "No hay usuarios registrados",
                content = @Content)
    })
    @GetMapping
    public ResponseEntity<List<Usuario>> listar() {
        List<Usuario> usuarios = usuarioService.listarUsuarios();
        if (usuarios.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(usuarios);
    }

    @Operation(summary = "Guardar un nuevo usuario", responses = {
            @ApiResponse(responseCode = "201", description = "Usuario creado correctamente",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Usuario.class),
                    examples = @ExampleObject(value = "{ 'id': 2, 'run': '23456789', 'dv': 'K', 'fechaNacimiento': '1985-05-10', 'nombre': 'Ana', 'apellido1': 'López', 'apellido2': 'Martínez', 'email': 'ana.lopez@gmail.com', 'telefono': 912345678 }")
                )
            ),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida",
                content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = "{ 'error': 'RUN inválido o datos incompletos' }")
                )
            )
    })
    @PostMapping(consumes = {"application/json", "application/json;charset=UTF-8", "application/*+json"})
    public ResponseEntity<Usuario> guardar(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Usuario a crear",
            required = true,
            content = @Content(schema = @Schema(implementation = Usuario.class),
                examples = @ExampleObject(value = "{ 'run': '23456789', 'dv': 'K', 'fechaNacimiento': '1985-05-10', 'nombre': 'Ana', 'apellido1': 'López', 'apellido2': 'Martínez', 'email': 'ana.lopez@gmail.com', 'telefono': 912345678 }")
            )
        )
        @RequestBody Usuario usuario) {
        Usuario usuarioNuevo = usuarioService.registrarUsuario(usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioNuevo);
    }

    @Operation(summary = "Buscar usuario por id", responses = {
            @ApiResponse(responseCode = "200", description = "Usuario encontrado",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Usuario.class),
                    examples = @ExampleObject(value = "{ 'id': 1, 'run': '12345678', 'dv': '9', 'fechaNacimiento': '1990-01-01', 'nombre': 'Juan', 'apellido1': 'Pérez', 'apellido2': 'González', 'email': 'juan.perez@gmail.com', 'telefono': 987654321 }")
                )
            ),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado",
                content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = "{ 'error': 'Usuario no encontrado' }"))
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Usuario>> buscar(
        @Parameter(description = "ID del usuario a buscar", required = true, example = "1")
        @PathVariable Integer id) {
        try {
            Usuario usuario = usuarioService.findById(id);
            EntityModel<Usuario> recurso = EntityModel.of(usuario);

            // Enlace a sí mismo
            Link selfLink = linkTo(methodOn(UsuarioController.class).buscar(id)).withSelfRel();
            recurso.add(selfLink);

            // Enlace a los pedidos de este usuario
            Link pedidosLink = linkTo(methodOn(UsuarioController.class).listarPedidosPorUsuario(id)).withRel("pedidos");
            recurso.add(pedidosLink);

            return ResponseEntity.ok(recurso);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Agrega este método si no existe, para que el link funcione:
    @GetMapping("/{id}/pedidos")
    public ResponseEntity<List<Pedido>> listarPedidosPorUsuario(@PathVariable Integer id) {
        try {
            Usuario Usuario = UsuarioService.findById(id);
            return ResponseEntity.ok(Usuario.getPedidos());
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Buscar Usuario por primer apellido", responses = {
            @ApiResponse(responseCode = "200", description = "Usuarios encontrados",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(type = "array", implementation = Usuario.class),
                    examples = @ExampleObject(value = "[{ \"id\": 1, \"run\": \"12345678\", \"dv\": \"9\", \"fechaNacimiento\": \"1990-01-01\", \"nombre\": \"Juan\", \"apellido1\": \"Pérez\", \"apellido2\": \"González\", \"gmail\": \"juan.perez@gmail.com\", \"telefono\": 987654321 }]"))
            ),
            @ApiResponse(responseCode = "404", description = "No se encontraron Usuarios con ese apellido",
                content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = "{ \"error\": \"No se encontraron Usuarios con ese apellido\" }"))
            )
    })
    @GetMapping("/apellido1/{ape1}")
    public ResponseEntity<List<Usuario>> buscarApellido1(
        @Parameter(description = "primer apellido del Usuario a buscar", required = true, example = "perez")
        @PathVariable String ape1) {
        try {
            List<Usuario> Usuarios = UsuarioService.findByApellido1(ape1);
            return ResponseEntity.ok(Usuarios);
        } catch ( Exception e ) {
            return  ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Buscar Usuario por segundo apellido", responses = {
            @ApiResponse(responseCode = "200", description = "Usuarios encontrados",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(type = "array", implementation = Usuario.class),
                    examples = @ExampleObject(value = "[{ \"id\": 3, \"run\": \"34567890\", \"dv\": \"2\", \"fechaNacimiento\": \"1992-03-15\", \"nombre\": \"Pedro\", \"apellido1\": \"Soto\", \"apellido2\": \"Pérez\", \"gmail\": \"pedro.soto@gmail.com\", \"telefono\": 912345679 }]"))
            ),
            @ApiResponse(responseCode = "404", description = "No se encontraron Usuarios con ese apellido",
                content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = "{ \"error\": \"No se encontraron Usuarios con ese apellido\" }"))
            )
    })
    @GetMapping("/apellido2/{ape2}")
    public ResponseEntity<List<Usuario>> buscarApellido2(
        @Parameter(description = "segundo apellido del Usuario a buscar", required = true, example = "perez")
        @PathVariable String ape2) {
        try {
            List<Usuario> Usuarios = UsuarioService.findByApellido2(ape2);
            return ResponseEntity.ok(Usuarios);
        } catch ( Exception e ) {
            return  ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Buscar Usuario por correo", responses = {
            @ApiResponse(responseCode = "200", description = "Usuarios encontrados",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(type = "array", implementation = Usuario.class),
                    examples = @ExampleObject(value = "[{ \"id\": 4, \"run\": \"45678901\", \"dv\": \"3\", \"fechaNacimiento\": \"1995-07-20\", \"nombre\": \"Lucía\", \"apellido1\": \"Gómez\", \"apellido2\": \"Rojas\", \"gmail\": \"lucia.gomez@gmail.com\", \"telefono\": 912345680 }]"))
            ),
            @ApiResponse(responseCode = "404", description = "No se encontraron Usuarios con ese correo",
                content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = "{ \"error\": \"No se encontraron Usuarios con ese correo\" }"))
            )
    })
    @GetMapping("/correo/{gmail}")
    public ResponseEntity<List<Usuario>> buscarCorreo(
        @Parameter(description = "Correo del Usuario a buscar", required = true, example = "lucia.gomez@gmail.com")
        @PathVariable String gmail) {
        try {
            List<Usuario> Usuarios = UsuarioService.findByCorreo(gmail);
            return ResponseEntity.ok(Usuarios);
        } catch ( Exception e ) {
            return  ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Buscar Usuario por RUT", responses = {
            @ApiResponse(responseCode = "200", description = "Usuarios encontrados",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(type = "array", implementation = Usuario.class),
                    examples = @ExampleObject(value = "[{ \"id\": 5, \"run\": \"21245222\", \"dv\": \"1\", \"fechaNacimiento\": \"1988-11-30\", \"nombre\": \"Carlos\", \"apellido1\": \"Muñoz\", \"apellido2\": \"Vega\", \"gmail\": \"carlos.munoz@gmail.com\", \"telefono\": 912345681 }]"))
            ),
            @ApiResponse(responseCode = "404", description = "No se encontraron Usuarios con ese rut",
                content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = "{ \"error\": \"No se encontraron Usuarios con ese rut\" }"))
            )
    })
    @GetMapping("/rut/{rut}")
    public ResponseEntity<List<Usuario>> buscarPorRut(
        @Parameter(description = "RUT del Usuario a buscar (formato 12345678-1)", required = true, example = "21245222-1")
        @PathVariable String rut) {
        try {
            List<Usuario> Usuarios = UsuarioService.findByRut(rut);
            return ResponseEntity.ok(Usuarios);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Actualizar un Usuario", responses = {
            @ApiResponse(responseCode = "200", description = "Usuario actualizado correctamente",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Usuario.class),
                    examples = @ExampleObject(value = "{ 'id': 1, 'run': '12345678', 'dv': '9', 'fechaNacimiento': '1990-01-01', 'nombre': 'Juan', 'apellido1': 'Pérez', 'apellido2': 'González', 'gmail': 'juan.perez@gmail.com', 'telefono': 987654321 }"))
            ),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado",
                content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = "{ 'error': 'Usuario no encontrado' }"))
            )
    })
    @PutMapping(value = "/{id}", consumes = {"application/json", "application/*+json"})
    public ResponseEntity<Usuario> actualizar(
        @Parameter(description = "ID del Usuario a actualizar", required = true, example = "1")
        @PathVariable Integer id,
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Datos del Usuario a actualizar",
            required = true,
            content = @Content(schema = @Schema(implementation = Usuario.class),
                examples = @ExampleObject(value = "{ 'run': '12345678', 'dv': '9', 'fechaNacimiento': '1990-01-01', 'nombre': 'Juan', 'apellido1': 'Pérez', 'apellido2': 'González', 'gmail': 'juan.perez@gmail.com', 'telefono': 987654321 }")
            )
        )
        @RequestBody Usuario Usuario) {
        try {
            Usuario cli = UsuarioService.findById(id);
            cli.setId(id);
            cli.setRun(Usuario.getRun());
            cli.setDv(Usuario.getDv());
            cli.setNombre(Usuario.getNombre());
            cli.setApellido1(Usuario.getApellido1());
            cli.setApellido2(Usuario.getApellido2());
            cli.setFechaNacimiento(Usuario.getFechaNacimiento());
            cli.setGmail(Usuario.getGmail());
            cli.setTelefono(Usuario.getTelefono());
            cli.setHistorialPedidos(Usuario.getHistorialPedidos());

            UsuarioService.save(cli);
            return ResponseEntity.ok(cli);
        } catch ( Exception e ) {
            return  ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Eliminar un Usuario", responses = {
            @ApiResponse(responseCode = "204", description = "Usuario eliminado correctamente",
                content = @Content),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado",
                content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = "{ 'error': 'Usuario no encontrado' }"))
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(
        @Parameter(description = "ID del Usuario a eliminar", required = true, example = "1")
        @PathVariable Integer id) {
        try {
            UsuarioService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch ( Exception e ) {
            return  ResponseEntity.notFound().build();
        }
    }
}

