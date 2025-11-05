package io.github.czjena.local_trade.repository;

import io.github.czjena.local_trade.model.Review;
import io.github.czjena.local_trade.model.Trade;
import io.github.czjena.local_trade.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReviewRepository  extends JpaRepository<Review, Long> {
    Optional<Review> findByReviewedUserOrReviewer(Users user, Users user1);
    List<Review> findAllByReviewedUserOrReviewer(Users user, Users user1);

    boolean existsByTradeAndReviewer(Trade trade, Users reviewer);

    Optional<Review> findByReviewId(UUID reviewId);

    List<Review> findAllByReviewedUser(Users user);
}
