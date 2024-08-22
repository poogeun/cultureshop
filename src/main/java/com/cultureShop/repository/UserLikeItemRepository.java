package com.cultureShop.repository;

import com.cultureShop.dto.LikeItemDto;
import com.cultureShop.dto.MainItemDto;
import com.cultureShop.entity.UserLikeItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserLikeItemRepository extends JpaRepository<UserLikeItem, Long> {

    List<UserLikeItem> findByItemId(Long itemId);

    UserLikeItem findByItemIdAndUserLikeId(Long itemId, Long userLikeId);

    List<UserLikeItem> findByUserLikeId(Long userLikeId);

    @Query("select new com.cultureShop.dto.LikeItemDto(uli.id, i.id, i.itemName, i.place, i.address, i.price, i.startDay, i.endDay, iti.imgUrl) " +
            "from UserLikeItem uli, ItemImg iti join uli.item i " +
            "where uli.id = :likeItemId and iti.item.id = uli.item.id and iti.repImgYn = 'Y'")
    LikeItemDto findLikeItemDto(Long likeItemId);

}
