package com.cultureShop.controller;

import com.cultureShop.dto.ItemSearchDto;
import com.cultureShop.dto.MainItemDto;
import com.cultureShop.entity.Item;
import com.cultureShop.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MainController {

    private final ItemService itemService;

    @GetMapping(value = "/")
    public String main(Model model){


        List<MainItemDto> exhiItems = itemService.getCategoryItem("exhibition");
        List<MainItemDto> musItems = itemService.getCategoryItem("museum");
        List<MainItemDto> festItems = itemService.getCategoryItem("festival");

        List<MainItemDto> seoulItems = itemService.getAddressItem("서울");
        List<MainItemDto> kkItems = itemService.getAddressItem("경기");


        model.addAttribute("exhiItems", exhiItems);
        model.addAttribute("musItems", musItems);
        model.addAttribute("festItems", festItems);

        model.addAttribute("seoulItems", seoulItems);
        model.addAttribute("kkItems", kkItems);

        return "main";
    }

    @GetMapping(value = "/search")
    public String searchResult(ItemSearchDto itemSearchDto, Model model){

        List<MainItemDto> items = itemService.getSearchItemList(itemSearchDto);

        model.addAttribute("items", items);
        model.addAttribute("itemSearchDto", itemSearchDto);

        return "item/searchItem";
    }
}
