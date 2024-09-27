package com.cultureShop.service;

import com.cultureShop.dto.CommentDto;
import com.cultureShop.dto.ReCommentDto;
import com.cultureShop.dto.ReCommentViewDto;
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

    /* 댓글 저장 */
    public void saveComment(CommentDto commentDto) {
        MusArt musArt = musArtRepository.findById(commentDto.getMusArtId())
                .orElseThrow(EntityNotFoundException::new);
        Comment comment = Comment.createComment(commentDto, musArt);
        commentRepository.save(comment);
    }

    /* 답글 저장 */
    public Comment saveReComment(ReCommentDto reCommentDto) {
        MusArt musArt = musArtRepository.findById(reCommentDto.getMusArtId())
                .orElseThrow(EntityNotFoundException::new);
        Comment comment = Comment.createReComment(reCommentDto, musArt);
        commentRepository.save(comment);
        return comment;
    }

    /* 특정 장소의 댓글,답글 */
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

    /* 상세페이지 답글 조회 시 */
    @Transactional(readOnly = true)
    public List<Comment> getReComment(Long commentId) {
        return commentRepository.findReComments(commentId);
    }

    /* 답글 작성 후 화면 추가 시 (ajax) */
    @Transactional(readOnly = true)
    public ReCommentViewDto getViewReComment(Long commentId) {
        ReCommentViewDto comment = commentRepository.findReCommentViewDto(commentId);
        return comment;
    }
}
