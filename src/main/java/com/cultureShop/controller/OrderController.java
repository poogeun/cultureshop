package com.cultureShop.controller;

import com.cultureShop.dto.MainItemDto;
import com.cultureShop.dto.OrderFormDto;
import com.cultureShop.entity.Item;
import com.cultureShop.entity.Member;
import com.cultureShop.repository.ItemRepository;
import com.cultureShop.repository.MemberRepository;
import com.cultureShop.service.ItemService;
import com.cultureShop.service.OrderService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.graphql.GraphQlProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;
    private final ItemService itemService;

    @GetMapping(value = "/order")
    public String order(@RequestParam("itemId")Long itemId, @RequestParam("count")int count,
                        @RequestParam("date") LocalDate date, Principal principal, Model model) {

        Member member = memberRepository.findByEmail(principal.getName());
        MainItemDto item = itemRepository.findMainItemDto(itemId);

        model.addAttribute("orderFormDto", new OrderFormDto());
        model.addAttribute("member", member);
        model.addAttribute("item", item);
        model.addAttribute("count", count);
        model.addAttribute("date", date);
        return "order/orderForm";
    }

    @PostMapping(value = "/order")
    public @ResponseBody ResponseEntity order(@RequestBody @Valid OrderFormDto orderFormDto,
                                              BindingResult bindingResult, Principal principal) {

        if(bindingResult.hasErrors()) {
            StringBuilder sb = new StringBuilder();
            List<FieldError> fieldErrorList = bindingResult.getFieldErrors();
            for(FieldError fieldError : fieldErrorList) {
                sb.append(fieldError.getDefaultMessage());
            }
            return new ResponseEntity<String>(sb.toString(), HttpStatus.BAD_REQUEST);
        }

        String email = principal.getName();
        Long orderId;
        try{
            orderId = orderService.order(orderFormDto, email);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<Long>(orderId, HttpStatus.OK);
    }

    @GetMapping(value = "/order/like")
    public String orderLike(@RequestParam("likeChkBox")List<Long> likeItemIds, Principal principal, Model model){

        Member member = memberRepository.findByEmail(principal.getName());
        List<MainItemDto> likeItems = itemService.getOrderLike(likeItemIds);

        model.addAttribute("member", member);
        model.addAttribute("likeItems", likeItems);

        return "order/likeOrderForm";

    }

}
