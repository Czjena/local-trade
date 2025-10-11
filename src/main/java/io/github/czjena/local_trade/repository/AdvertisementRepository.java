package io.github.czjena.local_trade.repository;

import io.github.czjena.local_trade.dto.AdvertisementFilterDto;
import io.github.czjena.local_trade.model.Advertisement;
import io.github.czjena.local_trade.model.Image;
import io.github.czjena.local_trade.model.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AdvertisementRepository extends JpaRepository<Advertisement, Integer> {
    List<Advertisement> findByCategoryId(Integer categoryId);
    Optional<Advertisement> findByUserAndId(Users user, Integer advertisementId);
    Page<Advertisement> findAll(Specification<Advertisement> specification, Pageable pageable);
    Optional<Advertisement> findByAdvertisementId(UUID advertisementId);
    List<Advertisement> user(Users user);
}
