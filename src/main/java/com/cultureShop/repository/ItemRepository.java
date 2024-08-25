package com.cultureShop.repository;

import com.cultureShop.dto.MainItemDto;
import com.cultureShop.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long>,
        QuerydslPredicateExecutor<Item>, ItemRepositoryCustom {

    List<Item> findByItemName(String itemName);

    List<Item> findByPriceLessThan(Integer price); // List에 int 안쓰기 때문에 Integer 사용

    List<Item> findByCategoryOrderByRegTimeDesc(String category);

    List<Item> findByCategoryOrderByEndDayAsc(String category);

    @Query("select i from Item i where i.address like %:address% order by i.regTime desc")
    List<Item> findByAddress(String address);

    @Query("select new com.cultureShop.dto.MainItemDto(i.id, i.itemName, i.place, i.address, i.price, i.startDay, i.endDay, iti.imgUrl) " +
            "from Item i, ItemImg iti " +
            "where iti.item.id = :itemId and i.id = :itemId and iti.repImgYn = 'Y'")
    MainItemDto findMainItemDto(Long itemId);

    @Query("select i, COUNT(r) as reviewCount " +
            "from Item i left join i.reviews r " +
            "where i.category = :category " +
            "group by i.id " +
            "order by reviewCount desc")
    List<Item> findItemsOrderByReviewCount(String category);

    // select * from item where price < Integer price order by desc; 내림차순
    List<Item> findByPriceLessThanOrderByPriceDesc(Integer price);

    // %:itemDetail% 와 (@Param("itemDetail") itemDetail 부분이 같아야 함 -> 매개변수로 받아오는 부
    @Query("select i from Item i where i.itemDetail like %:itemDetail% order by i.price desc")
    List<Item> findByItemDetail(@Param("itemDetail")String itemDetail);

}
