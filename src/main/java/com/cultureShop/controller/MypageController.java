package com.cultureShop.controller;

import com.cultureShop.config.CustomOAuth2UserService;
import com.cultureShop.dto.*;
import com.cultureShop.entity.*;
import com.cultureShop.repository.MemberRepository;
import com.cultureShop.repository.OrderRepository;
import com.cultureShop.repository.UserLikeItemRepository;
import com.cultureShop.repository.UserLikeRepository;
import com.cultureShop.service.*;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

import org.json.simple.parser.JSONParser;

@RequestMapping("/my-page")
@Controller
@RequiredArgsConstructor
public class MypageController {

    private final OrderService orderService;
    private final MemberRepository memberRepository;
    private final UserLikeItemService userLikeItemService;
    private final UserLikeRepository userLikeRepository;
    private final OrderRepository orderRepository;
    private final ReviewService reviewService;
    private final OrderItemService orderItemService;
    private final CustomOAuth2UserService customOAuth2UserService;

    @GetMapping(value = {"/orders", "/orders/{page}"})
    public String orderHist(@PathVariable("page")Optional<Integer> page, Principal principal, Model model) {

        String email = customOAuth2UserService.getSocialEmail(principal);
        if(email == null) {
            email = principal.getName();
        }

        Member member = memberRepository.findByEmail(email);
        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 5);
        Page<OrderHistDto> orderHistDtoList = orderService.getOrderList(email, pageable);

        int likeCount = userLikeItemService.likeCount(email);
        Long orderCount = orderRepository.countOrder(email);
        int reviewCount = reviewService.getMemReviewCount(email);

        model.addAttribute("member", member);
        model.addAttribute("likeCount", likeCount);
        model.addAttribute("orderCount", orderCount);
        model.addAttribute("reviewCount", reviewCount);
        model.addAttribute("orders", orderHistDtoList);
        model.addAttribute("page", pageable.getPageNumber());
        model.addAttribute("maxPage", 5);

        return "mypage/orderHist";
    }

    @GetMapping(value = "/likes")
    public String likeList(Model model, Principal principal) {

        String email = customOAuth2UserService.getSocialEmail(principal);
        if(email == null) {
            email = principal.getName();
        }

        Member member = memberRepository.findByEmail(email);
        int likeCount = userLikeItemService.likeCount(email);
        Long orderCount = orderRepository.countOrder(email);
        int reviewCount = reviewService.getMemReviewCount(email);

        List<LikeItemDto> likeItems = userLikeItemService.getLikeList(email);
        List<MusArtMainDto> likePlaces = userLikeItemService.getLikePlaceList(email);

        model.addAttribute("member", member);
        model.addAttribute("likeCount", likeCount);
        model.addAttribute("orderCount", orderCount);
        model.addAttribute("reviewCount", reviewCount);
        model.addAttribute("likeItems", likeItems);
        model.addAttribute("likePlaces", likePlaces);

        return "mypage/likeList";
    }

    @GetMapping(value = "/reviews")
    public String reviewList(Model model, Principal principal) {

        String email = customOAuth2UserService.getSocialEmail(principal);
        if(email == null) {
            email = principal.getName();
        }

        Member member = memberRepository.findByEmail(email);
        int likeCount = userLikeItemService.likeCount(email);
        Long orderCount = orderRepository.countOrder(email);
        int reviewCount = reviewService.getMemReviewCount(email);
        List<ReviewItemDto> reviews = reviewService.getMemReview(email);
        List<MainItemDto> noRevItems = orderItemService.getNoRevOrderItems(email);

        model.addAttribute("member", member);
        model.addAttribute("likeCount", likeCount);
        model.addAttribute("orderCount", orderCount);
        model.addAttribute("reviewCount", reviewCount);
        model.addAttribute("reviews", reviews);
        model.addAttribute("noRevItems", noRevItems);

        return "mypage/reviewList";
    }
}
