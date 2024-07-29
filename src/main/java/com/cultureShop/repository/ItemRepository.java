package com.cultureShop.repository;

import com.cultureShop.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findByItemName(String itemName);

    List<Item> findByPriceLessThan(Integer price); // List에 int 안쓰기 때문에 Integer 사용

    // select * from item where price < Integer price order by desc; 내림차순
    List<Item> findByPriceLessThanOrderByPriceDesc(Integer price);

    // %:itemDetail% 와 (@Param("itemDetail") itemDetail 부분이 같아야 함 -> 매개변수로 받아오는 부
    @Query("select i from Item i where i.itemDetail like %:itemDetail% order by i.price desc")
    List<Item> findByItemDetail(@Param("itemDetail")String itemDetail);

}
