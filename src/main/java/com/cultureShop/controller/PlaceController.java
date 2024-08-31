package com.cultureShop.controller;

import com.cultureShop.API.SigunguExplorer;
import com.cultureShop.dto.ApiDto.SigunguApiDto;
import com.cultureShop.dto.MainItemDto;
import com.cultureShop.dto.MusArtMainDto;
import com.cultureShop.entity.Item;
import com.cultureShop.entity.MusArt;
import com.cultureShop.service.ItemService;
import com.cultureShop.service.MusArtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/place")
public class PlaceController {

    private final MusArtService musArtService;
    private final ItemService itemService;
    private final SigunguExplorer sigunguExplorer;

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

    @GetMapping(value = "/address")
    public String searchAddress(SigunguApiDto sigunguDatas, Model model) {

        model.addAttribute("sigungus", sigunguDatas);

        return "place/searchAddress";
    }

    @PostMapping(value = "/address")
    public @ResponseBody ResponseEntity searchAddress(@RequestParam String simAddr) {

        List<SigunguApiDto> addrList = sigunguExplorer.getSigunguApiDatas();

        List<String> curAddr = new ArrayList<>();
        for(SigunguApiDto sigunguDto : addrList) {
            String addr = sigunguDto.getSimAddr();
            if(addr.contains(simAddr)) {
                curAddr.add(addr);
            }
        }

        return new ResponseEntity<List<String>>(curAddr, HttpStatus.OK);
    }
}
