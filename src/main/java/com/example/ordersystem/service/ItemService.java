package com.example.ordersystem.service;

import com.example.ordersystem.entity.Item;
import com.example.ordersystem.payload.response.ItemDTO;
import com.example.ordersystem.repository.ItemRepository;
import jakarta.annotation.security.PermitAll;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    @PermitAll
    public List<ItemDTO> findAll() {
        List<Item> items =  itemRepository.findAll();
        return items.stream()
                .map(ItemDTO::fromEntity)
                .collect(Collectors.toList());
    }


    public Map<UUID, Item> getItemsByIds(List<UUID> itemIds) {
        List<Item> items =  itemRepository.findAllById(itemIds);
        return items.stream().collect(Collectors.toMap(Item::getId, item -> item));
    }

    public Item save(Item item) {
        return itemRepository.save(item);
    }

    public Item getItem(UUID id) {
        Item item = itemRepository.findById(id).orElse(null);

        if (item == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return item;
    }


    public Boolean isPurchaseAvailable(UUID itemId , Integer quantity) {
        Item item = itemRepository.findById(itemId).orElse(null);

        if (item == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 상품이 존재하지 않습니다.");
        }

        if(item.getStock_quantity() - quantity < 0){
            return false;
        }
        return true;
    }

    public Integer getTotalPrice(UUID itemId , Integer quantity) {
        Item item = itemRepository.findById(itemId).orElse(null);

        if (item == null) {
            return 0;
        }

        return item.getPrice() * quantity;
    }

    @Transactional
    public void decreaseStock(UUID itemId, Integer quantity) {
        // findByIdWithLock 대신 findById 사용
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        item.setStock_quantity(item.getStock_quantity() - quantity);
        if(item.getStock_quantity() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수량이 모두 소진되었습니다.");
        }
        itemRepository.save(item);
    }

}
