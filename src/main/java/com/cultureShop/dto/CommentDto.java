package com.cultureShop.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentDto {

    private Long id;
    private String writer;
    private String content;
    private Long musArtId;
}
