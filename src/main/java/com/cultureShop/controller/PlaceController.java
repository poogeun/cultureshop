package com.cultureShop.controller;

import com.cultureShop.entity.Item;
import com.cultureShop.entity.MusArt;
import com.cultureShop.service.ItemService;
import com.cultureShop.service.MusArtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/place")
public class PlaceController {

    private final MusArtService musArtService;
    private final ItemService itemService;

    @GetMapping
    public String placeMain(Model model) {

        List<MusArt> museums = musArtService.getAllMusArt("museum");
        List<MusArt> arts = musArtService.getAllMusArt("art");
        List<Item> festivals = itemService.getFestItem("festival");

        model.addAttribute("museums", museums);
        model.addAttribute("arts", arts);
        model.addAttribute("festivals", festivals);

        return "place/placeMain";
    }
}
