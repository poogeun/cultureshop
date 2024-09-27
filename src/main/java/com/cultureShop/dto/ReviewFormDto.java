package com.cultureShop.dto;

import com.cultureShop.entity.Review;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.modelmapper.ModelMapper;

@Getter
@Setter
@ToString
/* 댓글 등록 */
public class ReviewFormDto {

    private Long id;

    private Long itemId;

    private String title;

    private String content;

    private int starPoint;


    //=================================================
    private static ModelMapper modelMapper = new ModelMapper();

    public Review createReview() {
        return modelMapper.map(this, Review.class);
    }

    public static ReviewFormDto of(Review review) {
        return modelMapper.map(review, ReviewFormDto.class);
    }

}
