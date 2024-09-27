package com.cultureShop.service;

import com.cultureShop.constant.PaymentStatus;
import com.cultureShop.dto.OrderFormDto;
import com.cultureShop.dto.OrderHistDto;
import com.cultureShop.dto.OrderItemDto;
import com.cultureShop.entity.*;
import com.cultureShop.repository.*;
import jakarta.persistence.EntityNotFoundException;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;

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
    private final PaymentRepository paymentRepository;

    /* 바로 주문 */
    public Long order(OrderFormDto orderFormDto, String email) {
        Item item = itemRepository.findById(orderFormDto.getItemId())
                .orElseThrow(EntityNotFoundException::new);
        Member member = memberRepository.findByEmail(email);

        // 결제 준비
        Payment payment = Payment.builder()
                .price(orderFormDto.getOrderPrice())
                .status(PaymentStatus.READY)
                .build();
        paymentRepository.save(payment);

        List<OrderItem> orderItemList = new ArrayList<>();
        OrderItem orderItem = OrderItem.createOrderItem(item, orderFormDto.getViewDay(), orderFormDto.getCount());
        orderItemList.add(orderItem);

        Order order = Order.createOrder(member, payment, orderItemList, orderFormDto);
        orderRepository.save(order);
        return order.getId();
    }

    /* 찜 선택 주문 */
    public Long orders(List<OrderFormDto> orderFormDtoList, String email) {
        Member member = memberRepository.findByEmail(email);

        Payment payment = Payment.builder()
                .price(orderFormDtoList.getFirst().getOrderPrice())
                .status(PaymentStatus.READY)
                .build();
        paymentRepository.save(payment);

        List<OrderItem> orderItemList = new ArrayList<>();
        for(OrderFormDto orderFormDto : orderFormDtoList) { // 다중 주문일 경우 상품 수만큼 orderFormDto
            Item item = itemRepository.findById(orderFormDto.getItemId())
                    .orElseThrow(EntityNotFoundException::new);
            OrderItem orderItem = OrderItem.createOrderItem(item, orderFormDto.getViewDay(),
                    orderFormDto.getCount());
            orderItemList.add(orderItem);
        }

        Order order = Order.createOrder(member, payment, orderItemList, orderFormDtoList.getFirst());
        orderRepository.save(order);

        return order.getId();
    }

    @Transactional(readOnly = true)
    public Page<OrderHistDto> getOrderList(String email, Pageable pageable) {
        List<Order> orders = orderRepository.findOrders(email, pageable);
        Long totalCount = orderRepository.countOrder(email);
        List<OrderHistDto> orderHistDtos = new ArrayList<>();

        for(Order order : orders) { // 유저의 주문서만큼
            OrderHistDto orderHistDto = new OrderHistDto(order);
            List<OrderItem> orderItems = order.getOrderItems();
            for(OrderItem orderItem : orderItems) { // 주문서의 주문상품 dto 객체 생성
                ItemImg itemImg = itemImgRepository.findByItemIdAndRepImgYn(orderItem.getItem().getId(), "Y");
                OrderItemDto orderItemDto = new OrderItemDto(orderItem, itemImg.getImgUrl());
                orderHistDto.addOrderItemDto(orderItemDto);
            }
            orderHistDtos.add(orderHistDto); // 주문 상품 리스트
        }
        return new PageImpl<OrderHistDto>(orderHistDtos, pageable, totalCount);
    }

    public void deleteOrder(String orderUid) {
        Order order = orderRepository.findByOrderUid(orderUid);
        orderRepository.delete(order);
        paymentRepository.delete(order.getPayment());
    }

    /* 현재 유저와 주문서의 회원 같은지 */
    @Transactional(readOnly = true)
    public boolean validateOrder(Long orderId, String email) {
        Member curMember = memberRepository.findByEmail(email);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(EntityNotFoundException::new);
        Member savedMember = order.getMember();

        if(!StringUtils.equals(curMember.getEmail(), savedMember.getEmail())) {
            return false;
        }
        return true;
    }

    @Transactional(readOnly = true)
    public void cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(EntityNotFoundException::new);
        List<OrderItem> orderItems = order.getOrderItems();
        for(OrderItem orderItem : orderItems) {
            orderItem.cancel();
        }
        orderRepository.delete(order);
    }
}
