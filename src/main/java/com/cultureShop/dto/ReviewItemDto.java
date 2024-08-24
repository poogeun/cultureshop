package com.cultureShop.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ReviewItemDto {

    private Long reviewId;
    private Long itemId;
    private String itemName;
    private String place;
    private String address;
    private LocalDate viewDay;
    private String imgUrl;
    private LocalDate regTime;

    @QueryProjection
    public ReviewItemDto(Long reviewId, Long itemId, String itemName, String place, String address, LocalDate viewDay, String imgUrl, LocalDate regTime) {
        this.reviewId = reviewId;
        this.itemId = itemId;
        this.itemName = itemName;
        this.place = place;
        this.address = address;
        this.viewDay = viewDay;
        this.imgUrl = imgUrl;
        this.regTime = regTime;
    }
}
