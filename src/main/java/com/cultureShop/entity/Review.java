package com.cultureShop.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "review")
@Getter
@Setter
public class Review extends BaseEntity{

    @Id
    @Column(name = "review_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    private String title;

    @Lob
    @Column(length = 300)
    private String content;

    private int starPoint;

    public void updateReview(String title, String content, int starPoint){
        this.title = title;
        this.content = content;
        this.starPoint = starPoint;
    }
}
