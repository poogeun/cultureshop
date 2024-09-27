package com.cultureShop.controller;

import com.cultureShop.config.CustomOAuth2UserService;
import com.cultureShop.dto.ItemFormDto;
import com.cultureShop.dto.ItemSearchDto;
import com.cultureShop.dto.LikeDto;
import com.cultureShop.dto.MainItemDto;
import com.cultureShop.entity.*;
import com.cultureShop.service.ItemService;
import com.cultureShop.service.OrderItemService;
import com.cultureShop.service.ReviewService;
import com.cultureShop.service.UserLikeItemService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;
    private final UserLikeItemService userLikeItemService;
    private final ReviewService reviewService;
    private final OrderItemService orderItemService;
    private final CustomOAuth2UserService customOAuth2UserService;

    /* 상품 등록 페이지 */
    @GetMapping(value = "/admin/item/new")
    public String itemForm(Model model){
        model.addAttribute("itemFormDto", new ItemFormDto());
        return "/item/itemForm";
    }

    /* 상품 등록 */
    @PostMapping(value = "/admin/item/new")
    public String itemNew(@Valid ItemFormDto itemFormDto, BindingResult bindingResult, Model model,
                          @RequestParam("itemImgFile") List<MultipartFile> itemImgFileList) {

        if(bindingResult.hasErrors()) {
            return "item/itemForm";
        }
        if(itemImgFileList.getFirst().isEmpty() && itemFormDto.getId() == null) {
            model.addAttribute("errorMessage", "첫번째 상품 이미지는 필수 입력 값입니다.");
            return "item/itemForm";
        }

        try{
            itemService.saveItem(itemFormDto, itemImgFileList);
        } catch (Exception e) {
            model.addAttribute("errorMessage", "상품 등록 중 에러가 발생하였습니다.");
            return "item/itemForm";
        }
        return "redirect:/admin/item/new";
    }

    /* 상품 수정 페이지 */
    @GetMapping(value = "/admin/item/{itemId}")
    public String itemUpdate(@PathVariable("itemId") Long itemId, Model model){
        try{
            /* 수정할 상품 정보 */
            ItemFormDto itemFormDto = itemService.getItemDtl(itemId);
            model.addAttribute("itemFormDto", itemFormDto);
        } catch (EntityNotFoundException e) {
            model.addAttribute("errorMessage", "존재하지 않는 상품입니다.");
            model.addAttribute("itemFormDto", new ItemFormDto());
            return "item/itemForm";
        }

        return "item/itemForm";
    }

    /* 상품 수정 */
    @PostMapping(value = "/admin/item/{itemId}")
    public String itemUpdate(@Valid ItemFormDto itemFormDto, BindingResult bindingResult,
                             @RequestParam("itemImgFile") List<MultipartFile> itemImgFileList,
                             Model model) {
        if(bindingResult.hasErrors()){
            return "item/itemForm";
        }
        if(itemImgFileList.getFirst().isEmpty() && itemFormDto.getId() == null){
            model.addAttribute("errorMessage", "첫번째 상품 이미지는 필수 입력 값입니다.");
            return "item/itemForm";
        }
        try{
            itemService.updateItem(itemFormDto, itemImgFileList);
        } catch (Exception e) {
            model.addAttribute("errorMessage", "상품 수정 중 에러가 발생하였습니다.");
            return "item/itemForm";
        }
        return "redirect:/admin/items";
    }

    /* 관리자 상품 목록 페이지 */
    @GetMapping(value = {"/admin/items", "/admin/items/{page}"})
    public String itemManage(ItemSearchDto itemSearchDto, @PathVariable("page") Optional<Integer> page,
                             Model model) {
        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 10);

        /* itemSearchDto의 검색어, 검색조건에 맞는 상품목록 - QueryDSL */
        Page<Item> items = itemService.getAdminItemPage(itemSearchDto, pageable);

        model.addAttribute("items", items);
        model.addAttribute("itemSearchDto", itemSearchDto);
        model.addAttribute("maxPage", 5);

        return "item/itemMng";
    }

    /* 상품 상세 페이지 */
    @GetMapping(value = "/item/{itemId}")
    public String itemDtl(@PathVariable("itemId")Long itemId, Model model, Principal principal){

        /* 로그인된 경우 */
        if(principal != null) {
            String email = customOAuth2UserService.getSocialEmail(principal); // 소셜 로그인일 경우
            if(email == null) { // 이메일 로그인일 경우
                email = principal.getName();
            }

            /* 찜한 상품인지 아닌지 */
            if (userLikeItemService.findLikeItem(email, itemId)) {
                model.addAttribute("isLikeItem", "afterLike");
            }
            else {
                model.addAttribute("isLikeItem", "beforeLike");
            }

            /* 구매한 상품이면 리뷰작성 가능 */
            OrderItem orderItem = orderItemService.getOrderItem(itemId, email);
            /* 내가 작성한 리뷰이면 수정 가능 */
            Review memReview = reviewService.getMemItemReview(itemId, email);

            model.addAttribute("orderItem", orderItem);
            model.addAttribute("memReview", memReview);
        }
        else {
            model.addAttribute("orderItem", null);
            model.addAttribute("memReview", null);
        }

        ItemFormDto itemFormDto = itemService.getItemDtl(itemId);
        LocalDate today = LocalDate.now();
        List<UserLikeItem> likeItems = userLikeItemService.getLikeItems(itemId);
        int likeCount = likeItems.size();
        List<Review> reviews = reviewService.getItemReview(itemId);
        int reviewCount = reviews.size();

        model.addAttribute("item", itemFormDto);
        model.addAttribute("today", today);
        model.addAttribute("likeItems", likeItems);
        model.addAttribute("likeCount", likeCount);
        model.addAttribute("reviews", reviews);
        model.addAttribute("reviewCount", reviewCount);

        return "item/itemDtl";
    }

    /* 찜 추가 */
    @PostMapping(value = "/like")
    public @ResponseBody ResponseEntity addLike(@RequestBody LikeDto likeDto, Principal principal) {

        if(principal != null) {
            String email = customOAuth2UserService.getSocialEmail(principal);
            if(email == null) {
                email = principal.getName();
            }
            userLikeItemService.addLike(email, likeDto.getItemId());
            return new ResponseEntity<Long>(likeDto.getItemId(), HttpStatus.OK);
        }
        else {
            return new ResponseEntity<String>("찜 기능은 로그인 후 이용 가능합니다.", HttpStatus.BAD_REQUEST);
        }
    }

    /* 상품 목록 페이지
    * 카테고리, 정렬기준, 주소필터값을 받아서 QueryDsl 활용
    */
    @GetMapping(value = "/item/category/{category}")
    public String category(@PathVariable("category") String category,
                           @RequestParam(value = "sort", required = false) String sort,
                           @RequestParam(value = "address", required = false) String address,
                           Model model) {
        Pageable pageable = PageRequest.of(0, 20);

        Long count;
        Page<MainItemDto> items;

        /* 카테고리, 정렬기준(sort), 주소필터(address)가 적용된 상품목록 */
        items = itemService.getAllCategoryItem(category, address, sort, pageable);

        count = itemService.getCateItemCount(category, address, sort); // 총 상품개수

        model.addAttribute("items", items);
        model.addAttribute("category", category);
        model.addAttribute("sort", sort);
        model.addAttribute("address", address);
        model.addAttribute("count", count);

        return "item/itemCategory";
    }

    /* 상품목록 무한스크롤 */
    @PostMapping(value = "/item/category/{category}")
    public @ResponseBody ResponseEntity infinityScroll(@PathVariable String category,
                                                       @RequestParam(value = "address", required = false)String address,
                                                       @RequestParam(value = "sort", required = false) String sort,
                                                       @RequestParam("page")int page, @RequestParam("limit")int limit) {
        Pageable pageable = PageRequest.of(page, limit);
        try {
            /* 카테고리, 정렬기준(sort), 주소필터(address)가 적용된 상품목록 */
            Page<MainItemDto> items = itemService.getAllCategoryItem(category, address, sort, pageable);
            List<MainItemDto> itemList = items.getContent();
            return new ResponseEntity<List<MainItemDto>>(itemList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

}
