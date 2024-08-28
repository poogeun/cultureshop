package com.cultureShop.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MusArtApiDto {

    private String name; // 시설명
    private String type; // 박물관미술관구분
    private String address; // 주소
    private String tel; // 시설 전화번호
    private String openTime; // 관람시작시각
    private String closeTime; // 관람종료시각
    private double geoX; // 위도
    private double geoY; // 경도

    public MusArtApiDto(String name, String type, String address, String tel, String openTime, String closeTime, double geoX, double geoY) {
        this.name = name;
        this.type = getType(name);
        this.address = address;
        this.tel = tel;
        this.openTime = openTime;
        this.closeTime = substCloseTime(closeTime);
        this.geoX = geoX;
        this.geoY = geoY;
    }

    public MusArtApiDto() {
    }

    private String getType(String name) {
        if(name.contains("박물관") || name.contains("기념관")) {
            return "museum";
        }
        else if(name.contains("미술관") || name.contains("아트") ) {
            return "art";
        }
        else{
            return "";
        }
    }

    private String substCloseTime(String closeTime) {
        return closeTime.substring(0,5);
    }
}
