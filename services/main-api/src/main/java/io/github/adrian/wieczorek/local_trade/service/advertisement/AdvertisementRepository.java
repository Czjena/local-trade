package io.github.adrian.wieczorek.local_trade.service.advertisement;

import io.github.adrian.wieczorek.local_trade.service.user.UsersEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AdvertisementRepository extends JpaRepository<AdvertisementEntity, Integer> {
    List<AdvertisementEntity> findByCategoryEntityId(Integer categoryId);
    Optional<AdvertisementEntity> findByUserAndId(UsersEntity user, Integer advertisementId);
    Page<AdvertisementEntity> findAll(Specification<AdvertisementEntity> specification, Pageable pageable);
    Optional<AdvertisementEntity> findByAdvertisementId(UUID advertisementId);
    List<AdvertisementEntity> user(UsersEntity user);
    long countByCategoryEntityId(Integer categoryId);
    AdvertisementEntity findByUser(UsersEntity user);
    AdvertisementEntity findByTitle(String title);
}
