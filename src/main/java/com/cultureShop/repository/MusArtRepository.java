package com.cultureShop.repository;

import com.cultureShop.dto.MainItemDto;
import com.cultureShop.dto.MusArtMainDto;
import com.cultureShop.entity.Item;
import com.cultureShop.entity.MusArt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MusArtRepository extends JpaRepository<MusArt, Long> {

    @Query("select ma from MusArt ma where ma.type = :type order by rand()")
    List<MusArt> findByType(String type);

    @Query("select new com.cultureShop.dto.MusArtMainDto(ma.id, ma.name, ma.type, ma.address, ma.openTime, ma.closeTime) " +
            "from MusArt ma where ma.id = :musArtId")
    MusArtMainDto findPlaceDto(Long musArtId);

    @Query("select ma from MusArt ma where ma.address like %:address% and ma.type = :type order by rand()")
    List<MusArt> findBySimpleAddr(String address, String type);

}
