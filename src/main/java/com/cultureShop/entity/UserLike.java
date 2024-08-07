package com.cultureShop.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "user_like")
@Getter
@Setter
@ToString
public class UserLike {

    @Id
    @Column(name = "user_like_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    public static UserLike createLike(Member member) {
        UserLike like = new UserLike();
        like.setMember(member);
        return like;
    }
}
