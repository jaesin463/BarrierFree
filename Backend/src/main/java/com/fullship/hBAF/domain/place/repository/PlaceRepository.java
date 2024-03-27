package com.fullship.hBAF.domain.place.repository;

import com.fullship.hBAF.domain.place.entity.Place;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PlaceRepository extends JpaRepository<Place, Long> {

    Place findPlaceByPoiId(String PoiId);

    boolean existsByPoiId(String poiId);

    Optional<Place> findByPoiId(String poiId);

    List<Place> findByCategory(String category);

    List<Place> findByType(boolean type);

//    @Query("select wtcltId from Place where type = true and barrierFree is null")
    @Query("select wtcltId from Place where type = true and barrierFree is null")
    List<String> findWtcltIdByType();

    Optional<Place> findByWtcltId(String wcltId);

//    List<Place> findByTypeTrueAndBarrierFreeIsNull();
    Optional<Place> findById(Long placeId);
}