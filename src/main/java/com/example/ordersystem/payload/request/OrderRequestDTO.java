package com.example.ordersystem.payload.request;

import java.util.List;
import java.util.UUID;

public class OrderRequestDTO {

    private List<OrderItemDTO> orderItems;

    public static class OrderItemDTO {
        private UUID itemId;
        private int quantity;
    }
}
