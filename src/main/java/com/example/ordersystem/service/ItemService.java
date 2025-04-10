package com.example.ordersystem.service;

import com.example.ordersystem.entity.Item;
import com.example.ordersystem.repository.ItemRepository;
import jakarta.annotation.security.PermitAll;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    @PermitAll
    public List<Item> findAll() {
        return itemRepository.findAll();
    }


    public Item save(Item item) {
        return itemRepository.save(item);
    }

}
