package com.cultureShop.repository;

import com.cultureShop.entity.MusArt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MusArtRepository extends JpaRepository<MusArt, Long> {

    List<MusArt> findByTypeOrderByRand(String type);
}
