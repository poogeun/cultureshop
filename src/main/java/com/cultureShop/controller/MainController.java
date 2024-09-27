package com.cultureShop.controller;

import com.cultureShop.config.CustomOAuth2UserService;
import com.cultureShop.dto.ItemSearchDto;
import com.cultureShop.dto.MainItemDto;
import com.cultureShop.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class MainController {

    private final ItemService itemService;
    private final CustomOAuth2UserService customOAuth2UserService;

    /* 메인 페이지 */
    @GetMapping(value = "/")
    public String main(Model model, Principal principal){

        List<MainItemDto> bannerItems = itemService.getBannerItem();
        List<MainItemDto> seoulItems;
        List<MainItemDto> kkItems;
        List<MainItemDto> ksItems;
        List<MainItemDto> jrItems;
        List<MainItemDto> jjItems;
        List<MainItemDto> ccItems;
        List<MainItemDto> exhiItems;
        List<MainItemDto> musItems;
        List<MainItemDto> festItems;

        /* 지역별 상품목록 */
        seoulItems = itemService.getAddressItem("서울");
        kkItems = itemService.getAddressItem("경기");
        ksItems = itemService.getAddressItem("경상");
        jrItems = itemService.getAddressItem("전라");
        jjItems = itemService.getAddressItem("제주");
        ccItems = itemService.getAddressItem("충청");

        /* 카테고리별 상품목록 */
        exhiItems = itemService.getCategoryItem("exhibition");
        musItems = itemService.getCategoryItem("museum");
        festItems = itemService.getCategoryItem("festival");

        if(principal != null) { // 로그인된 경우
            String email = customOAuth2UserService.getSocialEmail(principal);
            if(email == null) {
                email = principal.getName();
            }

            /* 찜 상품 여부 포함된 상품목록 */
            seoulItems = itemService.getUserAddrItem("서울", email);
            kkItems = itemService.getUserAddrItem("경기", email);
            ksItems = itemService.getUserAddrItem("경상", email);
            jrItems = itemService.getUserAddrItem("전라", email);
            jjItems = itemService.getUserAddrItem("제주", email);
            ccItems = itemService.getUserAddrItem("충청", email);
            exhiItems = itemService.getUserCategoryItem("exhibition", email);
            musItems = itemService.getUserCategoryItem("museum", email);
            festItems = itemService.getUserCategoryItem("festival", email);
        }


        model.addAttribute("bannerItems", bannerItems);
        model.addAttribute("seoulItems", seoulItems);
        model.addAttribute("kkItems", kkItems);
        model.addAttribute("ksItems", ksItems);
        model.addAttribute("jrItems", jrItems);
        model.addAttribute("jjItems", jjItems);
        model.addAttribute("ccItems", ccItems);
        model.addAttribute("exhiItems", exhiItems);
        model.addAttribute("musItems", musItems);
        model.addAttribute("festItems", festItems);

        return "main";
    }

    /* 검색 결과 페이지 */
    @GetMapping(value = "/search")
    public String searchResult(ItemSearchDto itemSearchDto, Model model){

        List<MainItemDto> items = itemService.getSearchItemList(itemSearchDto);
        int itemCount = items.size();

        model.addAttribute("items", items);
        model.addAttribute("itemCount", itemCount);
        model.addAttribute("itemSearchDto", itemSearchDto);

        return "item/searchItem";
    }
}
