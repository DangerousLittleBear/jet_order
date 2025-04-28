package com.example.ordersystem.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class DBsynchronizeService {

    private final ItemService itemService;

    @Async("dbSyncExecutor")
    @Transactional
    public void synchronizeDB(String itemIdStr, Integer quantity) {
        try {
            UUID itemId = UUID.fromString(itemIdStr);
            itemService.decreaseStockAtomically(itemId, quantity);
        } catch (IllegalArgumentException e) {
            log.error("Invalid UUID string: {}", itemIdStr);
        } catch (ResponseStatusException e) {
            log.error("Error processing item: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error in synchronizeDB: {}", e.getMessage());
        }
    }
}
