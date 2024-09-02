package com.cultureShop.repository;

import com.cultureShop.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByMusArtIdOrderByRegTimeDesc(Long musArtId);
}
