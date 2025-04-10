package com.example.ordersystem.service;

import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class PaymentMockService {
    private final Random random = new Random();

    //결제하는 가상의 시나리오를 제공할 것임.
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
}
