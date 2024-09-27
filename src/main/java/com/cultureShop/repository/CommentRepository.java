package com.cultureShop.repository;

import com.cultureShop.dto.ReCommentViewDto;
import com.cultureShop.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByMusArtIdOrderByRegTimeDesc(Long musArtId);

    // 특정 댓글의 답글 조회
    @Query("select c from Comment c where cDepth = 1 and cGroup = :commentId order by regTime desc")
    List<Comment> findReComments(Long commentId);

    // 특정 댓글의 답글 dto 조회
    @Query("select new com.cultureShop.dto.ReCommentViewDto(c.id, c.content, c.createdBy, c.regTime) " +
            "from Comment c where c.id = :commentId")
    ReCommentViewDto findReCommentViewDto(Long commentId);
}
