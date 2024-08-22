package com.cultureShop.service;

import com.cultureShop.entity.Member;
import com.cultureShop.entity.Order;
import com.cultureShop.entity.OrderItem;
import com.cultureShop.repository.MemberRepository;
import com.cultureShop.repository.OrderItemRepository;
import com.cultureShop.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderItemService {

    private final MemberRepository memberRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    public OrderItem getOrderItem(Long itemId, String email) {
        Member member = memberRepository.findByEmail(email);
        List<Order> orders = orderRepository.findByMemberId(member.getId());

        for(Order order : orders) {
            List<OrderItem> orderItems = order.getOrderItems();
            for(OrderItem orderItem : orderItems) {
                if(orderItem.getItem().getId() == itemId) {
                    return orderItem;
                }
            }
        }
        return null;
    }
}
