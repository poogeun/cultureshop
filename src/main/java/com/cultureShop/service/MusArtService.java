package com.cultureShop.service;

import com.cultureShop.dto.MusArtMainDto;
import com.cultureShop.entity.Member;
import com.cultureShop.entity.MusArt;
import com.cultureShop.entity.UserLike;
import com.cultureShop.repository.MemberRepository;
import com.cultureShop.repository.MusArtRepository;
import com.cultureShop.repository.UserLikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class MusArtService {

    private final MusArtRepository musArtRepository;
    private final UserLikeRepository userLikeRepository;
    private final MemberRepository memberRepository;

    // 박물관, 미술관별 장소 목록 조회
    @Transactional(readOnly = true)
    public List<MusArtMainDto> getAllPlace(String type) {
        List<MusArt> items = musArtRepository.findByType(type);
        List<MusArtMainDto> placeMainItems = new ArrayList<>();
        for(MusArt item : items){
            MusArtMainDto placeMainItem = musArtRepository.findPlaceDto(item.getId());
            placeMainItems.add(placeMainItem);
        }

        return placeMainItems;
    }

    // 주소 입력 후 박물관, 미술관별 장소 목록 조회
    @Transactional(readOnly = true)
    public List<MusArtMainDto> getAllAddrPlace(String address, String type) {
        List<MusArt> items = musArtRepository.findBySimpleAddr(address, type);
        List<MusArtMainDto> placeMainItems = new ArrayList<>();
        for(MusArt item : items){
            MusArtMainDto placeMainItem = musArtRepository.findPlaceDto(item.getId());
            placeMainItems.add(placeMainItem);
        }

        return placeMainItems;
    }

    // 메인페이지 박물관, 미술관별 장소 조회
    @Transactional(readOnly = true)
    public List<MusArtMainDto> getPlaceMainMusArt(String type) {
        List<MusArt> items = musArtRepository.findByType(type);
        List<MusArtMainDto> placeMainItems = new ArrayList<>();
        for(int i=0; i<5; i++) {
            MusArt item = items.get(i);
            MusArtMainDto placeMainItem = musArtRepository.findPlaceDto(item.getId());
            placeMainItems.add(placeMainItem);
        }

        return placeMainItems;
    }

    // 주소 입력 후 메인페이지 박물관, 미술관별 장소 조회
    @Transactional(readOnly = true)
    public List<MusArtMainDto> getAddrMusArt(String address, String type) {
        List<MusArt> items = musArtRepository.findBySimpleAddr(address, type);
        List<MusArtMainDto> placeMainItems = new ArrayList<>();
        for(int i=0; i<5; i++) {
            MusArt item = items.get(i);
            MusArtMainDto placeMainItem = musArtRepository.findPlaceDto(item.getId());
            placeMainItems.add(placeMainItem);
        }

        return placeMainItems;
    }

    // 로그인 후 메인페이지 박물관, 미술관별 장소 조회
    @Transactional(readOnly = true)
    public List<MusArtMainDto> getUserPlaceMainMusArt(String type, String email) {
        Member member = memberRepository.findByEmail(email);
        List<MusArt> items = musArtRepository.findByType(type);
        List<MusArtMainDto> placeMainItems = new ArrayList<>();
        UserLike userLike = userLikeRepository.findByMemberId(member.getId());

        for(int i=0; i<5; i++) {
            MusArt item = items.get(i);
            MusArtMainDto placeMainItem = musArtRepository.findUserPlaceDto(item.getId(), userLike.getId());
            placeMainItems.add(placeMainItem);
        }

        return placeMainItems;
    }

    // 로그인 후 주소 검색 된 메인페이지 박물관, 미술관별 장소 조회
    @Transactional(readOnly = true)
    public List<MusArtMainDto> getUserAddrMusArt(String address, String type, String email) {
        Member member = memberRepository.findByEmail(email);
        List<MusArt> items = musArtRepository.findBySimpleAddr(address, type);
        List<MusArtMainDto> placeMainItems = new ArrayList<>();
        UserLike userLike = userLikeRepository.findByMemberId(member.getId());
        for(int i=0; i<5; i++) {
            MusArt item = items.get(i);
            MusArtMainDto placeMainItem = musArtRepository.findUserPlaceDto(item.getId(), userLike.getId());
            placeMainItems.add(placeMainItem);
        }

        return placeMainItems;
    }


}
