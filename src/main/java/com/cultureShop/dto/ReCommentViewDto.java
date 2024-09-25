package com.cultureShop.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@Setter
@ToString
public class ReCommentViewDto {

    private Long commentId;
    private String content;
    private String createdBy;
    private LocalDate regTime;

    public ReCommentViewDto(Long commentId, String content, String createdBy, LocalDate regTime) {
        this.commentId = commentId;
        this.content = content;
        this.createdBy = createdBy;
        this.regTime = regTime;
    }
}
