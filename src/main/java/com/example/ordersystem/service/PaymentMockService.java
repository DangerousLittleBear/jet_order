package com.example.ordersystem.service;

import org.springframework.stereotype.Service;

@Service
public class PaymentMockService {

    //결제하는 가상의 시나리오를 제공할 것임.
    //결제 상태는 Boolean값으로 전달. 랜덤한 시간뒤에 값을 반환한다.
    public Boolean payment(Integer totalPrice){



        return true;
    }
}
