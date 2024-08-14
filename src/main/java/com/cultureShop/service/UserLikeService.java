package com.cultureShop.service;

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

@Service
@RequiredArgsConstructor
@Transactional
public class UserLikeService {

    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;
    private final UserLikeRepository userLikeRepository;
    private final UserLikeItemRepository userLikeItemRepository;

    public Long addUserLike(Long itemId, String email) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(EntityNotFoundException::new);
        Member member = memberRepository.findByEmail(email);

        UserLike userLike = userLikeRepository.findByMemberId(member.getId());
        if(userLike == null) {
            userLike = UserLike.createLike(member);
            userLikeRepository.save(userLike);
        }

        UserLikeItem savedLikeItem = userLikeItemRepository.findByItemId(itemId);
        if(savedLikeItem != null) {
            savedLikeItem.addLike();
            return savedLikeItem.getId();
        }
        else{
            UserLikeItem userLikeItem = UserLikeItem.createLikeItem(item,userLike);
            userLikeItemRepository.save(userLikeItem);
            return userLikeItem.getId();
        }
    }
}
