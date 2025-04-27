package com.example.ordersystem.repository;

import com.example.ordersystem.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {

    @Modifying
    @Query("UPDATE Order o SET o.orderStatus = :status WHERE o.id = :id")
    void updateStatusById(UUID id, Integer status);

}
