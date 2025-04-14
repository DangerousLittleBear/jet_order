package com.example.ordersystem.config;

import com.example.ordersystem.entity.Item;
import com.example.ordersystem.repository.ItemRepository;
import com.example.ordersystem.repository.OrderRepository;
import com.example.ordersystem.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationListener<ContextRefreshedEvent> {

    private final ItemService itemService;
    private final ItemRepository itemRepository;
    private final OrderRepository orderRepository;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        // 삭제와 저장을 별도의 트랜잭션으로 분리
        deleteAllOrders();
        deleteAllItems();
        saveExampleItems();
    }

    @Transactional
    public void deleteAllItems() {
        itemRepository.deleteAll();
        itemRepository.flush();
    }

    @Transactional
    public void deleteAllOrders() {
        orderRepository.deleteAll();
        orderRepository.flush();
    }

    @Transactional
    public void saveExampleItems() {
        // 새로운 엔티티 생성
        Item mxKeys = new Item();
        mxKeys.setName("logitech mx keys");
        mxKeys.setDescription("로지텍의 사무용 펜타그래프 키보드입니다.");
        mxKeys.setPrice(159000);
        mxKeys.setStock_quantity(500000);

        Item realforce = new Item();
        realforce.setName("리얼포스 R3");
        realforce.setDescription("리얼포스 텐키리스 모델입니다.");
        realforce.setPrice(390000);
        realforce.setStock_quantity(500000);


        Item mxMaster = new Item();
        mxMaster.setName("Logitech mx master 3s");
        mxMaster.setDescription("로지텍의 사무용 마우스입니다.");
        mxMaster.setPrice(139000);
        mxMaster.setStock_quantity(500000);


        // 개별 저장
        itemService.save(mxKeys);
        itemService.save(realforce);
        itemService.save(mxMaster);
    }
}
