package com.cultureShop.entity;

import com.cultureShop.dto.CommentDto;
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

    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "musArtId")
    private MusArt musArt;

    public static Comment createComment(CommentDto commentDto, MusArt musArt) {
        Comment comment = new Comment();
        comment.setContent(commentDto.getContent());
        comment.setMusArt(musArt);
        return comment;
    }

    public void updateComment(String content) {
        this.content = content;
    }
}
