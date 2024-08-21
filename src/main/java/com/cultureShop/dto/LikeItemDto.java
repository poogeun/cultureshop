package com.cultureShop.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class LikeItemDto {

    private Long likeItemId;
    private Long itemId;
    private String itemName;
    private String place;
    private String address;
    private int price;
    private LocalDate startDay;
    private LocalDate endDay;
    private String imgUrl;

    @QueryProjection
    public LikeItemDto(Long likeItemId, Long itemId, String itemName, String place, String address,
                       int price, LocalDate startDay, LocalDate endDay, String imgUrl) {
        this.likeItemId = likeItemId;
        this.itemId = itemId;
        this.itemName = itemName;
        this.place = place;
        this.address = address;
        this.price = price;
        this.startDay = startDay;
        this.endDay = endDay;
        this.imgUrl = imgUrl;
    }
}
