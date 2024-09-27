package com.cultureShop.entity;

import com.cultureShop.dto.CommentDto;
import com.cultureShop.dto.ReCommentDto;
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

    private Long cDepth; // 일반댓글이면 0 대댓글이면 1
    private Long cGroup; // 답글일경우 id값 저장

    public static Comment createComment(CommentDto commentDto, MusArt musArt) {
        Comment comment = new Comment();
        comment.setContent(commentDto.getContent());
        comment.setMusArt(musArt);
        comment.setCDepth(0L); // 댓글이면 0
        return comment;
    }

    public static Comment createReComment(ReCommentDto reCommentDto, MusArt musArt) {
        Comment comment = new Comment();
        comment.setContent(reCommentDto.getContent());
        comment.setMusArt(musArt);
        comment.setCDepth(1L); // 답글이면 1
        comment.setCGroup(reCommentDto.getCommentId()); // 현재 댓글 id
        return comment;
    }

    public void updateComment(String content) {
        this.content = content;
    }
}
