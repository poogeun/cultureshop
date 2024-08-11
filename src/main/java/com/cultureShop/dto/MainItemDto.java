package com.cultureShop.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class MainItemDto {

    private Long id;
    private String itemName;
    private String place;
    private String address;
    private LocalDate startDay;
    private LocalDate endDay;
    private String imgUrl;

    @QueryProjection
    public MainItemDto(Long id, String itemName, String place, String address, LocalDate startDay, LocalDate endDay, String imgUrl) {
        this.id = id;
        this.itemName = itemName;
        this.place = place;
        this.address = address;
        this.startDay = startDay;
        this.endDay = endDay;
        this.imgUrl = imgUrl;
    }
}
