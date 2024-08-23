package com.cultureShop.repository;

import com.cultureShop.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByItemIdOrderByRegTimeDesc(Long itemId);

    Review findByItemIdAndMemberId(Long itemId, Long memberId);

    List<Review> findByMemberIdOrderByRegTimeDesc(Long memberId);
}
