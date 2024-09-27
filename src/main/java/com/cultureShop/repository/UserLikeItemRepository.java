package com.cultureShop.repository;

import com.cultureShop.dto.LikeItemDto;
import com.cultureShop.entity.UserLikeItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserLikeItemRepository extends JpaRepository<UserLikeItem, Long> {

    List<UserLikeItem> findByItemId(Long itemId);

    List<UserLikeItem> findByMusArtId(Long musArtId);

    List<UserLikeItem> findByUserLikeId(Long userLikeId);

    // 현재 유저의 특정 상품 찜 여부 조회
    UserLikeItem findByItemIdAndUserLikeId(Long itemId, Long userLikeId);

    // 현재 유저의 장소 상품 찜 여부 조회
    UserLikeItem findByMusArtIdAndUserLikeId(Long musArtId, Long userLikeId);

    // 현재 유저의 찜 상품 dto 조회
    @Query("select new com.cultureShop.dto.LikeItemDto(uli.id, i.id, i.itemName, i.place, i.address, i.price, i.startDay, i.endDay, iti.imgUrl) " +
            "from UserLikeItem uli, ItemImg iti join uli.item i " +
            "where uli.id = :likeItemId and iti.item.id = uli.item.id and iti.repImgYn = 'Y'")
    LikeItemDto findLikeItemDto(Long likeItemId);

}
