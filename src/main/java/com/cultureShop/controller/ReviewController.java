package com.cultureShop.controller;

import com.cultureShop.dto.MainItemDto;
import com.cultureShop.dto.ReviewFormDto;
import com.cultureShop.entity.Item;
import com.cultureShop.entity.OrderItem;
import com.cultureShop.entity.Review;
import com.cultureShop.repository.ItemRepository;
import com.cultureShop.repository.ReviewRepository;
import com.cultureShop.service.ItemService;
import com.cultureShop.service.OrderItemService;
import com.cultureShop.service.ReviewService;
import jakarta.persistence.EntityNotFoundException;
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
    private final ReviewRepository reviewRepository;

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
    public String reviewWrite(@PathVariable Long itemId, ReviewFormDto reviewFormDto,
                              Model model, Principal principal) {
        MainItemDto item = itemRepository.findMainItemDto(itemId);
        OrderItem orderItem = orderItemService.getOrderItem(itemId, principal.getName());

        if(reviewFormDto.getTitle().isEmpty() || reviewFormDto.getContent().isEmpty()) {
            model.addAttribute("errorMessage", "제목과 내용을 입력해주세요.");
        }
        if(reviewFormDto.getStarPoint() == 0) {
            model.addAttribute("errorMessage", "별점을 선택해주세요.");
        }

        try {
            reviewService.saveReview(reviewFormDto, itemId, principal.getName());
            return "redirect:/item/" + itemId + "#detail-review";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "리뷰 등록 중 에러가 발생하였습니다.");
        }
        model.addAttribute("reviewFormDto", new ReviewFormDto());
        model.addAttribute("item", item);
        model.addAttribute("orderItem", orderItem);
        return "review/reviewWrite";
    }

    @GetMapping(value = "/update/{reviewId}")
    public String reviewUpdate(@PathVariable Long reviewId,
                               Principal principal, Model model) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(EntityNotFoundException::new);
        Long itemId = review.getItem().getId();
        MainItemDto item = itemRepository.findMainItemDto(itemId);
        OrderItem orderItem = orderItemService.getOrderItem(itemId, principal.getName());

        try {
            ReviewFormDto reviewFormDto = reviewService.getReviewForm(reviewId);
            model.addAttribute("reviewFormDto", reviewFormDto);

        } catch (EntityNotFoundException e) {
            model.addAttribute("reviewFormDto", new ReviewFormDto());
            model.addAttribute("errorMessage", "존재하지 않는 리뷰입니다.");
        }

        model.addAttribute("item", item);
        model.addAttribute("orderItem", orderItem);
        return "review/reviewWrite";
    }

    @PostMapping(value = "/update/{reviewId}")
    public String updateReview(@PathVariable Long reviewId, ReviewFormDto reviewFormDto,
                               Principal principal, Model model, HttpServletRequest request) {
        CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        model.addAttribute("_csrf", csrfToken);

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(EntityNotFoundException::new);
        Long itemId = review.getItem().getId();

        MainItemDto item = itemRepository.findMainItemDto(itemId);
        OrderItem orderItem = orderItemService.getOrderItem(itemId, principal.getName());

        if(reviewFormDto.getTitle().isEmpty() || reviewFormDto.getContent().isEmpty()) {
            model.addAttribute("errorMessage", "제목과 내용을 입력해주세요.");
        }
        if(reviewFormDto.getStarPoint() == 0) {
            model.addAttribute("errorMessage", "별점을 선택해주세요.");
        }

        try {
            reviewService.updateReview(reviewId, reviewFormDto);
            return "redirect:/item/" + itemId + "#detail-review";
        } catch (EntityNotFoundException e) {
            model.addAttribute("errorMessage", "리뷰 등록 중 에러가 발생하였습니다.");
        }

        model.addAttribute("reviewFormDto", new ReviewFormDto());
        model.addAttribute("item", item);
        model.addAttribute("orderItem", orderItem);
        return "review/reviewWrite";
    }

}
