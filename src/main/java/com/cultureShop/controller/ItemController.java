package com.cultureShop.controller;

import com.cultureShop.dto.ItemFormDto;
import com.cultureShop.dto.ItemSearchDto;
import com.cultureShop.dto.LikeDto;
import com.cultureShop.dto.MainItemDto;
import com.cultureShop.entity.*;
import com.cultureShop.repository.MemberRepository;
import com.cultureShop.service.ItemService;
import com.cultureShop.service.OrderItemService;
import com.cultureShop.service.ReviewService;
import com.cultureShop.service.UserLikeItemService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
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
    private final MemberRepository memberRepository;
    private final ReviewService reviewService;
    private final OrderItemService orderItemService;

    @GetMapping(value = "/admin/item/new")
    public String itemForm(Model model, HttpServletRequest request){

        CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        model.addAttribute("_csrf", csrfToken);
        model.addAttribute("itemFormDto", new ItemFormDto());
        return "/item/itemForm";
    }

    @PostMapping(value = "/admin/item/new")
    public String itemNew(@Valid ItemFormDto itemFormDto, BindingResult bindingResult, Model model,
                          @RequestParam("itemImgFile") List<MultipartFile> itemImgFileList, HttpServletRequest request) {

        CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        model.addAttribute("_csrf", csrfToken);

        if(bindingResult.hasErrors()){
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

    @GetMapping(value = "/admin/item/{itemId}")
    public String itemUpdate(@PathVariable("itemId") Long itemId, Model model){
        try{
            ItemFormDto itemFormDto = itemService.getItemDtl(itemId);
            model.addAttribute("itemFormDto", itemFormDto);
        } catch (EntityNotFoundException e) {
            model.addAttribute("errorMessage", "존재하지 않는 상품입니다.");
            model.addAttribute("itemFormDto", new ItemFormDto());
            return "item/itemForm";
        }

        return "item/itemForm";
    }

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

    @GetMapping(value = {"/admin/items", "/admin/items/{page}"})
    public String itemManage(ItemSearchDto itemSearchDto, @PathVariable("page") Optional<Integer> page,
                             Model model) {
        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 10);
        Page<Item> items = itemService.getAdminItemPage(itemSearchDto, pageable);


        model.addAttribute("items", items);
        model.addAttribute("itemSearchDto", itemSearchDto);
        model.addAttribute("maxPage", 5);

        return "item/itemMng";
    }

    @GetMapping(value = "/item/{itemId}")
    public String itemDtl(@PathVariable("itemId")Long itemId, Model model, Principal principal){

        ItemFormDto itemFormDto = itemService.getItemDtl(itemId);
        LocalDate today = LocalDate.now();
        List<UserLikeItem> likeItems = userLikeItemService.getLikeItems(itemId);
        int likeCount = likeItems.size();

        if(principal != null) {
            if (userLikeItemService.findLikeItem(principal.getName(), itemId)) {
                model.addAttribute("isLikeItem", "afterLike");
            }
            else {
                model.addAttribute("isLikeItem", "beforeLike");
            }

            OrderItem orderItem = orderItemService.getOrderItem(itemId, principal.getName());
            Review memReview = reviewService.getMemItemReview(itemId, principal.getName());
            model.addAttribute("orderItem", orderItem);
            model.addAttribute("memReview", memReview);
        }
        else {
            model.addAttribute("orderItem", null);
            model.addAttribute("memReview", null);
        }

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

    @PostMapping(value = "/like") // 찜 기능
    public @ResponseBody ResponseEntity addLike(@RequestBody LikeDto likeDto, Principal principal, Model model) {

        if(principal != null) {
            userLikeItemService.addLike(principal.getName(), likeDto.getItemId());
            return new ResponseEntity<Long>(likeDto.getItemId(), HttpStatus.OK);
        }
        else {
            return new ResponseEntity<String>("찜 기능은 로그인 후 이용 가능합니다.", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/item/category/{category}")
    public String category(@PathVariable("category") String category,
                           @RequestParam(value = "sort", required = false) String sort,
                           Model model) {
        Pageable pageable = PageRequest.of(0, 20);

        Long count;
        Page<MainItemDto> items;
        if(category.equals("exhibition")){
            items = itemService.getAllCategoryItem(category, pageable);

        }
        else if(category.equals("museum")){
            items = itemService.getAllCategoryItem(category, pageable);
        }
        else {
            items = itemService.getAllCategoryItem(category, pageable);
        }
        count = items.getTotalElements();

        if(sort != null) {
            if(sort.equals("endDay")) {
                items = itemService.getEndDayCategoryItem(category, pageable);
            }
            else if(sort.equals("review")) {
                items = itemService.getReviewCategoryItem(category, pageable);
            }
        }

        model.addAttribute("items", items);
        model.addAttribute("category", category);
        model.addAttribute("sort", sort);
        model.addAttribute("count", count);

        return "menu/exhibition";
    }

    @PostMapping(value = "/item/category/{category}")
    public @ResponseBody ResponseEntity infinityScroll(@PathVariable String category,
                                                       @RequestParam("page")int page,
                                                       @RequestParam("limit")int limit) {
        Pageable pageable = PageRequest.of(page, limit);
        try {
            Page<MainItemDto> items = itemService.getAllCategoryItem(category, pageable);
            List<MainItemDto> itemList = items.getContent();

            return new ResponseEntity<List<MainItemDto>>(itemList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

}
