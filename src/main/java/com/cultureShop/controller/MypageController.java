package com.cultureShop.controller;

import com.cultureShop.dto.OrderHistDto;
import com.cultureShop.entity.Member;
import com.cultureShop.entity.Order;
import com.cultureShop.entity.UserLike;
import com.cultureShop.entity.UserLikeItem;
import com.cultureShop.repository.MemberRepository;
import com.cultureShop.repository.OrderRepository;
import com.cultureShop.repository.UserLikeItemRepository;
import com.cultureShop.repository.UserLikeRepository;
import com.cultureShop.service.OrderService;
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
    private final UserLikeItemRepository userLikeItemRepository;
    private final UserLikeRepository userLikeRepository;
    private final OrderRepository orderRepository;

    @GetMapping(value = {"/orders", "/orders/{page}"})
    public String orderHist(@PathVariable("page")Optional<Integer> page, Principal principal, Model model) {

        Member member = memberRepository.findByEmail(principal.getName());
        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 5);
        Page<OrderHistDto> orderHistDtoList = orderService.getOrderList(principal.getName(), pageable);

        UserLike userLike = userLikeRepository.findByMemberId(member.getId());
        int likeCount = 0;
        if(userLike != null) {
            List<UserLikeItem> userLikeItems = userLikeItemRepository.findByUserLikeId(userLike.getId());
            if(userLikeItems != null) {
                likeCount = userLikeItems.size();
            }
        }

        Long orderCount = orderRepository.countOrder(principal.getName());

        model.addAttribute("member", member);
        model.addAttribute("likeCount", likeCount);
        model.addAttribute("orderCount", orderCount);
        model.addAttribute("orders", orderHistDtoList);
        model.addAttribute("page", pageable.getPageNumber());
        model.addAttribute("maxPage", 5);

        return "mypage/orderHist";
    }
}
