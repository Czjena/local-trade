package io.github.czjena.local_trade.repository;

import io.github.czjena.local_trade.model.Advertisement;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AdvertisementRepository extends JpaRepository<Advertisement, Integer> {
    List<Advertisement> findByCategoryId(Integer categoryId);

}
