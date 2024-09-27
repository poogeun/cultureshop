package com.cultureShop.controller;

import com.cultureShop.config.CustomOAuth2UserService;
import com.cultureShop.dto.MainItemDto;
import com.cultureShop.dto.ReviewFormDto;
import com.cultureShop.entity.OrderItem;
import com.cultureShop.entity.Review;
import com.cultureShop.repository.ItemRepository;
import com.cultureShop.repository.ReviewRepository;
import com.cultureShop.service.OrderItemService;
import com.cultureShop.service.ReviewService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
@RequestMapping("/review")
public class ReviewController {

    private final ItemRepository itemRepository;
    private final OrderItemService orderItemService;
    private final ReviewService reviewService;
    private final ReviewRepository reviewRepository;
    private final CustomOAuth2UserService customOAuth2UserService;

    /* 리뷰 작성 페이지 */
    @GetMapping(value = "/write/{itemId}")
    public String reviewWrite(@PathVariable("itemId")Long itemId, Principal principal, Model model) {

        String email = customOAuth2UserService.getSocialEmail(principal); // 소셜 로그인일 경우
        if(email == null) { // 이메일 로그인일 경우
            email = principal.getName();
        }
        /* 상품 정보 */
        MainItemDto item = itemRepository.findMainItemDto(itemId);
        /* 관람일 */
        OrderItem orderItem = orderItemService.getOrderItem(itemId, email);

        model.addAttribute("reviewFormDto", new ReviewFormDto());
        model.addAttribute("item", item);
        model.addAttribute("orderItem", orderItem);

        return "review/reviewWrite";
    }

    /* 리뷰 작성 */
    @PostMapping(value = "/write/{itemId}")
    public String reviewWrite(@PathVariable Long itemId, ReviewFormDto reviewFormDto,
                              Model model, Principal principal) {
        String email = customOAuth2UserService.getSocialEmail(principal);
        if(email == null) {
            email = principal.getName();
        }

        MainItemDto item = itemRepository.findMainItemDto(itemId);
        OrderItem orderItem = orderItemService.getOrderItem(itemId, email);

        if(reviewFormDto.getTitle().isEmpty() || reviewFormDto.getContent().isEmpty()) { // 제목, 내용 미입력
            model.addAttribute("errorMessage", "제목과 내용을 입력해주세요.");
        }
        if(reviewFormDto.getStarPoint() == 0) { // 별점 미선택
            model.addAttribute("errorMessage", "별점을 선택해주세요.");
        }

        try {
            /* 리뷰 저장 -> 상세페이지 리뷰탭 페이지로 이동 */
            reviewService.saveReview(reviewFormDto, itemId, email);
            return "redirect:/item/" + itemId + "#detail-review";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "리뷰 등록 중 에러가 발생하였습니다.");
        }
        model.addAttribute("reviewFormDto", new ReviewFormDto());
        model.addAttribute("item", item);
        model.addAttribute("orderItem", orderItem);
        return "review/reviewWrite";
    }

    /* 리뷰 수정 페이지 */
    @GetMapping(value = "/update/{reviewId}")
    public String reviewUpdate(@PathVariable Long reviewId,
                               Principal principal, Model model) {
        String email = customOAuth2UserService.getSocialEmail(principal);
        if(email == null) {
            email = principal.getName();
        }

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(EntityNotFoundException::new);
        Long itemId = review.getItem().getId();
        /* 상품정보 */
        MainItemDto item = itemRepository.findMainItemDto(itemId);
        /* 관람일 */
        OrderItem orderItem = orderItemService.getOrderItem(itemId, email);

        try {
            /* 해당 리뷰 정보 */
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

    /* 리뷰수정 */
    @PostMapping(value = "/update/{reviewId}")
    public String updateReview(@PathVariable Long reviewId, ReviewFormDto reviewFormDto,
                               Principal principal, Model model) {
        String email = customOAuth2UserService.getSocialEmail(principal);
        if(email == null) {
            email = principal.getName();
        }

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(EntityNotFoundException::new);
        Long itemId = review.getItem().getId();
        MainItemDto item = itemRepository.findMainItemDto(itemId);
        OrderItem orderItem = orderItemService.getOrderItem(itemId, email);

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

    /* 리뷰 삭제 */
    @DeleteMapping(value = "/delete/{reviewId}")
    public @ResponseBody ResponseEntity deleteReview(@PathVariable Long reviewId) {
        try {
            reviewService.deleteReview(reviewId);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<String>("리뷰 삭제 중 에러가 발생하였습니다.", HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<Long>(reviewId, HttpStatus.OK);
    }
}
