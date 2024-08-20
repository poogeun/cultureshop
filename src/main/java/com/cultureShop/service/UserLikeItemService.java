package com.cultureShop.service;

import com.cultureShop.dto.MainItemDto;
import com.cultureShop.entity.Item;
import com.cultureShop.entity.Member;
import com.cultureShop.entity.UserLike;
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
@Transactional
@RequiredArgsConstructor
public class UserLikeItemService {

    private final UserLikeItemRepository userLikeItemRepository;
    private final MemberRepository memberRepository;
    private final UserLikeRepository userLikeRepository;
    private final ItemRepository itemRepository;

    public List<UserLikeItem> getLikeItems(Long itemId) {
        return userLikeItemRepository.findByItemId(itemId);
    }

    // 찜 여부 확인
    public boolean findLikeItem(String email, Long itemId) {
        Member member = memberRepository.findByEmail(email);
        if(member != null) {
            UserLike userLike = userLikeRepository.findByMemberId(member.getId());
            if(userLike != null) {
                List<UserLikeItem> likeItems = userLikeItemRepository.findByUserLikeId(userLike.getId());
                if (likeItems != null) {
                    for (UserLikeItem likeItem : likeItems) {
                        if (likeItem.getItem().getId() == itemId) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    // 찜 추가
    public void addLike(String email, Long itemId) {
        Member member = memberRepository.findByEmail(email);
        UserLike userLike = userLikeRepository.findByMemberId(member.getId());
        Item item = itemRepository.findById(itemId)
                .orElseThrow(EntityNotFoundException::new);

        if(userLike == null) {
            userLike = UserLike.createLike(member);
            userLikeRepository.save(userLike);
        }
        UserLikeItem userLikeItem = userLikeItemRepository.findByItemIdAndUserLikeId(itemId, userLike.getId());

        if(userLikeItem == null) {
            userLikeItem = UserLikeItem.createLikeItem(item,userLike);
            userLikeItemRepository.save(userLikeItem);
            userLikeItem.addLike();
        }
        else {
            userLikeItemRepository.delete(userLikeItem);
        }
    }

    // 찜 상품 개수
    public int likeCount(String email) {
        Member member = memberRepository.findByEmail(email);
        UserLike userLike = userLikeRepository.findByMemberId(member.getId());
        int likeCount = 0;
        if(userLike != null) {
            List<UserLikeItem> userLikeItems = userLikeItemRepository.findByUserLikeId(userLike.getId());
            if(userLikeItems != null) {
                likeCount = userLikeItems.size();
            }
        }
        return likeCount;
    }

    //  찜 상품 리스트
    public List<MainItemDto> getLikeList(String email) {

        Member member = memberRepository.findByEmail(email);
        UserLike userLike = userLikeRepository.findByMemberId(member.getId());
        List<UserLikeItem> userLikeItems = userLikeItemRepository.findByUserLikeId(userLike.getId());

        List<MainItemDto> likeItemDtls = new ArrayList<>();
        for(UserLikeItem userLikeItem : userLikeItems) {
            Long itemId = userLikeItem.getItem().getId();
            MainItemDto likeItem = itemRepository.findMainItemDto(itemId);
            likeItemDtls.add(likeItem);
        }
        return likeItemDtls;
    }
}
