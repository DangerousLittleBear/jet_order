package com.example.ordersystem.payload.response;

import com.example.ordersystem.entity.Item;
import lombok.Data;

import java.util.UUID;

@Data
public class ItemDTO {
    private UUID id;
    private String name;
    private String description;
    private int price;
    private int quantity;

    public static ItemDTO fromEntity(Item item) {
        ItemDTO dto = new ItemDTO();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setPrice(item.getPrice());
        dto.setQuantity(item.getStock_quantity());
        return dto;
    }
}
