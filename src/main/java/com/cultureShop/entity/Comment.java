package com.cultureShop.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "comments")
@Getter
@Setter
@ToString
public class Comment extends BaseEntity{

    @Id
    @Column(name = "comment_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private  Long id;

    private String writer;
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "musArtId")
    private MusArt musArt;
}
