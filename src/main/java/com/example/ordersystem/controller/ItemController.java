package com.example.ordersystem.controller;

import com.example.ordersystem.entity.Item;
import com.example.ordersystem.payload.response.ItemDTO;
import com.example.ordersystem.service.ItemService;
import jakarta.annotation.security.PermitAll;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/item")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @GetMapping
    @PreAuthorize("permitAll()")
    public List<ItemDTO> getItems() {
        List<ItemDTO> items = itemService.findAll();
        return items;
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public Item createItem(@RequestBody Item item) {
        Item createdItem = itemService.save(item);
        return createdItem;
    }

}
