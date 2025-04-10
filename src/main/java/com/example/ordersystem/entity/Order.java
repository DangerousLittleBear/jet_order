package com.example.ordersystem.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@Table(name = "orders")
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id" , nullable = false)
    private Member member;

    @Column(nullable = false)
    private LocalDateTime orderTime;

    @Column(nullable = false)
    //1은 정상, 0은 취소
    private Integer orderStatus;

    @Column(nullable = false)
    private Integer totalPrice;

    @OneToMany(mappedBy = "order" , cascade = CascadeType.ALL)
    private List<OrderItem> orderItems = new ArrayList<>();

    public void changeOrderStatus(LocalDateTime orderChangedTime) {
        if(this.orderStatus == 0){
            this.orderStatus = 1;
        }
        else if(this.orderStatus == 1){
            this.orderStatus = 0;
        }
        this.orderTime = orderChangedTime;
    }
}
