package com.joyeria.joyeria.controller;

import com.joyeria.joyeria.model.Envio;
import com.joyeria.joyeria.service.EnvioService;
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
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Tag(name = "envio", description = "operaciones relacionadas con los envios")
@RestController
@RequestMapping("/api/v1/envios")
public class EnvioController {

    @Autowired
    private EnvioService envioService;

    @Operation(summary = "Listar todos los envios de los pedidos", responses = {
            @ApiResponse(responseCode = "200", description = "Lista de envíos obtenidos correctamente",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Envio.class),
                    examples = @ExampleObject(value = "[{ \"id\": 1, \"direccion\": \"Calle 1\", \"fechaEnvio\": \"2023-01-01\", \"estado\": \"Enviado\" }, { \"id\": 2, \"direccion\": \"Calle 2\", \"fechaEnvio\": \"2023-01-02\", \"estado\": \"Pendiente\" }]")
                )
            ),
            @ApiResponse(responseCode = "204", description = "No hay envíos registrados",
                content = @Content)
    })
    @GetMapping
    public ResponseEntity<List<Envio>> listar() {
        List<Envio> envios = envioService.findAll();
        if (envios.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(envios);
    }

    @Operation(summary = "Guardar un nuevo envio", responses = {
            @ApiResponse(responseCode = "201", description = "envio creado correctamente",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Envio.class),
                    examples = @ExampleObject(value = "{ 'id': 1, 'direccion': 'Calle 1', 'fechaEnvio': '2023-01-01', 'estado': 'Enviado' }")
                )
            ),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida",
                content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = "{ 'error': 'algun dato es inválido'}")
                )
            )
    })
    @PostMapping
    public ResponseEntity<Envio> guardar(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "envio a crear",
            required = true,
            content = @Content(schema = @Schema(implementation = Envio.class),
                examples = @ExampleObject(value = "{ 'id': 1, 'direccion': 'Calle 1', 'fechaEnvio': '2023-01-01', 'estado': 'Enviado' }" )
            )
        )
        @RequestBody Envio envio) {
        Envio envioNuevo = envioService.save(envio);
        return ResponseEntity.status(HttpStatus.CREATED).body(envioNuevo);
    }

    @Operation(summary = "Buscar envio por id", responses = {
            @ApiResponse(responseCode = "200", description = "envio encontrado",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Envio.class),
                    examples = @ExampleObject(value = "{ 'id': 1, 'direccion': 'Calle 1', 'fechaEnvio': '2023-01-01', 'estado': 'Enviado' }")
                )
            ),
            @ApiResponse(responseCode = "404", description = "envio no encontrado",
                content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = "{ 'error': 'envio no encontrado' }")
                )
            )
    })
    @GetMapping("/{id_envio}")
    public ResponseEntity<EntityModel<Envio>> buscar(
        @Parameter(description = "id del envio a buscar", required = true, example = "1")
        @PathVariable Integer id_envio) {
        try {
            Envio envio = envioService.findById(id_envio);

            EntityModel<Envio> recurso = EntityModel.of(envio);

            // Enlace a sí mismo
            Link selfLink = linkTo(methodOn(EnvioController.class).buscar(id_envio)).withSelfRel();
            recurso.add(selfLink);

            // Enlace al pedido relacionado
            if (envio.getPedido() != null) {
                Link pedidoLink = linkTo(methodOn(PedidoController.class).buscar(envio.getPedido().getIdPedido())).withRel("pedido");
                recurso.add(pedidoLink);
            }

            return ResponseEntity.ok(recurso);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Actualizar un envio por id", responses = {
            @ApiResponse(responseCode = "200", description = "envio actualizado correctamente",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Envio.class),
                    examples = @ExampleObject(value = "{ 'id': 1, 'direccion': 'Calle 1', 'fechaEnvio': '2023-01-01', 'estado': 'Enviado' }"))
            ),
            @ApiResponse(responseCode = "404", description = "envio no encontrado",
                content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = "{ 'error': 'envio no encontrado' }"))
            )
    })
    @PutMapping("/{id_envio}")
    public ResponseEntity<Envio> actualizar(
        @Parameter(description = "ID del envío a actualizar", required = true, example = "1")
        @PathVariable Integer id_envio, 
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Datos del envío a actualizar",
            required = true,
            content = @Content(schema = @Schema(implementation = Envio.class),
                examples = @ExampleObject(value = "{ 'id': 1, 'direccion': 'Calle 1', 'fechaEnvio': '2023-01-01', 'estado': 'Enviado' }")
            )
        )
        @RequestBody Envio envio) {
        try {
            Envio env = envioService.findById(id_envio);
            env.setFecha_envio(envio.getFecha_envio());
            env.setEstado_envio(envio.getEstado_envio());
            env.setPedido(envio.getPedido());
            envioService.save(env);
            return ResponseEntity.ok(env);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Eliminar un envío", responses = {
            @ApiResponse(responseCode = "204", description = " envío eliminado correctamente",
                content = @Content),
            @ApiResponse(responseCode = "404", description = " envío no encontrado",
                content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = "{ 'error': ' envío no encontrado' }"))
            )
    })
    @DeleteMapping("/{id_envio}")
    public ResponseEntity<?> eliminar(
        @Parameter(description = "ID del envío a eliminar", required = true, example = "1")
        @PathVariable Integer id_envio) {
        try {
            envioService.delete(id_envio);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
