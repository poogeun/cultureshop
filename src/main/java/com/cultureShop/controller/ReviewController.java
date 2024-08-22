package com.cultureShop.controller;

import com.cultureShop.dto.MainItemDto;
import com.cultureShop.dto.ReviewFormDto;
import com.cultureShop.entity.OrderItem;
import com.cultureShop.repository.ItemRepository;
import com.cultureShop.service.ItemService;
import com.cultureShop.service.OrderItemService;
import com.cultureShop.service.ReviewService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
@RequestMapping("/review")
public class ReviewController {

    private final ItemService itemService;
    private final ItemRepository itemRepository;
    private final OrderItemService orderItemService;
    private final ReviewService reviewService;

    @GetMapping(value = "/write/{itemId}")
    public String reviewWrite(@PathVariable("itemId")Long itemId, Principal principal, Model model) {

        MainItemDto item = itemRepository.findMainItemDto(itemId);

        OrderItem orderItem = orderItemService.getOrderItem(itemId, principal.getName());

        model.addAttribute("reviewFormDto", new ReviewFormDto());
        model.addAttribute("item", item);
        model.addAttribute("orderItem", orderItem);

        return "review/reviewWrite";
    }

    @PostMapping(value = "/write/{itemId}")
    public String reviewWrite(@Valid ReviewFormDto reviewFormDto, @PathVariable Long itemId,
                              BindingResult bindingResult, Model model, Principal principal) {

        if(bindingResult.hasErrors()){
            MainItemDto item = itemRepository.findMainItemDto(itemId);
            OrderItem orderItem = orderItemService.getOrderItem(itemId, principal.getName());

            model.addAttribute("reviewFormDto", new ReviewFormDto());
            model.addAttribute("item", item);
            model.addAttribute("orderItem", orderItem);
            return "review/reviewWrite";
        }
        if(reviewFormDto.getStarPoint() == 0) {
            MainItemDto item = itemRepository.findMainItemDto(itemId);
            OrderItem orderItem = orderItemService.getOrderItem(itemId, principal.getName());

            model.addAttribute("errorMessage", "별점을 선택해주세요.");
            model.addAttribute("reviewFormDto", new ReviewFormDto());
            model.addAttribute("item", item);
            model.addAttribute("orderItem", orderItem);
            return "review/reviewWrite";
        }

        try {
            reviewService.saveReview(reviewFormDto);
        } catch (Exception e) {
            MainItemDto item = itemRepository.findMainItemDto(itemId);
            OrderItem orderItem = orderItemService.getOrderItem(itemId, principal.getName());

            model.addAttribute("errorMessage", "리뷰 등록 중 에러가 발생하였습니다.");
            model.addAttribute("reviewFormDto", new ReviewFormDto());
            model.addAttribute("item", item);
            model.addAttribute("orderItem", orderItem);
            return "review/reviewWrite";
        }

        return "redirect:/item/" + itemId + "#detail-review";
    }

}
