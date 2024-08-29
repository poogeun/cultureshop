package com.cultureShop.controller;

import com.cultureShop.dto.MainItemDto;
import com.cultureShop.dto.MusArtMainDto;
import com.cultureShop.entity.Item;
import com.cultureShop.entity.MusArt;
import com.cultureShop.service.ItemService;
import com.cultureShop.service.MusArtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/place")
public class PlaceController {

    private final MusArtService musArtService;
    private final ItemService itemService;

    @GetMapping
    public String placeMain(Model model) {

        List<MusArtMainDto> museums = musArtService.getPlaceMainMusArt("museum");
        List<MusArtMainDto> arts = musArtService.getPlaceMainMusArt("art");
        List<MainItemDto> festivals = itemService.getCategoryItem("festival");

        model.addAttribute("museums", museums);
        model.addAttribute("arts", arts);
        model.addAttribute("festivals", festivals);

        return "place/placeMain";
    }
/*
    @PostMapping
    public @ResponseBody ResponseEntity placeMain() {

    }

 */
}
