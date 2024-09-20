package com.cultureShop.repository;

import com.cultureShop.dto.ItemSearchDto;
import com.cultureShop.dto.MainItemDto;
import com.cultureShop.entity.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ItemRepositoryCustom {

    Page<Item> getAdminItemPage(ItemSearchDto itemSearchDto, Pageable pageable);

    List<MainItemDto> getSearchItemList(ItemSearchDto itemSearchDto);

    Page<MainItemDto> getCateItemList(String category, String address, String sort, Pageable pageable);

}
