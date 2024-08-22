package com.cultureShop.service;

import com.cultureShop.dto.ReviewFormDto;
import com.cultureShop.entity.Review;
import com.cultureShop.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;

    public Long saveReview(ReviewFormDto reviewFormDto) throws Exception {

        Review review = reviewFormDto.createReview();
        reviewRepository.save(review);
        return review.getId();
    }
}
