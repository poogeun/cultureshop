package com.cultureShop.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReCommentDto {
    private Long commentId;
    private Long musArtId;
    private String content;
}
