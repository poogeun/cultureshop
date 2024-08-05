package com.cultureShop.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "like_item")
public class LikeItem {

    @Id
    @GeneratedValue
    @Column(name = "like_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "like_id")
    private Like like;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

}
