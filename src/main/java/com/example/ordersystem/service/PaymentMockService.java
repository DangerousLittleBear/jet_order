package com.example.ordersystem.service;

import com.example.ordersystem.entity.Order;
import com.example.ordersystem.payload.request.OrderRequestDTO;
import com.example.ordersystem.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class PaymentMockService {
    private final Random random = new Random();
    private final RedisService redisService;

    //결제 상태는 Boolean값으로 전달. 랜덤한 시간뒤에 값을 반환한다.
    public Boolean payment(Integer totalPrice) {
        try {
            // 랜덤한 지연 시간 (1초 ~ 5초)
            int delaySeconds = random.nextInt(4) + 1;
            TimeUnit.SECONDS.sleep(delaySeconds);
            
            // 모든 결제는 성공
            return true;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("결제 처리 중 오류가 발생했습니다.", e);
        }
    }

    private final OrderRepository orderRepository;

    // 결제 처리를 비동기적으로 수행하는 것으로 변경.
    @Async("paymentExecutor")
    public void paymentAsync(Integer totalPrice , List<OrderRequestDTO.OrderItemDTO> purchaseItem, Order order) {
        int delaySeconds = random.nextInt(4) + 1;

        try {
            TimeUnit.SECONDS.sleep(delaySeconds);

        } catch (Exception e) {
            //실패시 보상 트랜잭션 수행 Exception 처리 된 것이 결제 처리 실패라고 가정한다.
            purchaseItem.forEach(orderRequestDTO -> {
                String itemID = orderRequestDTO.getItemId().toString();
                Integer quantity = orderRequestDTO.getQuantity();
                //보상 트랜잭션 수행.
                redisService.incrementStockQuantityInRedis(itemID, quantity);
                //주문 상태를 취소로 변경한다.
                orderRepository.updateStatusById(order.getId() ,0);
            });
        }
    }


}
