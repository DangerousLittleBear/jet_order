package com.example.ordersystem.payload.response;

import com.example.ordersystem.entity.Order;
import com.example.ordersystem.entity.OrderItem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

// OrderResponseDTO.java
@Data
public class OrderResponseDTO {
    private UUID id;
    private UUID memberId;
    private String memberEmail;
    private LocalDateTime orderTime;
    private Integer orderStatus;
    private Integer totalPrice;
    private List<OrderItemDTO> orderItems;

    public static OrderResponseDTO fromEntity(Order order) {
        OrderResponseDTO dto = new OrderResponseDTO();
        dto.setId(order.getId());
        dto.setMemberId(order.getMember().getId());
        dto.setMemberEmail(order.getMember().getEmail());
        dto.setOrderTime(order.getOrderTime());
        dto.setOrderStatus(order.getOrderStatus());
        dto.setTotalPrice(order.getTotalPrice());
        
        // OrderItem 변환
        dto.setOrderItems(order.getOrderItems().stream()
            .map(OrderItemDTO::fromEntity)
            .collect(Collectors.toList()));
        
        return dto;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemDTO {
        private UUID id;
        private UUID itemId;
        private String itemName;
        private int price;
        private int quantity;
        
        public static OrderItemDTO fromEntity(OrderItem orderItem) {
            OrderItemDTO dto = new OrderItemDTO();
            dto.setId(orderItem.getId());
            dto.setItemId(orderItem.getItem().getId());
            dto.setItemName(orderItem.getItem().getName());
            dto.setPrice(orderItem.getItem().getPrice());
            dto.setQuantity(orderItem.getQuantity());
            return dto;
        }
    }
}