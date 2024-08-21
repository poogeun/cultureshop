package com.cultureShop.service;

import com.cultureShop.dto.LikeOrderFormDto;
import com.cultureShop.dto.OrderFormDto;
import com.cultureShop.entity.UserLikeItem;
import com.cultureShop.repository.ItemRepository;
import com.cultureShop.repository.MemberRepository;
import com.cultureShop.repository.UserLikeItemRepository;
import com.cultureShop.repository.UserLikeRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserLikeService {

    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;
    private final UserLikeRepository userLikeRepository;
    private final UserLikeItemRepository userLikeItemRepository;
    private final OrderService orderService;

    public Long orderLikeItem(List<LikeOrderFormDto> likeOrderDtoList, String email) {
        List<OrderFormDto> orderFormDtoList = new ArrayList<>();

        for(LikeOrderFormDto likeOrderDto : likeOrderDtoList) {
            OrderFormDto orderFormDto = new OrderFormDto();
            orderFormDto.setItemId(likeOrderDto.getItemId());
            orderFormDto.setCount(likeOrderDto.getCount());
            orderFormDto.setViewDay(likeOrderDto.getViewDay());
            orderFormDto.setAddress(likeOrderDto.getAddress());
            orderFormDto.setDelReq(likeOrderDto.getDelReq());
            orderFormDto.setReqWrite(likeOrderDto.getReqWrite());
            orderFormDto.setGetTicket(likeOrderDto.getGetTicket());
            orderFormDtoList.add(orderFormDto);
        }

        Long orderId = orderService.orders(orderFormDtoList, email);

        for(LikeOrderFormDto likeOrderFormDto : likeOrderDtoList) {
            UserLikeItem likeItem = userLikeItemRepository.findById(likeOrderFormDto.getLikeItemId())
                    .orElseThrow(EntityNotFoundException::new);
            userLikeItemRepository.delete(likeItem);
        }

        return orderId;
    }


}
