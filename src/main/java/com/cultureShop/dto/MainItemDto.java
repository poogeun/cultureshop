package com.cultureShop.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@Setter
@ToString
public class MainItemDto {

    private Long id;
    private String itemName;
    private String place;
    private String address;
    private int price;
    private LocalDate startDay;
    private LocalDate endDay;
    private String imgUrl;

    @QueryProjection
    public MainItemDto(Long id, String itemName, String place, String address, int price, LocalDate startDay, LocalDate endDay, String imgUrl) {
        this.id = id;
        this.itemName = itemName;
        this.place = place;
        this.address = address;
        this.price = price;
        this.startDay = startDay;
        this.endDay = endDay;
        this.imgUrl = imgUrl;
    }
}
