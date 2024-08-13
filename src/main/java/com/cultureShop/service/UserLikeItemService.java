package com.cultureShop.service;

import com.cultureShop.entity.UserLikeItem;
import com.cultureShop.repository.UserLikeItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserLikeItemService {

    private final UserLikeItemRepository userLikeItemRepository;

    public UserLikeItem getLikeItem(Long itemId) {
        return userLikeItemRepository.findByItemId(itemId);
    }
}
