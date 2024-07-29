package com.cultureShop.entity;

import com.cultureShop.constant.ItemStartStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Table(name = "item")
@Getter
@Setter
@ToString
public class Item {

    @Id
    @Column(name = "item_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private  Long id; // 상품코드

    @Column(nullable = false, length = 50)
    private String itemName; // 상품명

    @Column
    private String place; // 장소

    @Column
    private String address; // 주소

    @Column(nullable = false, length = 50)
    private String period; // 기간

    @Column(nullable = false, length = 50)
    private String category; // 카테고리

    @Column(nullable = false)
    private int price; // 가격

    @Column
    private int stockNumber; // 재고

    @Lob
    @Column
    private String itemDetail; // 상품상세설명

    @Enumerated(EnumType.STRING)
    private ItemStartStatus itemStartStatus; // 진행중 or 전

    @Lob
    @Column(length = 500)
    private String info; // 취소/교환/반품 안내

    private LocalDateTime regTime; // 등록 시간

    private LocalDateTime updateTime; // 수정 시간
}

