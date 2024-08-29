package com.cultureShop.service;

import com.cultureShop.dto.MusArtMainDto;
import com.cultureShop.entity.MusArt;
import com.cultureShop.repository.MusArtRepository;
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
}
