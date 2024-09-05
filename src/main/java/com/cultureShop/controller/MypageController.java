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

        String email = principal.getName();
        String emailPattern = "^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*.[a-zA-Z]{2,3}$";
        if(!Pattern.matches(emailPattern, principal.getName())) {
            email = customOAuth2UserService.getSocialEmail(principal);
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

        Member member = memberRepository.findByEmail(principal.getName());
        int likeCount = userLikeItemService.likeCount(principal.getName());
        Long orderCount = orderRepository.countOrder(principal.getName());
        int reviewCount = reviewService.getMemReviewCount(principal.getName());

        List<LikeItemDto> likeItems = userLikeItemService.getLikeList(principal.getName());
        List<MusArtMainDto> likePlaces = userLikeItemService.getLikePlaceList(principal.getName());

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

        Member member = memberRepository.findByEmail(principal.getName());
        int likeCount = userLikeItemService.likeCount(principal.getName());
        Long orderCount = orderRepository.countOrder(principal.getName());
        int reviewCount = reviewService.getMemReviewCount(principal.getName());
        List<ReviewItemDto> reviews = reviewService.getMemReview(principal.getName());
        List<MainItemDto> noRevItems = orderItemService.getNoRevOrderItems(principal.getName());

        model.addAttribute("member", member);
        model.addAttribute("likeCount", likeCount);
        model.addAttribute("orderCount", orderCount);
        model.addAttribute("reviewCount", reviewCount);
        model.addAttribute("reviews", reviews);
        model.addAttribute("noRevItems", noRevItems);

        return "mypage/reviewList";
    }
}
