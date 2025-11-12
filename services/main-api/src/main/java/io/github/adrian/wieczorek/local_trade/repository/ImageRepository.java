package io.github.adrian.wieczorek.local_trade.repository;

import io.github.adrian.wieczorek.local_trade.model.AdvertisementEntity;
import io.github.adrian.wieczorek.local_trade.model.ImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ImageRepository extends JpaRepository<ImageEntity, Integer> {
    ImageEntity findByImageId(UUID imageId);
    ImageEntity findByAdvertisementEntity(AdvertisementEntity advertisementEntity);
    List<ImageEntity> findAllByAdvertisementEntity(AdvertisementEntity ad);
}
