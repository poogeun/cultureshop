package com.cultureShop.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MusArtMainDto {

    private Long id;
    private String name;
    private String type;
    private String address;
    private String openTime;
    private String closeTime;

    @QueryProjection
    public MusArtMainDto(Long id, String name, String type, String address, String openTime, String closeTime) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.address = getSimpleAddr(address);
        this.openTime = openTime;
        this.closeTime = closeTime;
    }

    private String getSimpleAddr(String address) {
        String[] list = address.split(" ");
        String simpleAddr = list[0] +" "+ list[1];

        return simpleAddr;
    }
}
