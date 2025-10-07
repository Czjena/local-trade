package io.github.czjena.local_trade.repository;

import io.github.czjena.local_trade.model.Advertisement;
import io.github.czjena.local_trade.model.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ImageRepository extends JpaRepository<Image, Integer> {
    Image findByImageId(UUID imageId);
    Image findByAdvertisement(Advertisement advertisement);

    List<Image> findAllByAdvertisement(Advertisement ad);
}
