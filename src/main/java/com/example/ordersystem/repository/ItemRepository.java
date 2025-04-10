package com.example.ordersystem.repository;

import com.example.ordersystem.entity.Item;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ItemRepository extends JpaRepository<Item, UUID> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Item findByIdWithLock(UUID id);
}
