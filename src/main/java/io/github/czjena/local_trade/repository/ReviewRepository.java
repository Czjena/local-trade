package io.github.czjena.local_trade.repository;

import io.github.czjena.local_trade.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface ReviewRepository  extends JpaRepository<Review, Long> {
}
