package com.cultureShop.repository;

import com.cultureShop.dto.MusArtMainDto;
import com.cultureShop.entity.MusArt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MusArtRepository extends JpaRepository<MusArt, Long> {

    // 박물관 or 미술관 조회
    @Query("select ma from MusArt ma where ma.type = :type order by rand()")
    List<MusArt> findByType(String type);

    // 주소 입력 후 박물관 or 미술관 조회
    @Query("select ma from MusArt ma where ma.address like %:address% and ma.type = :type order by rand()")
    List<MusArt> findBySimpleAddr(String address, String type);

    // 장소 dto 조회
    @Query("select new com.cultureShop.dto.MusArtMainDto(ma.id, ma.name, ma.type, ma.address, ma.openTime, ma.closeTime, ma.geoX, ma.geoY) " +
            "from MusArt ma where ma.id = :musArtId")
    MusArtMainDto findPlaceDto(Long musArtId);

    // 로그인 된 경우 장소 dto 조회 (찜 여부)
    @Query("select new com.cultureShop.dto.MusArtMainDto(ma.id, ma.name, ma.type, ma.address, ma.openTime, ma.closeTime, ma.geoX, ma.geoY, uli.id) " +
            "from MusArt ma left join UserLikeItem uli on ma.id = uli.musArt.id and uli.userLike.id = :userLikeId " +
            "where ma.id = :musArtId")
    MusArtMainDto findUserPlaceDto(Long musArtId, Long userLikeId);

}
