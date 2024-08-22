package com.cultureShop.dto;

import com.cultureShop.entity.Member;
import com.cultureShop.entity.Review;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;

@Getter
@Setter
public class ReviewFormDto {

    private Long id;

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
