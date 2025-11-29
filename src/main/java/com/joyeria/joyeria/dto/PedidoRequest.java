package com.joyeria.joyeria.dto;
import lombok.Data;
import java.util.List;

@Data
public class PedidoRequest {
    private String direccionEnvio;
    private String metodoPago;
    private List<ProductoPedidoJson> productos; // Lista simple de IDs y cantidades

    @Data
    public static class ProductoPedidoJson {
        private Integer idProducto;
        private Integer cantidad;
    }
}