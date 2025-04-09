package com.example.ordersystem.repository;

import com.example.ordersystem.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Long> {
}
