package com.cultureShop.controller;

import com.cultureShop.dto.*;
import com.cultureShop.entity.*;
import com.cultureShop.repository.MemberRepository;
import com.cultureShop.repository.OrderRepository;
import com.cultureShop.repository.UserLikeItemRepository;
import com.cultureShop.repository.UserLikeRepository;
import com.cultureShop.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

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

    @GetMapping(value = {"/orders", "/orders/{page}"})
    public String orderHist(@PathVariable("page")Optional<Integer> page, Principal principal, Model model) {

        Member member = memberRepository.findByEmail(principal.getName());
        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 5);
        Page<OrderHistDto> orderHistDtoList = orderService.getOrderList(principal.getName(), pageable);

        int likeCount = userLikeItemService.likeCount(principal.getName());
        Long orderCount = orderRepository.countOrder(principal.getName());
        int reviewCount = reviewService.getMemReviewCount(principal.getName());

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
