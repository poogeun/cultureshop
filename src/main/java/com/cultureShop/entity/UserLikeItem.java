package com.cultureShop.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "user_like_item")
public class UserLikeItem extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "user_like_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_like_id")
    private UserLike userLike;

    @ColumnDefault("0")
    private int count; // 찜 수

    public static UserLikeItem createLikeItem(Item item, UserLike userLike) {
        UserLikeItem userLikeItem = new UserLikeItem();
        userLikeItem.setItem(item);
        userLikeItem.setUserLike(userLike);
        return userLikeItem;
    }

    public void addLike(){
        this.count += 1;
    }

    public void minusLike(){
        this.count -= 1;
    }
}
