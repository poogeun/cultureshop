package com.cultureShop.service;

import com.cultureShop.dto.MainItemDto;
import com.cultureShop.dto.OrderItemDto;
import com.cultureShop.entity.OrderItem;
import com.cultureShop.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderItemService {

    private final OrderItemRepository orderItemRepository;
    private final ItemRepository itemRepository;

    /* 특정 상품이 유저의 주문상품인지 */
    public OrderItem getOrderItem(Long itemId, String email) {
        List<OrderItem> orderItems = orderItemRepository.findByItemIdAndCreatedBy(itemId, email);

        for(OrderItem orderItem : orderItems) {
            if(Objects.equals(orderItem.getItem().getId(), itemId)) {
                return orderItem;
            }
        }
        return null;
    }

    /* 리뷰 작성 전인 주문상품 리스트 */
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

    @Transactional(readOnly = true)
    public List<OrderItemDto> getOrderSuccess(Long orderId) {
        return orderItemRepository.findOrderItemDto(orderId);
    }

}
