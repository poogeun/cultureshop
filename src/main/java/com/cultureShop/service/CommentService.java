package com.cultureShop.service;

import com.cultureShop.dto.CommentDto;
import com.cultureShop.dto.ReCommentDto;
import com.cultureShop.entity.Comment;
import com.cultureShop.entity.MusArt;
import com.cultureShop.repository.CommentRepository;
import com.cultureShop.repository.MusArtRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentService {

    private final MusArtRepository musArtRepository;
    private final CommentRepository commentRepository;

    public void saveComment(CommentDto commentDto) {
        MusArt musArt = musArtRepository.findById(commentDto.getMusArtId())
                .orElseThrow(EntityNotFoundException::new);
        Comment comment = Comment.createComment(commentDto, musArt);
        commentRepository.save(comment);
    }

    public Comment saveReComment(ReCommentDto reCommentDto) {
        MusArt musArt = musArtRepository.findById(reCommentDto.getMusArtId())
                .orElseThrow(EntityNotFoundException::new);
        Comment comment = Comment.createReComment(reCommentDto, musArt);
        commentRepository.save(comment);
        return comment;
    }

    @Transactional(readOnly = true)
    public List<Comment> getPlaceComments(Long musArtId) {
        return commentRepository.findByMusArtIdOrderByRegTimeDesc(musArtId);
    }

    public void deleteComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(EntityNotFoundException::new);
        commentRepository.delete(comment);
    }

    public void updateComment(Long commentId, String content) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(EntityNotFoundException::new);
        comment.updateComment(content);
    }

    @Transactional(readOnly = true)
    public List<Comment> getReComment(Long commentId) {
        return commentRepository.findReComments(commentId);
    }
}
