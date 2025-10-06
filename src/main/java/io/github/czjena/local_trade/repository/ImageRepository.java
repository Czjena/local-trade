package io.github.czjena.local_trade.repository;

import io.github.czjena.local_trade.model.Advertisement;
import io.github.czjena.local_trade.model.Image;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface ImageRepository extends CrudRepository<Image, Integer> {



    Image findByImageId(UUID imageId);
}
