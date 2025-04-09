package com.example.ordersystem.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Data
@Entity
@Table(name = "items")
@NoArgsConstructor
@AllArgsConstructor
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column()
    private String description;

    @Column(nullable = false)
    private int price;

    @Column(nullable = false)
    private int stock_quantity;

    @OneToMany(mappedBy = "item")
    private List<OrderItem> orders = new ArrayList<>();


    // 엔티티 메소드들
    public void changeName(String name) {
        this.name = name;
    }

    public void changeDescription(String description) {
        this.description = description;
    }

    public void decreaseStockQuantity(int quantity) {
        this.stock_quantity -= quantity;
    }

    public void increaseStockQuantity(int quantity) {
        this.stock_quantity += quantity;
    }

    // 재고량은 일시적으로 새롭게 서버를 시작할때마다 초기화.
}
