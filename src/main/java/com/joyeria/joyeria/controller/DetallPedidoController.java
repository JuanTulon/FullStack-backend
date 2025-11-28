package com.joyeria.joyeria.controller;

import com.joyeria.joyeria.model.DetallePedido;
import com.joyeria.joyeria.service.DetallPedidoService;

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

@Tag(name = "detalle pedido", description = "operaciones relacionadas con los detalles de pedidos")
@RestController
@RequestMapping("/api/v1/detallepedidos")
public class DetallPedidoController {
    @Autowired 
    private DetallPedidoService detallPedidoService;

    @Operation(summary = "Listar todos los detalles de los pedidos", responses = {
            @ApiResponse(responseCode = "200", description = "Lista de detalles de pedidos obtenida correctamente",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = DetallePedido.class),
                    examples = @ExampleObject(value = "[{ \"id\": 1, \"producto\": \"Producto 1\", \"cantidad\": 2, \"precio\": 100.0 }, { \"id\": 2, \"producto\": \"Producto 2\", \"cantidad\": 1, \"precio\": 50.0 }]")
                )
            ),
            @ApiResponse(responseCode = "204", description = "No hay detalles de pedidos registrados",
                content = @Content)
    })
    @GetMapping
    public ResponseEntity<List<DetallePedido>> listar() {
        List<DetallePedido> detpedido  = detallPedidoService.findAll();
        if (detpedido.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(detpedido);
    }

    @Operation(summary = "Guardar un nuevo detalle de pedido", responses = {
            @ApiResponse(responseCode = "201", description = "detalle de pedido creado correctamente",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = DetallePedido.class),
                    examples = @ExampleObject(value = "{ 'id': 1, 'producto': 'Producto 1', 'cantidad': 2, 'precio': 100.0 }")
                )
            ),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida",
                content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = "{ 'error': 'algun dato es inválido'}")
                )
            )
    })
    @PostMapping
    public ResponseEntity<DetallePedido> guardar(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "detalle de pedido a crear",
            required = true,
            content = @Content(schema = @Schema(implementation = DetallePedido.class),
                examples = @ExampleObject(value = "{ 'id': 1, 'producto': 'Producto 1', 'cantidad': 2, 'precio': 100.0 }" )
            )
        )
        @RequestBody DetallePedido detpedido) {
        DetallePedido detPedidoNuevo = detallPedidoService.save(detpedido);
        return ResponseEntity.status(HttpStatus.CREATED).body(detPedidoNuevo);
    }

    @Operation(summary = "Buscar detalle de pedido por id", responses = {
            @ApiResponse(responseCode = "200", description = "detalle de pedido encontrado",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = DetallePedido.class),
                    examples = @ExampleObject(value = "{ 'id': 1, 'producto': 'Producto 1', 'cantidad': 2, 'precio': 100.0 }")
                )
            ),
            @ApiResponse(responseCode = "404", description = "detalle de pedido no encontrado",
                content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = "{ 'error': 'detalle de pedido no encontrado' }")
                )
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<DetallePedido>> buscar(
        @Parameter(description = "ID del detalle de pedido a buscar", required = true, example = "1")
        @PathVariable Integer id) {
        try {
            DetallePedido detPedido = detallPedidoService.findById(id);

            EntityModel<DetallePedido> recurso = EntityModel.of(detPedido);

            // Enlace a sí mismo
            Link selfLink = linkTo(methodOn(DetallPedidoController.class).buscar(id)).withSelfRel();
            recurso.add(selfLink);

            // Enlace al pedido relacionado
            if (detPedido.getPedido() != null) {
                Link pedidoLink = linkTo(methodOn(PedidoController.class).buscar(detPedido.getPedido().getIdPedido())).withRel("pedido");
                recurso.add(pedidoLink);
            }

            // Enlace al producto relacionado
            if (detPedido.getProducto() != null) {
                Link productoLink = linkTo(methodOn(ProductoController.class).buscar(detPedido.getProducto().getIdProducto())).withRel("producto");
                recurso.add(productoLink);
            }

            return ResponseEntity.ok(recurso);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Actualizar un detalle de pedido", responses = {
            @ApiResponse(responseCode = "200", description = "detalle de pedido actualizado correctamente",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = DetallePedido.class),
                    examples = @ExampleObject(value = "{ 'id': 1, 'producto': 'Producto 1', 'cantidad': 2, 'precio': 100.0 }"))
            ),
            @ApiResponse(responseCode = "404", description = "detalle de pedido no encontrado",
                content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = "{ 'error': 'detalle de pedido no encontrado' }"))
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<DetallePedido> actualizar(
        @Parameter(description = "ID del detalle de pedido a actualizar", required = true, example = "1")
        @PathVariable Integer id, 
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Datos del detalle de pedido a actualizar",
            required = true,
            content = @Content(schema = @Schema(implementation = DetallePedido.class),
                examples = @ExampleObject(value = "{ 'id': 1, 'producto': 'Producto 1', 'cantidad': 2, 'precio': 100.0 }")
            )
        )
        @RequestBody DetallePedido detallePedido) {
        try {
            DetallePedido det = detallPedidoService.findById(id);
            det.setId(id);
            det.setCantidadProducto(detallePedido.getCantidadProducto());
            det.setSubtotal(detallePedido.getSubtotal());

            detallPedidoService.save(det);
            return ResponseEntity.ok(det);
        } catch ( Exception e ) {
            return  ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Eliminar un detalle de pedido", responses = {
            @ApiResponse(responseCode = "204", description = " detalle de pedido eliminado correctamente",
                content = @Content),
            @ApiResponse(responseCode = "404", description = " detalle de pedido no encontrado",
                content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = "{ 'error': ' detalle de pedido no encontrado' }"))
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(
        @Parameter(description = "ID del detalle de pedido a eliminar", required = true, example = "1")
        @PathVariable Integer id) {
        try {
            detallPedidoService.delete(id);
            return ResponseEntity.noContent().build();
        } catch ( Exception e ) {
            return  ResponseEntity.notFound().build();
        }
    }
}
