package io.github.czjena.local_trade.testutils;

import io.github.czjena.local_trade.model.Review;
import io.github.czjena.local_trade.model.Trade;
import io.github.czjena.local_trade.model.Users;

import java.util.UUID;

public class ReviewUtils {
    public static Review createTestReview(Trade trade, Users reviewer, Users reviewedUser) {
        return Review.builder()
                .reviewId(UUID.fromString("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee"))

                .trade(trade)
                .reviewer(reviewer)
                .reviewedUser(reviewedUser)

                .rating(5)
                .comment("Test comment everything is ok")
                .build();
    }
}
