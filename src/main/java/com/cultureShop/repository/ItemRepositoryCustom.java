package com.cultureShop.repository;

import com.cultureShop.dto.ItemSearchDto;
import com.cultureShop.dto.MainItemDto;
import com.cultureShop.entity.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ItemRepositoryCustom {

    // 관리자 상품 목록 조회
    Page<Item> getAdminItemPage(ItemSearchDto itemSearchDto, Pageable pageable);

    // 검색된 상품 조회
    List<MainItemDto> getSearchItemList(ItemSearchDto itemSearchDto);

    // 상품 카테고리 목록 조회
    Page<MainItemDto> getCateItemList(String category, String address, String sort, Pageable pageable);

    // 카테고리 상품 총 개수 조회
    long getCateItemCount(String category, String address, String sort);
}
