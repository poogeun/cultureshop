package com.cultureShop.service;

import com.cultureShop.dto.MainItemDto;
import com.cultureShop.dto.ReviewItemDto;
import com.cultureShop.entity.Member;
import com.cultureShop.entity.Order;
import com.cultureShop.entity.OrderItem;
import com.cultureShop.entity.Review;
import com.cultureShop.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderItemService {

    private final MemberRepository memberRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ReviewRepository reviewRepository;
    private final ItemRepository itemRepository;

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

    public List<MainItemDto> getNoRevOrderItems(String email) {
        List<OrderItem> orderItems = orderItemRepository.findByCreatedByOrderByRegTimeDesc(email);
        List<MainItemDto> noRevItemList = new ArrayList<>();

        for(OrderItem orderItem : orderItems) {
            if(orderItem.getReview() == null) {
                MainItemDto noRevItem = itemRepository.findMainItemDto(orderItem.getItem().getId());
                noRevItemList.add(noRevItem);
            }
        }
        return noRevItemList;
    }


}
