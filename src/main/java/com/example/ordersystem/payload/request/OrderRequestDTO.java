package com.example.ordersystem.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
public class OrderRequestDTO {

    private List<OrderItemDTO> orderItems;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemDTO {
        private UUID itemId;
        private int quantity;
    }
}
