package com.cultureShop.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "mus_art")
@Getter
@Setter
@ToString
public class MusArt {

    @Id
    @Column(name = "mus_art_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private  Long id;

    private String name; // 시설명
    private String type; // 박물관미술관구분
    private String address; // 주소
    private String tel; // 시설 전화번호
    private String openTime; // 관람시작시각
    private String closeTime; // 관람종료시각
    private double geoX; // 위도
    private double geoY; // 경도


}
