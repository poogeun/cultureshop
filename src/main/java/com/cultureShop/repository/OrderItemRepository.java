package com.cultureShop.repository;

import com.cultureShop.dto.OrderItemDto;
import com.cultureShop.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    @Query("select oi from OrderItem oi where oi.order.member.email = :email and oi.item.id = :itemId")
    OrderItem findByEmailAndItemId(String email, Long itemId);

    List<OrderItem> findByCreatedByOrderByRegTimeDesc(String email);

    List<OrderItem> findByItemIdAndCreatedBy(Long itemId, String createdBy);

    OrderItem findByReviewId(Long reviewId);

    @Query("select new com.cultureShop.dto.OrderItemDto(i.itemName, oi.count, oi.viewDay, i.place, oi.orderPrice, iti.imgUrl) " +
            "from OrderItem oi, ItemImg iti join oi.item i " +
            "where oi.order.id = :orderId and iti.item.id = oi.item.id and iti.repImgYn = 'Y'")
    List<OrderItemDto> findOrderItemDto(Long orderId);
}
