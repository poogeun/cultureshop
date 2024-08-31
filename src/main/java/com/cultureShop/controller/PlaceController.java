package com.cultureShop.controller;

import com.cultureShop.API.SigunguExplorer;
import com.cultureShop.dto.ApiDto.SigunguApiDto;
import com.cultureShop.dto.LikeDto;
import com.cultureShop.dto.MainItemDto;
import com.cultureShop.dto.MusArtMainDto;
import com.cultureShop.entity.Item;
import com.cultureShop.entity.MusArt;
import com.cultureShop.entity.OrderItem;
import com.cultureShop.entity.Review;
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
    public String placeDtl(@PathVariable Long placeId, Model model) {
        MusArt musArt = musArtRepository.findById(placeId)
                .orElseThrow(EntityNotFoundException::new);

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
}
