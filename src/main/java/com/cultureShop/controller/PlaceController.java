package com.cultureShop.controller;

import com.cultureShop.API.SigunguExplorer;
import com.cultureShop.config.CustomOAuth2UserService;
import com.cultureShop.dto.ApiDto.SigunguApiDto;
import com.cultureShop.dto.CommentDto;
import com.cultureShop.dto.LikeDto;
import com.cultureShop.dto.MainItemDto;
import com.cultureShop.dto.MusArtMainDto;
import com.cultureShop.entity.*;
import com.cultureShop.repository.MusArtRepository;
import com.cultureShop.service.CommentService;
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
    private final CommentService commentService;
    private final CustomOAuth2UserService customOAuth2UserService;

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
            String email = customOAuth2UserService.getSocialEmail(principal);
            if(email == null) {
                email = principal.getName();
            }

            museums = musArtService.getUserPlaceMainMusArt("museum", email);
            arts = musArtService.getUserPlaceMainMusArt("art", email);
            festivals = itemService.getCategoryItem("festival");

            if(simAddr != null) {
                museums = musArtService.getUserAddrMusArt(simAddr, "museum", email);
                arts = musArtService.getUserAddrMusArt(simAddr, "art", email);
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
        List<Comment> comments = commentService.getPlaceComments(placeId);

        if(principal != null) {
            String email = customOAuth2UserService.getSocialEmail(principal);
            if(email == null) {
                email = principal.getName();
            }

            if (userLikeItemService.findLikePlace(email, placeId)) {
                model.addAttribute("isLikePlace", "afterLike");
            } else {
                model.addAttribute("isLikePlace", "beforeLike");
            }

        }
        else{
            model.addAttribute("isLikePlace", "beforeLike");
            model.addAttribute("userEmail", "");
        }

        model.addAttribute("likePlaces", likePlaces);
        model.addAttribute("likeCount", likeCount);
        model.addAttribute("musArt", musArt);
        model.addAttribute("comments", comments);

        return "place/placeDtl";
    }

    @PostMapping(value = "/like")
    public @ResponseBody ResponseEntity addLike(@RequestBody LikeDto likeDto, Principal principal) {

        if(principal != null) {
            String email = customOAuth2UserService.getSocialEmail(principal);
            if(email == null) {
                email = principal.getName();
            }
            userLikeItemService.addLikePlace(email, likeDto.getItemId());
            return new ResponseEntity<Long>(likeDto.getItemId(), HttpStatus.OK);
        }
        else {
            return new ResponseEntity<String>("찜 기능은 로그인 후 이용 가능합니다.", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = {"/list/{type}", "/list/{type}/{simAddr}"})
    public String placeList(@PathVariable("type") String type,
                            @PathVariable(value = "simAddr", required = false) String simAddr, Model model) {

        List<MusArtMainDto> places = musArtService.getAllPlace(type);

        if(simAddr != null) {
            places = musArtService.getAllAddrPlace(simAddr, type);
        }

        model.addAttribute("places", places);

        return "place/placeList";
    }

    @PostMapping(value = "/comment/write")
    public @ResponseBody void commentWrite(@RequestBody CommentDto commentDto) {

        commentService.saveComment(commentDto);

    }

    @DeleteMapping(value = "/comment/{commentId}")
    public @ResponseBody ResponseEntity deleteComment(@PathVariable("commentId") Long commentId) {

        try {
            commentService.deleteComment(commentId);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<Long>(commentId, HttpStatus.OK);
    }

    @PatchMapping(value = "/comment/{commentId}")
    public @ResponseBody ResponseEntity updateComment(@PathVariable("commentId") Long commentId, String content) {

        if(content == null) {
            return new ResponseEntity<String>("내용을 입력해주세요.", HttpStatus.BAD_REQUEST);
        }
        commentService.updateComment(commentId, content);
        return new ResponseEntity<Long>(commentId, HttpStatus.OK);

    }

}
