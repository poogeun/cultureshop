package com.cultureShop.controller;

import com.cultureShop.dto.*;
import com.cultureShop.entity.Item;
import com.cultureShop.entity.Member;
import com.cultureShop.entity.Order;
import com.cultureShop.entity.UserLikeItem;
import com.cultureShop.repository.ItemRepository;
import com.cultureShop.repository.MemberRepository;
import com.cultureShop.repository.OrderRepository;
import com.cultureShop.service.ItemService;
import com.cultureShop.service.OrderService;
import com.cultureShop.service.UserLikeItemService;
import com.cultureShop.service.UserLikeService;
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
    private final UserLikeItemService userLikeItemService;
    private final UserLikeService userLikeService;
    private final OrderRepository orderRepository;

    @GetMapping(value = "/order")
    public String order(@RequestParam("itemId")Long itemId, @RequestParam("count")int count,
                        @RequestParam(value = "date", required = false) LocalDate date, Principal principal, Model model) {

        if(date == null) {
            ItemFormDto itemFormDto = itemService.getItemDtl(itemId);
            List<UserLikeItem> likeItems = userLikeItemService.getLikeItems(itemId);
            int likeCount = likeItems.size();

            model.addAttribute("item", itemFormDto);
            model.addAttribute("likeItems", likeItems);
            model.addAttribute("likeCount", likeCount);
            model.addAttribute("errorMessage", "관람일을 선택해주세요.");
            return "item/itemDtl";
        }
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

        if (bindingResult.hasErrors()) {
            StringBuilder sb = new StringBuilder();
            List<FieldError> fieldErrorList = bindingResult.getFieldErrors();
            for (FieldError fieldError : fieldErrorList) {
                sb.append(fieldError.getDefaultMessage());
            }
            return new ResponseEntity<String>(sb.toString(), HttpStatus.BAD_REQUEST);
        }

        String email = principal.getName();
        String orderUid;
        try {
            Long orderId = orderService.order(orderFormDto, email);
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(EntityNotFoundException::new);
            orderUid = order.getOrderUid();
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<String>(orderUid, HttpStatus.OK);
    }

    @GetMapping(value = "/order/like")
    public String orderLike(@RequestParam("likeChkBox")List<Long> likeItemIds, Principal principal, Model model){

        Member member = memberRepository.findByEmail(principal.getName());
        List<LikeItemDto> likeItems = userLikeItemService.getOrderLike(likeItemIds);
        LocalDate today = LocalDate.now();

        model.addAttribute("member", member);
        model.addAttribute("likeItems", likeItems);
        model.addAttribute("today", today);

        return "order/likeOrderForm";

    }


    @PostMapping(value = "/order/like")
    public @ResponseBody ResponseEntity orderLikeItem(@RequestBody LikeOrderFormDto likeOrderFormDto,
                                                      Principal principal) {
        List<LikeOrderFormDto> likeOrderFormDtoList = likeOrderFormDto.getLikeOrderFormDtoList();

        for(LikeOrderFormDto likeOrderForm : likeOrderFormDtoList) {
            if(likeOrderForm.getViewDay() == null) {
                return new ResponseEntity<String>("관람일을 선택해주세요.", HttpStatus.FORBIDDEN);
            }
            if(likeOrderForm.getAddress() == null) {
                return new ResponseEntity<String>("주소를 입력해주세요.", HttpStatus.FORBIDDEN);
            }
        }

        String orderUid;
        try {
            Long orderId = userLikeService.orderLikeItem(likeOrderFormDtoList, principal.getName());
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(EntityNotFoundException::new);
            orderUid = order.getOrderUid();
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<String>(orderUid, HttpStatus.OK);
    }

    @GetMapping(value = "/order/success")
    public String orderSuccess() {
        return "order/orderSuccess";
    }

    @PostMapping(value = "/order/{orderId}/cancel")
    public @ResponseBody ResponseEntity cancelOrder(@PathVariable Long orderId, Principal principal) {

        if(!orderService.validateOrder(orderId, principal.getName())) {
            return new ResponseEntity<String>("주문 취소 권한이 없습니다.", HttpStatus.FORBIDDEN);
        }
        orderService.cancelOrder(orderId);

        return new ResponseEntity<Long>(orderId, HttpStatus.OK);
    }

}
