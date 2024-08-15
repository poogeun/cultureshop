package com.cultureShop.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/review")
public class ReviewController {

    @GetMapping(value = "/write")
    public String reviewWrite() {
        return "review/reviewWrite";
    }
}
