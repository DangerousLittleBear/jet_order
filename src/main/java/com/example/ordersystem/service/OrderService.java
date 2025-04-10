package com.example.ordersystem.service;

import com.example.ordersystem.entity.Item;
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
        Member requestMember = memberService.findMemberById(userID);

        Boolean ordererStatus =  requestMember.getActive();

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
        try{
            Boolean paymentStatus = paymentMockService.payment(totalPrice);

            if(paymentStatus == false){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "결제 실패.");
            }
        }
        catch (Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "결제 중 오류가 발생하였습니다.");
        }

        //4. 결제가 완료되면 해당 주문건을 데이터베이스에 등록하고 아이템의 수량을 업데이트한다.


        Order order = new Order();
        order.setOrderTime(LocalDateTime.now());
        order.setMember(requestMember);
        order.setTotalPrice(totalPrice);
        order.setOrderStatus(1);

        List<OrderItem> orderItems = availableItems.stream()
                .map(dto -> {
                    OrderItem orderItem = new OrderItem();

                    Item item = itemService.getItem(dto.getItemId());
                    itemService.decreaseStock(dto.getItemId(), dto.getQuantity());

                    orderItem.setItem(item);
                    orderItem.setQuantity(dto.getQuantity());
                    orderItem.setOrder(order);
                    return orderItem;
                }).collect(Collectors.toList());


        order.setOrderItems(orderItems);

        Order completedOrder = orderRepository.save(order);

        //5. 완료되면 고객에게 해당 요청에 대한 응답을 반환한다.

        return orderRepository.save(completedOrder);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }
}
