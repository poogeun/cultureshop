package com.cultureShop.controller;

import com.cultureShop.dto.MainItemDto;
import com.cultureShop.dto.ReviewFormDto;
import com.cultureShop.repository.ItemRepository;
import com.cultureShop.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@RequestMapping("/review")
public class ReviewController {

    private final ItemService itemService;
    private final ItemRepository itemRepository;

    @GetMapping(value = "/write/{itemId}")
    public String reviewWrite(@PathVariable("itemId")Long itemId, Model model) {

        MainItemDto item = itemRepository.findMainItemDto(itemId);

        model.addAttribute("reviewFormDto", new ReviewFormDto());
        model.addAttribute("item", item);

        return "review/reviewWrite";
    }
}
