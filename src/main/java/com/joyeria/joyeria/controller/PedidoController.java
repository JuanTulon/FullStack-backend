package com.joyeria.joyeria.controller;

import com.joyeria.joyeria.model.Pedido;
import com.joyeria.joyeria.model.DetallePedido;
import com.joyeria.joyeria.service.PedidoService;
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
import org.springframework.format.annotation.DateTimeFormat;
import java.util.Date;

@Tag(name = "pedido", description = "operaciones relacionadas con los pedidos")
@RestController
@RequestMapping("/api/v1/pedidos")

public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    @Operation(summary = "Listar todos los pedidos", responses = {
            @ApiResponse(responseCode = "200", description = "Lista de pedidos obtenidos correctamente",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Pedido.class),
                    examples = @ExampleObject(value = "[{ \"id\": 1, \"direccion\": \"Calle 1\", \"fechaPedido\": \"2023-01-01\", \"estado\": \"Enviado\" }, { \"id\": 2, \"direccion\": \"Calle 2\", \"fechaPedido\": \"2023-01-02\", \"estado\": \"Pendiente\" }]")
                )
            ),
            @ApiResponse(responseCode = "204", description = "No hay pedidos registrados",
                content = @Content)
    })
    @GetMapping
    public ResponseEntity<List<Pedido>> listar() {
        List<Pedido> pedidos  = pedidoService.findAll();
        if (pedidos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(pedidos);
    }

    @Operation(summary = "Guardar un nuevo Pedido", responses = {
            @ApiResponse(responseCode = "201", description = "Pedido creado correctamente",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Pedido.class),
                    examples = @ExampleObject(value = "{ 'id': 1, 'direccion': 'Calle 1', 'fechaPedido': '2023-01-01', 'estado': 'Enviado' }")
                )
            ),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida",
                content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = "{ 'error': 'algun dato es inválido'}")
                )
            )
    })
    @PostMapping
    public ResponseEntity<Pedido> guardar(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Pedido a crear",
            required = true,
            content = @Content(schema = @Schema(implementation = Pedido.class),
                examples = @ExampleObject(value = "{ 'id': 1, 'direccion': 'Calle 1', 'fechaPedido': '2023-01-01', 'estado': 'Enviado' }" )
            )
        )
        @RequestBody Pedido pedido) {
        Pedido pedidosMuevo = pedidoService.save(pedido);
        return ResponseEntity.status(HttpStatus.CREATED).body(pedidosMuevo);
    }

    @Operation(summary = "Buscar Pedido por id", responses = {
            @ApiResponse(responseCode = "200", description = "Pedido encontrado",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Pedido.class),
                    examples = @ExampleObject(value = "{ 'id': 1, 'direccion': 'Calle 1', 'fechaPedido': '2023-01-01', 'estado': 'Enviado' }")
                )
            ),
            @ApiResponse(responseCode = "404", description = "Pedido no encontrado",
                content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = "{ 'error': 'Pedido no encontrado' }")
                )
            )
    })
    @GetMapping("/{id_pedido}")
    public ResponseEntity<EntityModel<Pedido>> buscar(
        @Parameter(description = "ID del pedido a buscar", required = true, example = "1")
        @PathVariable Integer id_pedido) {
        try {
            Pedido pedido = pedidoService.findById(id_pedido);

            EntityModel<Pedido> recurso = EntityModel.of(pedido);

            // Enlace a sí mismo
            Link selfLink = linkTo(methodOn(PedidoController.class).buscar(id_pedido)).withSelfRel();
            recurso.add(selfLink);

            // Enlace al Usuario del pedido
            if (pedido.getUsuario() != null) {
                Link UsuarioLink = linkTo(methodOn(UsuarioController.class).buscar(pedido.getUsuario().getId())).withRel("Usuario");
                recurso.add(UsuarioLink);
            }

            // Enlace al envío del pedido
            if (pedido.getEnvio() != null) {
                Link envioLink = linkTo(methodOn(EnvioController.class).buscar(pedido.getEnvio().getId_envio())).withRel("envio");
                recurso.add(envioLink);
            }

            // Enlace a los detalles del pedido
            Link detallesLink = linkTo(methodOn(PedidoController.class).listarDetallesPorPedido(id_pedido)).withRel("detalles");
            recurso.add(detallesLink);

            return ResponseEntity.ok(recurso);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Actualizar un Pedido por id", responses = {
            @ApiResponse(responseCode = "200", description = "Pedido actualizado correctamente",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Pedido.class),
                    examples = @ExampleObject(value = "{ 'id': 1, 'direccion': 'Calle 1', 'fechaPedido': '2023-01-01', 'estado': 'Enviado' }"))
            ),
            @ApiResponse(responseCode = "404", description = "Pedido no encontrado",
                content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = "{ 'error': 'Pedido no encontrado' }"))
            )
    })
    @PutMapping("/{id_pedido}")
    public ResponseEntity<Pedido> actualizar(
        @Parameter(description = "ID del pedido a actualizar", required = true, example = "1")
        @PathVariable Integer id_pedido, 
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Datos del Pedido a actualizar",
            required = true,
            content = @Content(schema = @Schema(implementation = Pedido.class),
                examples = @ExampleObject(value = "{ 'id': 1, 'direccion': 'Calle 1', 'fechaPedido': '2023-01-01', 'estado': 'Enviado' }")
            )
        )
        @RequestBody Pedido pedido) {
        try {
            Pedido ped = pedidoService.findById(id_pedido);
            ped.setFechaPedido(pedido.getFechaPedido());
            ped.setEstadoPedido(pedido.getEstadoPedido());
            ped.setDireccionEnvio(pedido.getDireccionEnvio());
            ped.setMetodoPago(pedido.getMetodoPago());
            ped.setTotalPedido(pedido.getTotalPedido());

            pedidoService.save(ped);
            return ResponseEntity.ok(ped);
        } catch ( Exception e ) {
            return  ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Eliminar un Pedido", responses = {
            @ApiResponse(responseCode = "204", description = " Pedido eliminado correctamente",
                content = @Content),
            @ApiResponse(responseCode = "404", description = " Pedido no encontrado",
                content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = "{ 'error': ' Pedido no encontrado' }"))
            )
    })
    @DeleteMapping("/{id_pedido}")
    public ResponseEntity<?> eliminar(
        @Parameter(description = "ID del pedido a eliminar", required = true, example = "1")
        @PathVariable Integer id_pedido) {
        try {
            pedidoService.delete(id_pedido);
            return ResponseEntity.noContent().build();
        } catch ( Exception e ) {
            return  ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Listar detalles de un pedido por su ID", responses = {
            @ApiResponse(responseCode = "200", description = "Lista de detalles del pedido obtenida correctamente",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = DetallePedido.class),
                    examples = @ExampleObject(value = "[{ \"id\": 1, \"producto\": \"Producto 1\", \"cantidad\": 2, \"precio\": 100 }, { \"id\": 2, \"producto\": \"Producto 2\", \"cantidad\": 1, \"precio\": 50 }]")
                )
            ),
            @ApiResponse(responseCode = "404", description = "Pedido no encontrado",
                content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = "{ 'error': 'Pedido no encontrado' }")
                )
            )
    })
    @GetMapping("/{id_pedido}/detalles")
    public ResponseEntity<List<DetallePedido>> listarDetallesPorPedido(@PathVariable Integer id_pedido) {
        try {
            Pedido pedido = pedidoService.findById(id_pedido);
            return ResponseEntity.ok(pedido.getDetalles());
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(
        summary = "Buscar pedidos por rango de fechas",
        description = "Obtiene todos los pedidos cuya fecha esté entre las fechas dadas (inclusive).",
        parameters = {
            @Parameter(name = "inicio", description = "Fecha de inicio (yyyy-MM-dd)", example = "2024-01-01"),
            @Parameter(name = "fin", description = "Fecha de fin (yyyy-MM-dd)", example = "2025-12-31")
        },
        responses = {
            @ApiResponse(responseCode = "200", description = "Lista de pedidos en el rango de fechas",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Pedido.class),
                    examples = @ExampleObject(value = "[{ 'idPedido': 1, 'fechaPedido': '2024-05-01', 'estadoPedido': 'Enviado', ... }]"))
            ),
            @ApiResponse(responseCode = "204", description = "No hay pedidos en el rango de fechas", content = @Content)
        }
    )
    @GetMapping("/fecha")
    public ResponseEntity<List<Pedido>> getPedidosPorRangoFechas(
        @RequestParam("inicio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date inicio,
        @RequestParam("fin") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date fin) {
        List<Pedido> pedidos = pedidoService.findByFechaPedidoBetween(inicio, fin);
        if (pedidos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(pedidos);
    }
}
