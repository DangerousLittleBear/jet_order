package com.example.ordersystem.service;

import com.example.ordersystem.entity.Item;
import com.example.ordersystem.entity.Member;
import com.example.ordersystem.entity.Order;
import com.example.ordersystem.entity.OrderItem;
import com.example.ordersystem.payload.request.OrderRequestDTO;
import com.example.ordersystem.repository.ItemRepository;
import com.example.ordersystem.repository.MemberRepository;
import com.example.ordersystem.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ItemService itemService;
    private final MemberRepository memberRepository;
    private final PaymentMockService paymentMockService;
    private final ItemRepository itemRepository;

    //Version 2. Working...
    /* Version 2  메이저 변경사항:
            1. 서비스 레이어에서 서비스 레이어를 직접 가져다쓰는 코드를 변경한다.
    */
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Order createOrder(UUID userID , OrderRequestDTO orderRequestData) {

        //1. 요청한 유저가 아이템을 구매할 수 있는 상황인지 확인해본다.
        Member requestMember = memberRepository.findById(userID)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "해당 유저가 존재하지 않습니다."));

        Boolean ordererStatus =  requestMember.getActive();

        if(ordererStatus == false){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "유저가 아이템을 구매할 수 있는 상태가 아닙니다. 운영자에게 문의하세요.");
        }


        //2. Version 2. 아이템 조회 쿼리를 한번에 보내기 위해서 id를 모은다.
        List<UUID> itemIds = orderRequestData.getOrderItems().stream()
                .map(OrderRequestDTO.OrderItemDTO::getItemId)
                .collect(Collectors.toList());

        List<Item> itemList = itemRepository.findAllById(itemIds);
        Map<UUID, Item> itemListMap = itemList.stream().collect(Collectors.toMap(Item::getId, item -> item));

        List<OrderRequestDTO.OrderItemDTO> availableItems = orderRequestData.getOrderItems().stream()
                .filter(orderItem -> {
                    Item item = itemListMap.get(orderItem.getItemId());
                    return item != null && item.getStock_quantity() >= orderItem.getQuantity();
                })
                .collect(Collectors.toList());

        if (availableItems.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "주문 가능한 아이템이 없습니다 재고가 모두 소진되었습니다.");
        }

        Integer totalPrice = availableItems.stream()
                .mapToInt(orderItem -> {
                    Item item = itemListMap.get(orderItem.getItemId());
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

        List<OrderItem> orderItems = new ArrayList<>();
        
        for (OrderRequestDTO.OrderItemDTO dto : availableItems) {
            // 비관적 락으로 재고 감소
            Item updatedItem = itemService.decreaseStockAndGetItem(dto.getItemId(), dto.getQuantity());
            
            // 감소된 재고 정보를 가진 아이템으로 주문 아이템 생성
            OrderItem orderItem = new OrderItem();
            orderItem.setItem(updatedItem);  // 업데이트된 아이템 사용
            orderItem.setQuantity(dto.getQuantity());
            orderItem.setOrder(order);
            orderItems.add(orderItem);
        }
        
        order.setOrderItems(orderItems);

        Order completedOrder = orderRepository.save(order);

        //5. 완료되면 고객에게 해당 요청에 대한 응답을 반환한다.

        return completedOrder;
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void test(OrderRequestDTO orderRequest) {
        orderRequest.getOrderItems().forEach(orderItem -> {
            Item item = this.itemRepository.findById(orderItem.getItemId()).orElseThrow(() -> new RuntimeException("ㅋㅋㅋ"));
            try {
                if (item.getStock_quantity() >= orderItem.getQuantity()) {
                    item.setStock_quantity(item.getStock_quantity() - orderItem.getQuantity());
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        });
    }
}
