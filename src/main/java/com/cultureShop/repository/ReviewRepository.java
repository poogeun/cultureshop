package com.cultureShop.repository;

import com.cultureShop.dto.LikeItemDto;
import com.cultureShop.dto.ReviewItemDto;
import com.cultureShop.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByItemIdOrderByRegTimeDesc(Long itemId);

    Review findByItemIdAndMemberId(Long itemId, Long memberId);

    List<Review> findByMemberIdOrderByRegTimeDesc(Long memberId);

    @Query("select new com.cultureShop.dto.ReviewItemDto(r.id, i.id, i.itemName, i.place, i.address, oi.viewDay, iti.imgUrl, r.regTime) " +
            "from Review r, OrderItem oi, ItemImg iti join r.item i " +
            "where r.member.id = :memberId and oi.item.id = r.item.id and iti.item.id = r.item.id and iti.repImgYn = 'Y' " +
            "order by r.regTime desc")
    List<ReviewItemDto> findReviewItemDto(Long memberId);



}
