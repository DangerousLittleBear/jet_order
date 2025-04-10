package com.example.ordersystem.service;

import com.example.ordersystem.entity.Member;
import com.example.ordersystem.entity.Order;
import com.example.ordersystem.entity.OrderItem;
import com.example.ordersystem.payload.request.OrderRequestDTO;
import com.example.ordersystem.repository.MemberRepository;
import com.example.ordersystem.repository.OrderItemRepository;
import com.example.ordersystem.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final MemberService memberService;
    private final OrderItemService orderItemService;
    private final ItemService itemService;
    private final PaymentMockService paymentMockService;

    @Transactional
    public Order createOrder(UUID userID , OrderRequestDTO orderRequestData) {

        //1. 요청한 유저가 아이템을 구매할 수 있는 상황인지 확인해본다.
        Boolean ordererStatus = memberService.isMemberValid(userID);

        if(ordererStatus == false){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "유저가 아이템을 구매할 수 있는 상태가 아닙니다. 운영자에게 문의하세요.");
        }


        //2. 해당 아이템의 재고가 남아있는지 확인한다.
        List<OrderRequestDTO.OrderItemDTO> availableItems = orderRequestData.getOrderItems().stream()
                .filter(orderItem ->
                        itemService.isPurchaseAvailable(orderItem.getItemId() ,orderItem.getQuantity()))
                        .collect(Collectors.toList());

        if (availableItems.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "주문 가능한 아이템이 없습니다 재고가 모두 소진되었습니다.");
        }

        //물품 ID와 수량을 통해서 총액을 계산한다.
        Integer totalPrice = availableItems.stream()
                .map(orderItem -> itemService.getTotalPrice(orderItem.getItemId(), orderItem.getQuantity()))
                .collect(Collectors.summingInt(Integer::intValue));

        //3. 재고가 남아있다면 결제를 진행한다. (결제시스템)
        try {
            paymentMockService.payment(totalPrice);
        }
        catch (Ex)


        //4. 완료되면 고객에게 해당 요청에 대한 응답을 반환한다.

        return orderRepository.save(orderRequest);
    }

}
