package com.cultureShop.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/my-page")
@Controller
@RequiredArgsConstructor
public class MypageController {

    @GetMapping(value = "")
    public String orderHist() {
        return "mypage/orderHist";
    }
}
