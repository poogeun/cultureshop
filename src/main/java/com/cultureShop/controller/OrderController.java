package com.cultureShop.controller;

import com.cultureShop.config.CustomOAuth2UserService;
import com.cultureShop.dto.*;
import com.cultureShop.entity.Member;
import com.cultureShop.entity.Order;
import com.cultureShop.entity.UserLikeItem;
import com.cultureShop.repository.ItemRepository;
import com.cultureShop.repository.MemberRepository;
import com.cultureShop.repository.OrderRepository;
import com.cultureShop.service.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    private final CustomOAuth2UserService customOAuth2UserService;
    private final OrderItemService orderItemService;

    /* 바로 주문 페이지 */
    @GetMapping(value = "/order")
    public String order(@RequestParam("itemId")Long itemId, @RequestParam("count")int count,
                        @RequestParam(value = "date", required = false) LocalDate date, Principal principal, Model model) {

        String email = customOAuth2UserService.getSocialEmail(principal); // 소셜 로그인일 경우
        if(email == null) { // 이메일 로그인일 경우
            email = principal.getName();
        }

        if(date == null) { // 관람일 미입력 시
            ItemFormDto itemFormDto = itemService.getItemDtl(itemId);
            List<UserLikeItem> likeItems = userLikeItemService.getLikeItems(itemId);
            int likeCount = likeItems.size();

            model.addAttribute("item", itemFormDto);
            model.addAttribute("likeItems", likeItems);
            model.addAttribute("likeCount", likeCount);
            model.addAttribute("errorMessage", "관람일을 선택해주세요.");
            return "item/itemDtl";
        }
        Member member = memberRepository.findByEmail(email);
        MainItemDto item = itemRepository.findMainItemDto(itemId);

        model.addAttribute("orderFormDto", new OrderFormDto());
        model.addAttribute("member", member);
        model.addAttribute("item", item);
        model.addAttribute("count", count);
        model.addAttribute("date", date);
        return "order/orderForm";
    }

    /* 바로 주문 */
    @PostMapping(value = "/order")
    public @ResponseBody ResponseEntity order(@RequestBody @Valid OrderFormDto orderFormDto,
                                              BindingResult bindingResult, Principal principal) {
        String email = customOAuth2UserService.getSocialEmail(principal);
        if(email == null) {
            email = principal.getName();
        }

        /* 주소, 전화번호 유효성 검사 */
        if (bindingResult.hasErrors()) {
            StringBuilder sb = new StringBuilder();
            List<FieldError> fieldErrorList = bindingResult.getFieldErrors();
            for (FieldError fieldError : fieldErrorList) {
                sb.append(fieldError.getDefaultMessage());
            }
            return new ResponseEntity<String>(sb.toString(), HttpStatus.BAD_REQUEST);
        }

        String orderUid; // 주문고유번호
        try {
            /* 주문서 저장, 결제처리를 위한 주문고유번호 전달 */
            Long orderId = orderService.order(orderFormDto, email);
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(EntityNotFoundException::new);
            orderUid = order.getOrderUid();
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<String>(orderUid, HttpStatus.OK);
    }

    /* 찜상품(선택) 주문 페이지 */
    @GetMapping(value = "/order/like")
    public String orderLike(@RequestParam("likeChkBox")List<Long> likeItemIds, Principal principal, Model model){

        String email = customOAuth2UserService.getSocialEmail(principal);
        if(email == null) {
            email = principal.getName();
        }

        Member member = memberRepository.findByEmail(email);
        /* 선택 상품 리스트 */
        List<LikeItemDto> likeItems = userLikeItemService.getOrderLike(likeItemIds);
        LocalDate today = LocalDate.now();

        model.addAttribute("member", member);
        model.addAttribute("likeItems", likeItems);
        model.addAttribute("today", today);

        return "order/likeOrderForm";

    }

    /* 찜상품(선택) 주문 */
    @PostMapping(value = "/order/like")
    public @ResponseBody ResponseEntity orderLikeItem(@RequestBody LikeOrderFormDto likeOrderFormDto,
                                                      Principal principal) {
        String email = customOAuth2UserService.getSocialEmail(principal);
        if(email == null) {
            email = principal.getName();
        }

        List<LikeOrderFormDto> likeOrderFormDtoList = likeOrderFormDto.getLikeOrderFormDtoList();
        for(LikeOrderFormDto likeOrderForm : likeOrderFormDtoList) { // 주문상품만큼
            /* 관람일, 주소 미입력 시 */
            if(likeOrderForm.getViewDay() == null) {
                return new ResponseEntity<String>("관람일을 선택해주세요.", HttpStatus.FORBIDDEN);
            }
            if(likeOrderForm.getAddress() == null) {
                return new ResponseEntity<String>("주소를 입력해주세요.", HttpStatus.FORBIDDEN);
            }
        }

        String orderUid; // 주문고유번호
        try {
            /* 주문서 저장, 결제처리를 위한 주문고유번호 전달 */
            Long orderId = userLikeService.orderLikeItem(likeOrderFormDtoList, email);
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(EntityNotFoundException::new);
            orderUid = order.getOrderUid();
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<String>(orderUid, HttpStatus.OK);
    }

    /* 주문 상세 페이지 */
    @GetMapping(value = "/order/success/{orderId}")
    public String orderSuccess(@PathVariable Long orderId, Model model, Principal principal) {
        String email = customOAuth2UserService.getSocialEmail(principal);
        if(email == null) {
            email = principal.getName();
        }
        Member member = memberRepository.findByEmail(email);
        /* 주문서 */
        Order order = orderRepository.findById(orderId)
                .orElseThrow(EntityNotFoundException::new);
        /* 주문 상품 리스트 */
        List<OrderItemDto> orderItems = orderItemService.getOrderSuccess(orderId);

        model.addAttribute("member", member);
        model.addAttribute("order", order);
        model.addAttribute("orderItems", orderItems);
        return "order/orderSuccess";
    }

    /* 주문 성공 페이지 */
    @GetMapping(value = "/order/success")
    public String orderSuccess(Model model, Principal principal) {
        String email = customOAuth2UserService.getSocialEmail(principal);
        if(email == null) {
            email = principal.getName();
        }
        Member member = memberRepository.findByEmail(email);
        /* 주문서 */
        Order order = orderRepository.findByMemberLate(email);
        /* 주문 상품 리스트 */
        List<OrderItemDto> orderItems = orderItemService.getOrderSuccess(order.getId());

        model.addAttribute("member", member);
        model.addAttribute("order", order);
        model.addAttribute("orderItems", orderItems);
        return "order/orderSuccess";
    }

    // 주문 취소
    @PostMapping(value = "/order/{orderId}/cancel")
    public @ResponseBody ResponseEntity cancelOrder(@PathVariable Long orderId, Principal principal) {

        String email = customOAuth2UserService.getSocialEmail(principal);
        if(email == null) {
            email = principal.getName();
        }
        /* 주문서 멤버가 아니면 */
        if(!orderService.validateOrder(orderId, email)) {
            return new ResponseEntity<String>("주문 취소 권한이 없습니다.", HttpStatus.FORBIDDEN);
        }
        /* 주문서 취소 */
        orderService.cancelOrder(orderId);

        return new ResponseEntity<Long>(orderId, HttpStatus.OK);
    }
}
