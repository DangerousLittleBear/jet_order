package com.example.ordersystem.controller;

import com.example.ordersystem.entity.Order;
import com.example.ordersystem.payload.request.OrderRequestDTO;
import com.example.ordersystem.payload.response.OrderResponseDTO;
import com.example.ordersystem.sercurity.UserDetailsImpl;
import com.example.ordersystem.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public OrderResponseDTO createOrder(@RequestBody OrderRequestDTO orderRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        
        Order completedOrder = orderService.createOrder(userDetails.getId(), orderRequest);
        return OrderResponseDTO.fromEntity(completedOrder);
    }

    @GetMapping
    public List<OrderResponseDTO> getAllOrders() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        List<Order> allOrders = orderService.getAllOrders();
        return allOrders.stream()
                .map(OrderResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

}
