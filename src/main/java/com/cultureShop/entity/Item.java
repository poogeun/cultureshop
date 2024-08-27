package com.cultureShop.entity;

import com.cultureShop.constant.ItemStartStatus;
import com.cultureShop.dto.ItemFormDto;
import com.cultureShop.exception.OutOfStockException;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.modelmapper.ModelMapper;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "item")
@Getter
@Setter
@ToString
public class Item extends BaseEntity {

    @Id
    @Column(name = "item_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private  Long id; // 상품코드

    @Column(nullable = false, length = 100)
    private String itemName; // 상품명

    @Column
    private String place; // 장소

    @Column
    private String address; // 주소

    @Column
    private LocalDate startDay; // 시작일

    @Column
    private LocalDate endDay; // 종료일

    @Column(nullable = false, length = 50)
    private String category; // 카테고리

    @Column(nullable = false)
    private int price; // 가격

    @Column
    private int stockNumber; // 재고

    @Lob
    @Column(length = 500)
    private String itemDetail; // 상품상세설명

    @Enumerated(EnumType.STRING)
    private ItemStartStatus itemStartStatus; // 진행중 or 전

    @Lob
    @Column(length = 500)
    private String info; // 취소/교환/반품 안내

    @OneToMany(mappedBy = "item")
    private List<Review> reviews;

    public void updateItem(ItemFormDto itemFormDto) {
        this.itemName = itemFormDto.getItemName();
        this.place = itemFormDto.getPlace();
        this.address = itemFormDto.getAddress();
        this.startDay = itemFormDto.getStartDay();
        this.endDay = itemFormDto.getEndDay();
        this.category = itemFormDto.getCategory();
        this.price = itemFormDto.getPrice();
        this.itemDetail = itemFormDto.getItemDetail();
        this.itemStartStatus = itemFormDto.getItemStartStatus();
        this.info = itemFormDto.getInfo();
    }

    public void removeStock(int stockNumber) {
        int restStock = this.stockNumber - stockNumber;
        if(restStock < 0) {
            throw new OutOfStockException("상품의 재고가 부족합니다.(현재 재고 수량: "+this.stockNumber);
        }
        this.stockNumber = restStock;
    }

    public void addStock(int stockNumber) {
        this.stockNumber += stockNumber;
    }

}

