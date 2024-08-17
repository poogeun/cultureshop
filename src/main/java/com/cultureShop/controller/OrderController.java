package com.cultureShop.controller;

import com.cultureShop.dto.OrderFormDto;
import com.cultureShop.entity.Member;
import com.cultureShop.repository.MemberRepository;
import com.cultureShop.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.time.LocalDate;

@Controller
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final MemberRepository memberRepository;

    @GetMapping(value = "/order")
    public String order(@RequestParam("itemId")Long itemId, @RequestParam("count")int count,
                        @RequestParam("date") LocalDate date, Principal principal, Model model) {

        Member member = memberRepository.findByEmail(principal.getName());

        model.addAttribute("orderFormDto", new OrderFormDto());
        model.addAttribute("member", member);
        model.addAttribute("itemId", itemId);
        model.addAttribute("count", count);
        model.addAttribute("date", date);
        return "order/orderForm";
    }

    @PostMapping(value = "/order")
    public String order(Model model) {

        return "order/payment";
    }
}
