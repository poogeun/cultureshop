package com.cultureShop.service;

import com.cultureShop.dto.OrderFormDto;
import com.cultureShop.entity.Item;
import com.cultureShop.entity.Member;
import com.cultureShop.entity.Order;
import com.cultureShop.entity.OrderItem;
import com.cultureShop.repository.ItemRepository;
import com.cultureShop.repository.MemberRepository;
import com.cultureShop.repository.OrderRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {

    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;
    private final OrderRepository orderRepository;

    public Long order(OrderFormDto orderDto, String email) {
        Item item = itemRepository.findById(orderDto.getItemId())
                .orElseThrow(EntityNotFoundException::new);
        Member member = memberRepository.findByEmail(email);

        List<OrderItem> orderItemList = new ArrayList<>();
        OrderItem orderItem = OrderItem.createOrderItem(item, orderDto.getViewDay(), orderDto.getCount());
        orderItemList.add(orderItem);

        Order order = Order.createOrder(member, orderItemList);
        orderRepository.save(order);
        return order.getId();
    }
}
