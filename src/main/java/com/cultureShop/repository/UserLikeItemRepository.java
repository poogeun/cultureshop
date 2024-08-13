package com.cultureShop.repository;

import com.cultureShop.entity.UserLikeItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserLikeItemRepository extends JpaRepository<UserLikeItem, Long> {

    UserLikeItem findByItemId(Long itemId);
}
