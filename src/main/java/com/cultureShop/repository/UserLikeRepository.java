package com.cultureShop.repository;

import com.cultureShop.entity.UserLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserLikeRepository extends JpaRepository<UserLike, Long> {

    UserLike findByMemberId(Long memberId);
}
