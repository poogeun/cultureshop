package com.cultureShop.service;

import com.cultureShop.dto.ReviewFormDto;
import com.cultureShop.entity.Item;
import com.cultureShop.entity.Member;
import com.cultureShop.entity.Review;
import com.cultureShop.repository.ItemRepository;
import com.cultureShop.repository.MemberRepository;
import com.cultureShop.repository.ReviewRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;

    public Long saveReview(ReviewFormDto reviewFormDto, Long itemId, String email) throws Exception {

        Member member = memberRepository.findByEmail(email);
        Review review = reviewFormDto.createReview();
        review.setMember(member);
        Item item = itemRepository.findById(itemId)
                        .orElseThrow(EntityNotFoundException::new);
        review.setItem(item);
        reviewRepository.save(review);
        return review.getId();
    }

    public List<Review> getItemReview(Long itemId) {
        return reviewRepository.findByItemIdOrderByRegTimeDesc(itemId);
    }

    public Review getMemItemReview(Long itemId, String email) {
        Member member = memberRepository.findByEmail(email);
        return reviewRepository.findByItemIdAndMemberId(itemId, member.getId());
    }

    @Transactional(readOnly = true)
    public ReviewFormDto getReviewForm(Long reviewId) {

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(EntityNotFoundException::new);
        ReviewFormDto reviewFormDto = ReviewFormDto.of(review);
        return reviewFormDto;
    }

    public void updateReview(Long reviewId, ReviewFormDto reviewFormDto) {

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(EntityNotFoundException::new);
        review.updateReview(reviewFormDto.getTitle(), reviewFormDto.getContent(), reviewFormDto.getStarPoint());
    }

    public List<Review> getMemReview(String email) {
        Member member = memberRepository.findByEmail(email);
        return reviewRepository.findByMemberIdOrderByRegTimeDesc(member.getId());
    }
}
