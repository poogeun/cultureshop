package com.cultureShop.service;

import com.cultureShop.entity.MusArt;
import com.cultureShop.repository.MusArtRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class MusArtService {

    private final MusArtRepository musArtRepository;

    @Transactional(readOnly = true)
    public List<MusArt> getAllMusArt(String type) {
        return musArtRepository.findByTypeOrderByRand(type);
    }
}
