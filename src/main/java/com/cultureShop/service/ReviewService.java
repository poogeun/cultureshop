package com.cultureShop.service;

import com.cultureShop.dto.ReviewFormDto;
import com.cultureShop.entity.Member;
import com.cultureShop.entity.Review;
import com.cultureShop.repository.MemberRepository;
import com.cultureShop.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final MemberRepository memberRepository;

    public Long saveReview(ReviewFormDto reviewFormDto, String email) throws Exception {

        Member member = memberRepository.findByEmail(email);
        Review review = reviewFormDto.createReview();
        review.setMember(member);
        reviewRepository.save(review);
        return review.getId();
    }
}
