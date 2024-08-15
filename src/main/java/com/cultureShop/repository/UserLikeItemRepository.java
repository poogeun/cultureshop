package com.cultureShop.repository;

import com.cultureShop.entity.UserLikeItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserLikeItemRepository extends JpaRepository<UserLikeItem, Long> {

    List<UserLikeItem> findByItemId(Long itemId);

    UserLikeItem findByItemIdAndUserLikeId(Long itemId, Long userLikeId);

    List<UserLikeItem> findByUserLikeId(Long userLikeId);
}
