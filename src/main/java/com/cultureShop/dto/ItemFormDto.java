package com.cultureShop.dto;

import com.cultureShop.constant.ItemStartStatus;
import com.cultureShop.entity.Item;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ItemFormDto {

    private Long id;

    @NotBlank(message = "상품명은 필수 입력 값입니다.")
    private String itemName;

    private String place;

    private String address;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDay;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDay;

    private String category;

    @NotNull(message = "가격은 필수 입력 값입니다.")
    private Integer price;

    @NotNull(message = "재고는 필수 입력 값입니다.")
    private int stockNumber;

    private String itemDetail;

    private ItemStartStatus itemStartStatus;

    //------------------------------------------------------------------
    // ItemImg
    private List<ItemImgDto> itemImgDtoList = new ArrayList<>();

    private List<Long> itemImgIds = new ArrayList<>();
    //------------------------------------------------------------------
    // ModelMapper
    private static ModelMapper modelMapper = new ModelMapper();

    public Item createItem() {
        return modelMapper.map(this, Item.class);
    }

    public static ItemFormDto of(Item item) { // 상품 정보 수정을 위한 메소드
        return modelMapper.map(item, ItemFormDto.class);
    }
}
