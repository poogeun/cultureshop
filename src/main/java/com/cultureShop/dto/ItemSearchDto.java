package com.cultureShop.dto;

import com.cultureShop.constant.ItemStartStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemSearchDto {

    private String searchDateType; // 조회 날짜
    private ItemStartStatus searchStartStatus; // 오픈상태
    private String searchCategory; // 조회 유형
    private String searchQuery = ""; // 검색 단어
}
