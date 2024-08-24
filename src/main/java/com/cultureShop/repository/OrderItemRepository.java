package com.cultureShop.repository;

import com.cultureShop.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    OrderItem findByCreatedByAndItemId(String email, Long itemId);

    List<OrderItem> findByCreatedByOrderByRegTimeDesc(String email);
}
