package com.cultureShop.controller;

import com.cultureShop.API.SigunguExplorer;
import com.cultureShop.config.CustomOAuth2UserService;
import com.cultureShop.dto.*;
import com.cultureShop.dto.ApiDto.SigunguApiDto;
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

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

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

    /* 내주변 메인 페이지 */
    @GetMapping(value = {"", "/{simAddr}"})
    public String placeMain(@PathVariable(required = false) String simAddr, Model model, Principal principal) {

        List<MusArtMainDto> museums;
        List<MusArtMainDto> arts;
        List<MainItemDto> festivals;

        museums = musArtService.getPlaceMainMusArt("museum");
        arts = musArtService.getPlaceMainMusArt("art");
        festivals = itemService.getCategoryItem("festival");

        /* 주소 입력 시 */
        if(simAddr != null) {
            museums = musArtService.getAddrMusArt(simAddr, "museum");
            arts = musArtService.getAddrMusArt(simAddr, "art");
            festivals = itemService.getPlaceFest(simAddr, "festival");
        }

        if(principal != null) { // 로그인된 경우
            String email = customOAuth2UserService.getSocialEmail(principal); // 소셜 로그인일 경우
            if(email == null) { // 이메일 로그인일 경우
                email = principal.getName();
            }

            /* 찜 상품 구분 위함 */
            museums = musArtService.getUserPlaceMainMusArt("museum", email);
            arts = musArtService.getUserPlaceMainMusArt("art", email);
            festivals = itemService.getCategoryItem("festival");

            /* 로그인 된, 주소 입력된 */
            if(simAddr != null) {
                museums = musArtService.getUserAddrMusArt(simAddr, "museum", email);
                arts = musArtService.getUserAddrMusArt(simAddr, "art", email);
                festivals = itemService.getPlaceFest(simAddr, "festival");
            }
        }

        model.addAttribute("museums", museums);
        model.addAttribute("arts", arts);
        model.addAttribute("festivals", festivals);
        model.addAttribute("simAddr", simAddr);

        return "place/placeMain";
    }

    /* 주소 검색 페이지 */
    @GetMapping(value = "/address")
    public String searchAddress(SigunguApiDto sigunguDatas, Model model) {
        /* 공공데이터 api 시군구명 */
        model.addAttribute("sigungus", sigunguDatas);
        return "place/searchAddress";
    }

    /* 주소 검색 */
    @PostMapping(value = "/address")
    public @ResponseBody ResponseEntity searchAddress(@RequestParam String simAddr) {

        /* 전국 시군구명 */
        List<SigunguApiDto> addrList = sigunguExplorer.getSigunguApiDatas();

        List<String> curAddr = new ArrayList<>();

        /* 검색어를 포함한 시군구명만 배열에 추가 */
        for(SigunguApiDto sigunguDto : addrList) {
            String addr = sigunguDto.getSimAddr();
            if(addr.contains(simAddr)) {
                curAddr.add(addr);
            }
        }

        return new ResponseEntity<List<String>>(curAddr, HttpStatus.OK);
    }

    /* 내주변 상세 페이지 */
    @GetMapping(value = "/detail/{placeId}")
    public String placeDtl(@PathVariable Long placeId, Model model, Principal principal) {

        /* 장소 정보 */
        MusArt musArt = musArtRepository.findById(placeId)
                .orElseThrow(EntityNotFoundException::new);
        /* 찜 상품인지 아닌지, 찜 개수 */
        List<UserLikeItem> likePlaces = userLikeItemService.getLikePlaces(placeId);
        int likeCount = likePlaces.size();
        /* 댓글 */
        List<Comment> comments = commentService.getPlaceComments(placeId);

        List<Comment> reCommentList = new ArrayList<>();
        /* 답글 - 댓글 id로 답글찾기 */
        for(Comment comment : comments) {
            List<Comment> reComments = commentService.getReComment(comment.getId());
            reCommentList.addAll(reComments);
        }

        String email;
        if(principal != null) {
            email = customOAuth2UserService.getSocialEmail(principal);
            if(email == null) {
                email = principal.getName();
            }
            /* 리뷰 수정,삭제 권한 */
            model.addAttribute("userEmail", email);

            /* 로그인된 유저의 찜 여부 */
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
        model.addAttribute("reCommentList", reCommentList);

        return "place/placeDtl";
    }

    /* 장소 찜 추가 */
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

    /* 내주변 장소 목록 페이지 */
    @GetMapping(value = {"/list/{type}", "/list/{type}/{simAddr}"})
    public String placeList(@PathVariable("type") String type,
                            @PathVariable(value = "simAddr", required = false) String simAddr, Model model) {

        /* 박물관인지 미술관인지 */
        List<MusArtMainDto> places = musArtService.getAllPlace(type);

        /* 주소가 적용된 박물관, 미술관 목록 */
        if(simAddr != null) {
            places = musArtService.getAllAddrPlace(simAddr, type);
        }
        model.addAttribute("places", places);
        return "place/placeList";
    }

    /* 댓글 작성 */
    @PostMapping(value = "/comment/write")
    public @ResponseBody void commentWrite(@RequestBody CommentDto commentDto) {
        commentService.saveComment(commentDto);
    }

    /* 댓글 삭제 */
    @DeleteMapping(value = "/comment/{commentId}")
    public @ResponseBody ResponseEntity deleteComment(@PathVariable("commentId") Long commentId) {

        try {
            commentService.deleteComment(commentId);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<Long>(commentId, HttpStatus.OK);
    }

    /* 댓글 수정 */
    @PatchMapping(value = "/comment/{commentId}")
    public @ResponseBody ResponseEntity updateComment(@PathVariable("commentId") Long commentId, String content) {

        if(content == null) { // 내용 미작성 시
            return new ResponseEntity<String>("내용을 입력해주세요.", HttpStatus.BAD_REQUEST);
        }
        commentService.updateComment(commentId, content);
        return new ResponseEntity<Long>(commentId, HttpStatus.OK);

    }

    /* 답글 작성 */
    @PostMapping(value = "/recomment/write")
    public @ResponseBody ResponseEntity reCommentWrite(@RequestBody ReCommentDto reCommentDto) {
        try{
            Comment recomment = commentService.saveReComment(reCommentDto);
            return new ResponseEntity<Long>(recomment.getId(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /* 작성된 답글 추가된 페이지 */
    @GetMapping(value = "/recomment/write")
    public @ResponseBody ResponseEntity reCommentWrite(@RequestParam Long commentId) {
        try {
            ReCommentViewDto recomment = commentService.getViewReComment(commentId);
            return new ResponseEntity<ReCommentViewDto>(recomment, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

}
