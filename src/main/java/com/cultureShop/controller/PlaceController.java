package com.cultureShop.controller;

import com.cultureShop.API.SigunguExplorer;
import com.cultureShop.dto.ApiDto.SigunguApiDto;
import com.cultureShop.dto.LikeDto;
import com.cultureShop.dto.MainItemDto;
import com.cultureShop.dto.MusArtMainDto;
import com.cultureShop.entity.*;
import com.cultureShop.repository.MusArtRepository;
import com.cultureShop.service.ItemService;
import com.cultureShop.service.MusArtService;
import com.cultureShop.service.UserLikeItemService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import retrofit2.http.Path;

import java.security.Principal;
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
    private final MusArtRepository musArtRepository;
    private final UserLikeItemService userLikeItemService;

    @GetMapping(value = {"", "/{simAddr}"})
    public String placeMain(@PathVariable(required = false) String simAddr, Model model, Principal principal) {

        List<MusArtMainDto> museums;
        List<MusArtMainDto> arts;
        List<MainItemDto> festivals;

        museums = musArtService.getPlaceMainMusArt("museum");
        arts = musArtService.getPlaceMainMusArt("art");
        festivals = itemService.getCategoryItem("festival");

        if(simAddr != null) {
            museums = musArtService.getAddrMusArt(simAddr, "museum");
            arts = musArtService.getAddrMusArt(simAddr, "art");
            festivals = itemService.getPlaceFest(simAddr, "festival");
        }

        if(principal != null) {
            museums = musArtService.getUserPlaceMainMusArt("museum", principal.getName());
            arts = musArtService.getUserPlaceMainMusArt("art", principal.getName());
            festivals = itemService.getCategoryItem("festival");

            if(simAddr != null) {
                museums = musArtService.getUserAddrMusArt(simAddr, "museum", principal.getName());
                arts = musArtService.getUserAddrMusArt(simAddr, "art", principal.getName());
                festivals = itemService.getPlaceFest(simAddr, "festival");
            }
            for(MusArtMainDto art : arts){
                System.out.println(art.getUserLikeYn());
            }
        }

        model.addAttribute("museums", museums);
        model.addAttribute("arts", arts);
        model.addAttribute("festivals", festivals);
        model.addAttribute("simAddr", simAddr);

        return "place/placeMain";
    }

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

    @GetMapping(value = "/detail/{placeId}")
    public String placeDtl(@PathVariable Long placeId, Model model, Principal principal) {
        MusArt musArt = musArtRepository.findById(placeId)
                .orElseThrow(EntityNotFoundException::new);
        List<UserLikeItem> likePlaces = userLikeItemService.getLikePlaces(placeId);
        int likeCount = likePlaces.size();

        if(principal != null) {
            if (userLikeItemService.findLikePlace(principal.getName(), placeId)) {
                model.addAttribute("isLikePlace", "afterLike");
            } else {
                model.addAttribute("isLikePlace", "beforeLike");
            }
        }

        model.addAttribute("likePlaces", likePlaces);
        model.addAttribute("likeCount", likeCount);
        model.addAttribute("musArt", musArt);
        return "place/placeDtl";
    }

    @PostMapping(value = "/like")
    public @ResponseBody ResponseEntity addLike(@RequestBody LikeDto likeDto, Principal principal) {

        if(principal != null) {
            userLikeItemService.addLikePlace(principal.getName(), likeDto.getItemId());
            return new ResponseEntity<Long>(likeDto.getItemId(), HttpStatus.OK);
        }
        else {
            return new ResponseEntity<String>("찜 기능은 로그인 후 이용 가능합니다.", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/list/{type}/{simAddr}")
    public String placeList(@PathVariable("type") String type,
                            @PathVariable(value = "simAddr", required = false) String simAddr, Model model) {

        List<MusArtMainDto> places = musArtService.getAllPlace(type);

        if(simAddr != null) {
            places = musArtService.getAllAddrPlace(simAddr, type);
        }
        model.addAttribute("places", places);

        return "place/placeList";
    }

}
