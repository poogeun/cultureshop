package com.cultureShop.repository;

import com.cultureShop.entity.UserLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeRepository extends JpaRepository<UserLike, Long> {

}
