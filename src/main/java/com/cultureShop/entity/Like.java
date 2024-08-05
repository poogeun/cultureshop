package com.cultureShop.entity;

import com.cultureShop.repository.MemberRepository;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "like")
@Getter
@Setter
@ToString
public class Like {

    @Id
    @Column(name = "like_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    public static Like createLike(Member member) {
        Like like = new Like();
        like.setMember(member);
        return like;
    }
}
