package com.example.ordersystem.repository;

import com.example.ordersystem.entity.Item;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ItemRepository extends JpaRepository<Item, UUID> {

//    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT i FROM Item i WHERE i.id = :id")
    Optional<Item> findByIdWithPessimisticLock(@Param("id") UUID id);


    @Modifying
    @Query("UPDATE Item i SET i.stock_quantity = i.stock_quantity - :quantity WHERE i.id = :id AND i.stock_quantity >= :quantity")
    int decreaseStockAtomically(@Param("id") UUID id, @Param("quantity") int quantity);

}
