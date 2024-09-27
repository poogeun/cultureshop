package com.cultureShop.repository;

import com.cultureShop.dto.MainItemDto;
import com.cultureShop.entity.Item;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long>,
        QuerydslPredicateExecutor<Item>, ItemRepositoryCustom {

    List<Item> findByCategoryOrderByRegTimeDesc(String category);

    // 지역별 상품 조회
    @Query("select i from Item i where i.address like %:address% order by i.regTime desc")
    List<Item> findByAddress(String address);

    // 내주변 축제 조회
    @Query("select i from Item i where i.address like %:address% and i.category = :category order by rand()")
    List<Item> findByAddressAndCategory(String address, String category);

    // 상품 dto 조회
    @Query("select new com.cultureShop.dto.MainItemDto(i.id, i.itemName, i.place, i.address, i.price, i.startDay, i.endDay, iti.imgUrl) " +
            "from ItemImg iti, Item i " +
            "where iti.item.id = :itemId and i.id = :itemId and iti.repImgYn = 'Y'")
    MainItemDto findMainItemDto(Long itemId);

    /* 상품 dto 조회
    * 로그인 된 경우
    * 찜 여부 확인 가능
    * */
    @Query("select new com.cultureShop.dto.MainItemDto(i.id, i.itemName, i.place, i.address, i.price, i.startDay, i.endDay, iti.imgUrl, uli.id) " +
            "from ItemImg iti, Item i left join UserLikeItem uli on i.id = uli.item.id and uli.userLike.id = :userLikeId " +
            "where iti.item.id = :itemId and i.id = :itemId and iti.repImgYn = 'Y'")
    MainItemDto findUserMainItemDto(Long itemId, Long userLikeId);

}
