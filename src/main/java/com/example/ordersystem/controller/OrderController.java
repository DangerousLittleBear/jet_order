package com.example.ordersystem.controller;

import com.example.ordersystem.entity.Order;
import com.example.ordersystem.payload.request.OrderRequestDTO;
import com.example.ordersystem.sercurity.UserDetailsImpl;
import com.example.ordersystem.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public Order createOrder(@RequestBody OrderRequestDTO orderRequest) {

        // 유저가 로그인되어있는 상태에서만 물품 구매 가능.
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        //유저 디테일 정보와 함께 리퀘스트를 넘김.
        Order completedOrder =  orderService.createOrder(userDetails.getId() ,orderRequest);

        return completedOrder;
    }

    @GetMapping
    public List<Order> getAllOrders() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        List<Order> allOrders = orderService.getAllOrders();
        return allOrders;
    }

}
