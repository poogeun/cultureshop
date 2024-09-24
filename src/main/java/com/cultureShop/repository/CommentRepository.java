package com.cultureShop.repository;

import com.cultureShop.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByMusArtIdOrderByRegTimeDesc(Long musArtId);

    @Query("select c from Comment c where cDepth = 1 and cGroup = :commentId order by regTime desc")
    List<Comment> findReComments(Long commentId);
}
