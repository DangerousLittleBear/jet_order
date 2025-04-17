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
import java.util.Map;
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

    //Version 1. Completed
    /* Version 1 메이저 변경사항:
            1. DTO를 사용하여 JSON 직렬화 과정에서 발생하던 순환참조 문제 해결
            2. createOrder 메소드의 트랜잭션 단위를 수정하여 불필요하게 병목이 형성되지 않도록 설정
            3. 중복 데이터베이스 조회 제거 (N + 1문제 해결, 아이템 재고조회와 총액 계산을 한번에 진행.)
            4. 불필요한 중복 저장 작업 제거.
    */

    @Transactional
    public Order createOrder(UUID userID , OrderRequestDTO orderRequestData) {

        //1. 요청한 유저가 아이템을 구매할 수 있는 상황인지 확인해본다.
        Member requestMember = memberService.findMemberById(userID);

        Boolean ordererStatus =  requestMember.getActive();

        if(ordererStatus == false){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "유저가 아이템을 구매할 수 있는 상태가 아닙니다. 운영자에게 문의하세요.");
        }


        //2. Version 2. 아이템 조회 쿼리를 한번에 보내기 위해서 id를 모은다.
        List<UUID> itemIds = orderRequestData.getOrderItems().stream()
                .map(OrderRequestDTO.OrderItemDTO::getItemId)
                .collect(Collectors.toList());

        Map<UUID, Item> itemList = itemService.getItemsByIds(itemIds);

        List<OrderRequestDTO.OrderItemDTO> availableItems = orderRequestData.getOrderItems().stream()
                .filter(orderItem -> {
                    Item item = itemList.get(orderItem.getItemId());
                    return item != null && item.getStock_quantity() >= orderItem.getQuantity();
                })
                .collect(Collectors.toList());

        if (availableItems.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "주문 가능한 아이템이 없습니다 재고가 모두 소진되었습니다.");
        }

        Integer totalPrice = availableItems.stream()
                .mapToInt(orderItem -> {
                    Item item = itemList.get(orderItem.getItemId());
                    return item.getPrice() * orderItem.getQuantity();
                })
                .sum();


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

        return completedOrder;
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }
}
