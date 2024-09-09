package com.cultureShop.service;

import com.cultureShop.dto.MainItemDto;
import com.cultureShop.dto.ReviewFormDto;
import com.cultureShop.dto.ReviewItemDto;
import com.cultureShop.entity.*;
import com.cultureShop.repository.*;
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
    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;

    public Long saveReview(ReviewFormDto reviewFormDto, Long itemId, String email) throws Exception {

        Member member = memberRepository.findByEmail(email);
        Review review = reviewFormDto.createReview();
        review.setMember(member);
        Item item = itemRepository.findById(itemId)
                        .orElseThrow(EntityNotFoundException::new);
        review.setItem(item);
        reviewRepository.save(review);

        // 주문아이템에 해당 리뷰 저장
        OrderItem orderItem = orderItemRepository.findByEmailAndItemId(email, itemId);
        orderItem.updateReview(review);

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

    public int getMemReviewCount(String email) {
        Member member = memberRepository.findByEmail(email);
        List<Review> reviews = reviewRepository.findByMemberIdOrderByRegTimeDesc(member.getId());

        int reviewCount = 0;
        if(reviews != null) {
            reviewCount = reviews.size();
        }

        return reviewCount;
    }

    public List<ReviewItemDto> getMemReview(String email) {
        Member member = memberRepository.findByEmail(email);
        List<ReviewItemDto> reviewItemDtos = reviewRepository.findReviewItemDto(member.getId());
        return reviewItemDtos;
    }

    public void deleteReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(EntityNotFoundException::new);
        OrderItem orderItem = orderItemRepository.findByReviewId(review.getId());
        orderItem.deleteReview();
        reviewRepository.delete(review);
    }

}
