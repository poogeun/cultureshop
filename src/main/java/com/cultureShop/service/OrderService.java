package com.cultureShop.service;

import com.cultureShop.constant.PaymentStatus;
import com.cultureShop.dto.OrderFormDto;
import com.cultureShop.dto.OrderHistDto;
import com.cultureShop.dto.OrderItemDto;
import com.cultureShop.entity.*;
import com.cultureShop.repository.ItemImgRepository;
import com.cultureShop.repository.ItemRepository;
import com.cultureShop.repository.MemberRepository;
import com.cultureShop.repository.OrderRepository;
import jakarta.persistence.EntityNotFoundException;

import lombok.RequiredArgsConstructor;
import org.antlr.v4.runtime.atn.SemanticContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {

    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;
    private final OrderRepository orderRepository;
    private final ItemImgRepository itemImgRepository;

    public Long order(OrderFormDto orderFormDto, String email) {
        Item item = itemRepository.findById(orderFormDto.getItemId())
                .orElseThrow(EntityNotFoundException::new);
        Member member = memberRepository.findByEmail(email);

        Payment payment = Payment.builder()
                .price(orderFormDto.getOrderPrice())
                .status(PaymentStatus.READY)
                .build();

        List<OrderItem> orderItemList = new ArrayList<>();
        OrderItem orderItem = OrderItem.createOrderItem(item, orderFormDto.getViewDay(), orderFormDto.getCount());
        orderItemList.add(orderItem);

        Order order = Order.createOrder(member, payment, orderItemList, orderFormDto);
        orderRepository.save(order);
        return order.getId();
    }

    public Long orders(List<OrderFormDto> orderFormDtoList, String email) {
        Member member = memberRepository.findByEmail(email);

        Payment payment = Payment.builder()
                .price(orderFormDtoList.getFirst().getOrderPrice())
                .status(PaymentStatus.READY)
                .build();

        List<OrderItem> orderItemList = new ArrayList<>();
        for(OrderFormDto orderFormDto : orderFormDtoList) {
            Item item = itemRepository.findById(orderFormDto.getItemId())
                    .orElseThrow(EntityNotFoundException::new);
            OrderItem orderItem = OrderItem.createOrderItem(item, orderFormDto.getViewDay(),
                    orderFormDto.getCount());
            orderItemList.add(orderItem);
        }

        Order order = Order.createOrder(member, payment, orderItemList,orderFormDtoList.getFirst());
        orderRepository.save(order);

        return order.getId();
    }

    @Transactional(readOnly = true)
    public Page<OrderHistDto> getOrderList(String email, Pageable pageable) {
        List<Order> orders = orderRepository.findOrders(email, pageable);
        Long totalCount = orderRepository.countOrder(email);
        List<OrderHistDto> orderHistDtos = new ArrayList<>();

        for(Order order : orders) {
            OrderHistDto orderHistDto = new OrderHistDto(order);
            List<OrderItem> orderItems = order.getOrderItems();
            for(OrderItem orderItem : orderItems) {
                ItemImg itemImg = itemImgRepository.findByItemIdAndRepImgYn(orderItem.getItem().getId(), "Y");
                OrderItemDto orderItemDto = new OrderItemDto(orderItem, itemImg.getImgUrl());
                orderHistDto.addOrderItemDto(orderItemDto);
            }
            orderHistDtos.add(orderHistDto);
        }
        return new PageImpl<OrderHistDto>(orderHistDtos, pageable, totalCount);
    }
}
