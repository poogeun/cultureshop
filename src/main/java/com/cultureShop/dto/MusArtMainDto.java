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
    private double geoX;
    private double geoY;
    private Long userLikeYn; // 해당 유저의 찜 여부

    // 로그인 전
    @QueryProjection
    public MusArtMainDto(Long id, String name, String type, String address, String openTime,
                         String closeTime, double geoX, double geoY) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.address = getSimpleAddr(address);
        this.openTime = openTime;
        this.closeTime = closeTime;
        this.geoX = geoX;
        this.geoY = geoY;
    }

    // 로그인 후
    public MusArtMainDto(Long id, String name, String type, String address, String openTime, String closeTime,
                         double geoX, double geoY, Long userLikeYn) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.address = getSimpleAddr(address);
        this.openTime = openTime;
        this.closeTime = closeTime;
        this.geoX = geoX;
        this.geoY = geoY;
        this.userLikeYn = userLikeYn;
    }

    /* 간단한 주소로 받기 (서울특별시 종로구)*/
    private String getSimpleAddr(String address) {
        String[] list = address.split(" ");

        if (list.length < 2) { // 이미 데이터가 간단한 주소이면
            return address;
        }
        String simpleAddr = list[0] +" "+ list[1];

        return simpleAddr;
    }
}
