package com.cultureShop.service;

import com.cultureShop.dto.LikeItemDto;
import com.cultureShop.dto.MainItemDto;
import com.cultureShop.dto.MusArtMainDto;
import com.cultureShop.entity.*;
import com.cultureShop.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
@RequiredArgsConstructor
public class UserLikeItemService {

    private final UserLikeItemRepository userLikeItemRepository;
    private final MemberRepository memberRepository;
    private final UserLikeRepository userLikeRepository;
    private final ItemRepository itemRepository;
    private final MusArtRepository musArtRepository;

    public List<UserLikeItem> getLikeItems(Long itemId) {
        return userLikeItemRepository.findByItemId(itemId);
    }

    public List<UserLikeItem> getLikePlaces(Long musArtId) {
        return userLikeItemRepository.findByMusArtId(musArtId);
    }

    // 찜 상품 여부 확인
    public boolean findLikeItem(String email, Long itemId) {
        Member member = memberRepository.findByEmail(email);
        if(member != null) {
            UserLike userLike = userLikeRepository.findByMemberId(member.getId());
            if(userLike != null) {
                List<UserLikeItem> likeItems = userLikeItemRepository.findByUserLikeId(userLike.getId());
                if (likeItems != null) {
                    for (UserLikeItem likeItem : likeItems) {
                        if(likeItem.getItem() != null) {
                            if (Objects.equals(likeItem.getItem().getId(), itemId)) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
    // 찜 장소 여부 확인
    public boolean findLikePlace(String email, Long musArtId) {
        Member member = memberRepository.findByEmail(email);
        if(member != null) {
            UserLike userLike = userLikeRepository.findByMemberId(member.getId());
            if(userLike != null) {
                List<UserLikeItem> likeItems = userLikeItemRepository.findByUserLikeId(userLike.getId());
                if (likeItems != null) {
                    for (UserLikeItem likeItem : likeItems) {
                        if(likeItem.getMusArt() != null) {
                            if (likeItem.getMusArt().getId() == musArtId) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    // 찜 상품 추가
    public void addLike(String email, Long itemId) {
        Member member = memberRepository.findByEmail(email);
        UserLike userLike = userLikeRepository.findByMemberId(member.getId());
        Item item = itemRepository.findById(itemId)
                .orElseThrow(EntityNotFoundException::new);

        if(userLike == null) {
            userLike = UserLike.createLike(member);
            userLikeRepository.save(userLike);
        }
        UserLikeItem userLikeItem = userLikeItemRepository.findByItemIdAndUserLikeId(itemId, userLike.getId());

        if(userLikeItem == null) {
            userLikeItem = UserLikeItem.createLikeItem(item,userLike);
            userLikeItemRepository.save(userLikeItem);
            userLikeItem.addLike();
        }
        else {
            userLikeItemRepository.delete(userLikeItem);
        }
    }

    // 찜 장소 추가
    public void addLikePlace(String email, Long musArtId) {
        Member member = memberRepository.findByEmail(email);
        UserLike userLike = userLikeRepository.findByMemberId(member.getId());
        MusArt musArt = musArtRepository.findById(musArtId)
                .orElseThrow(EntityNotFoundException::new);

        if(userLike == null) {
            userLike = UserLike.createLike(member);
            userLikeRepository.save(userLike);
        }
        UserLikeItem userLikeItem = userLikeItemRepository.findByMusArtIdAndUserLikeId(musArtId, userLike.getId());

        if(userLikeItem == null) {
            userLikeItem = UserLikeItem.createLikePlace(musArt,userLike);
            userLikeItemRepository.save(userLikeItem);
            userLikeItem.addLike();
        }
        else {
            userLikeItemRepository.delete(userLikeItem);
        }
    }

    // 찜 상품 개수
    public int likeCount(String email) {
        Member member = memberRepository.findByEmail(email);
        UserLike userLike = userLikeRepository.findByMemberId(member.getId());
        int likeCount = 0;
        if(userLike != null) {
            List<UserLikeItem> userLikeItems = userLikeItemRepository.findByUserLikeId(userLike.getId());
            if(userLikeItems != null) {
                likeCount = userLikeItems.size();
            }
        }

        return likeCount;
    }

    //  찜 상품 리스트
    public List<LikeItemDto> getLikeList(String email) {

        Member member = memberRepository.findByEmail(email);
        UserLike userLike = userLikeRepository.findByMemberId(member.getId());
        List<UserLikeItem> userLikeItems = userLikeItemRepository.findByUserLikeId(userLike.getId());

        List<LikeItemDto> likeItemDtls = new ArrayList<>();
        for(UserLikeItem userLikeItem : userLikeItems) {
            if(userLikeItem.getItem() != null) {
                Long itemId = userLikeItem.getItem().getId();
                LikeItemDto likeItem = userLikeItemRepository.findLikeItemDto(userLikeItem.getId());
                likeItemDtls.add(likeItem);
            }
        }
        return likeItemDtls;
    }

    public List<MusArtMainDto> getLikePlaceList(String email) {
        Member member = memberRepository.findByEmail(email);
        UserLike userLike = userLikeRepository.findByMemberId(member.getId());
        List<UserLikeItem> userLikeItems = userLikeItemRepository.findByUserLikeId(userLike.getId());

        List<MusArtMainDto> likePlaceDtls = new ArrayList<>();
        for(UserLikeItem userLikeItem : userLikeItems) {
            if(userLikeItem.getMusArt() != null) {
                Long musArtId = userLikeItem.getMusArt().getId();
                MusArtMainDto musArtMainDto = musArtRepository.findPlaceDto(musArtId);
                likePlaceDtls.add(musArtMainDto);
            }
        }
        return likePlaceDtls;
    }

    // 찜 주문 리스트 가져오기
    public List<LikeItemDto> getOrderLike(List<Long> likeItemIds) {

        List<LikeItemDto> orderLikes = new ArrayList<>();
        for(Long likeItemId : likeItemIds) {
            LikeItemDto orderLike = userLikeItemRepository.findLikeItemDto(likeItemId);
            orderLikes.add(orderLike);
        }
        return orderLikes;
    }
}
