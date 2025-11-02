package io.github.czjena.local_trade.service;

import io.github.czjena.local_trade.enums.TradeStatus;
import io.github.czjena.local_trade.exceptions.ConflictException;
import io.github.czjena.local_trade.exceptions.TradeAccessDenied;
import io.github.czjena.local_trade.exceptions.TradeReviewedConflictException;
import io.github.czjena.local_trade.exceptions.UserNotFoundException;
import io.github.czjena.local_trade.mappers.ReviewResponseDtoMapper;
import io.github.czjena.local_trade.model.Review;
import io.github.czjena.local_trade.model.Trade;
import io.github.czjena.local_trade.model.Users;
import io.github.czjena.local_trade.repository.ReviewRepository;
import io.github.czjena.local_trade.repository.TradeRepository;
import io.github.czjena.local_trade.repository.UsersRepository;
import io.github.czjena.local_trade.request.ReviewRequestDto;
import io.github.czjena.local_trade.response.ReviewResponseDto;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final UsersRepository usersRepository;
    private final ReviewResponseDtoMapper reviewResponseDtoMapper;
    private final TradeRepository tradeRepository;

    public ReviewServiceImpl(ReviewRepository reviewRepository, UsersRepository usersRepository, ReviewResponseDtoMapper reviewResponseDtoMapper, TradeRepository tradeRepository) {
        this.reviewRepository = reviewRepository;
        this.usersRepository = usersRepository;
        this.reviewResponseDtoMapper = reviewResponseDtoMapper;
        this.tradeRepository = tradeRepository;
    }

    @Transactional(readOnly = true)
    @Override
    public List<ReviewResponseDto> getAllMyReviews(UserDetails userDetails) {
        log.info("Getting all reviews for user {}", userDetails.getUsername());
        Users user = usersRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> {
                    log.info("User {} not found while fetching reviews" , userDetails.getUsername());
                    return new UserNotFoundException("User " + userDetails.getUsername());
                });

        List<Review> reviews = reviewRepository.findAllByReviewedUserOrReviewer(user, user);

        if(reviews.isEmpty()) {
            log.info("No reviews found for user {}", userDetails.getUsername());
            return Collections.emptyList();
        }
        log.info("Found {} reviews for user {}", reviews.size(), userDetails.getUsername());
        return reviews.stream().map(reviewResponseDtoMapper::toDto).toList();

    }

    @Transactional
    @Override
    public ReviewResponseDto postReview(UserDetails userDetails, UUID tradeId, ReviewRequestDto reviewRequestDto) {
        log.info("Creating new review");

        Users loggedInUser = usersRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new UserNotFoundException("User " + userDetails.getUsername()));
        Trade completedTrade = tradeRepository.findByTradeId(tradeId)
                .orElseThrow(() -> {
                    log.info("Trade with id {} not found while creating review", tradeId);
                    return new EntityNotFoundException("Trade not found");
                });

        var seller  = completedTrade.getSeller();
        var buyer = completedTrade.getBuyer();

        if(!loggedInUser.equals(buyer)&&!loggedInUser.equals(seller)) {
            log.warn("Logged in user is not a part of this trade and can't post this review");
            throw new TradeAccessDenied("Logged in user is not a part of this trade and not able to post this review" + userDetails.getUsername());
        }
        if(!completedTrade.getStatus().equals(TradeStatus.COMPLETED)) {
            log.warn("Trade {} is not in completed status", completedTrade.getId());
            throw new ConflictException("Trade " + completedTrade.getId() + " is not in completed status");
        }
        if (reviewRepository.existsByTradeAndReviewer(completedTrade, loggedInUser)) {
            log.warn("User {} has already reviewed this trade {}", loggedInUser.getUsername(), tradeId);
            throw new TradeReviewedConflictException("You have already reviewed this trade");
        }

        Users reviewedUser = loggedInUser.equals(buyer) ? seller : buyer;

            log.info("User {} is reviewing user {}", loggedInUser.getUsername(), reviewedUser.getUsername());
            var review = Review.builder()
                    .reviewer(loggedInUser)
                    .reviewedUser(reviewedUser)
                    .trade(completedTrade)
                    .comment(reviewRequestDto.comment())
                    .rating(reviewRequestDto.rating())
                    .build();
         Review savedReview = reviewRepository.save(review);
        log.info("User {} successfully posted review", loggedInUser.getUsername());
            if(loggedInUser.equals(buyer)) {
                completedTrade.setBuyerLeftReview(true);
            }else {
                completedTrade.setSellerLeftReview(true);
            }
            this.updateUserRating(reviewedUser);
            tradeRepository.save(completedTrade);

        return reviewResponseDtoMapper.toDto(savedReview);
    }

    @Transactional
    @Override
    public void deleteReviewByAdmin(UserDetails userDetails, UUID reviewId) {
        log.info("Deleting  review");
        Users user = usersRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new UserNotFoundException("User not found "));
        log.info("User {} with role {} has been found ", userDetails.getUsername(), user.getRole());
        Review review = reviewRepository.findByReviewId(reviewId)
                .orElseThrow(() -> new EntityNotFoundException("Review with id " + reviewId + " not found"));
        log.info("Review {} with id {} has been found ", review, review.getId());

        reviewRepository.delete(review);
        log.info("User {} successfully deleted review {}", userDetails.getUsername(), review.getId());

    }
    private void updateUserRating(Users user){
        log.info("Updating user rating");
        List<Review> reviews = reviewRepository.findAllByReviewedUser(user);

        if(reviews.isEmpty()) {
            log.info("No reviews found for user {}", user.getId());
            user.setAverageRating(0.0);
            user.setRatingCount(0);
        }else {
            log.info("Found {} reviews for user {}", reviews.size(), user.getId());
            double ratingSum = reviews.stream().mapToDouble(Review::getRating).sum();
            double averageRating = ratingSum / reviews.size();
            double roundedAverage = Math.round(averageRating * 10.0) / 10.0;
            user.setAverageRating(roundedAverage);
            user.setRatingCount(reviews.size());
        }

        usersRepository.save(user);
        log.info("User {} successfully updated user rating {}", user.getId(), user.getAverageRating());
    }
}


