package com.cultureShop.dto;

import com.cultureShop.entity.OrderItem;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@Setter
@ToString
public class OrderItemDto {

    private String itemName;
    private int count;
    private LocalDate viewDay;
    private String place;
    private int orderPrice;
    private String imgUrl;

    public OrderItemDto(OrderItem orderItem, String imgUrl){
        this.itemName = orderItem.getItem().getItemName();
        this.count = orderItem.getCount();
        this.viewDay = orderItem.getViewDay();
        this.orderPrice = orderItem.getOrderPrice();
        this.imgUrl = imgUrl;
    }

    public OrderItemDto(String itemName, int count, LocalDate viewDay, String place, int orderPrice, String imgUrl) {
        this.itemName = itemName;
        this.count = count;
        this.viewDay = viewDay;
        this.place = place;
        this.orderPrice = orderPrice;
        this.imgUrl = imgUrl;
    }
}
