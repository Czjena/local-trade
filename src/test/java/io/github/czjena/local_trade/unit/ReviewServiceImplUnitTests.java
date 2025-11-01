package io.github.czjena.local_trade.unit;

import io.github.czjena.local_trade.enums.TradeStatus;
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
import io.github.czjena.local_trade.service.ReviewServiceImpl;
import io.github.czjena.local_trade.testutils.UserUtils;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class ReviewServiceImplUnitTests {
    @InjectMocks
    private ReviewServiceImpl reviewService;
    @Mock
    private ReviewRepository reviewRepository;
    @Mock
    UsersRepository usersRepository;
    @Mock
    ReviewResponseDtoMapper reviewResponseDtoMapper;
    @Mock
    TradeRepository tradeRepository;

    private UserDetails userDetails;
    private Review review;
    private Trade trade;
    private Users reviewer;
    private Users reviewedUser;
    private ReviewResponseDto reviewResponseDto;

    @BeforeEach
    void setUp() {
        trade = new Trade();
        userDetails = mock(UserDetails.class);
        reviewer = UserUtils.createUserRoleUser();
        reviewedUser = UserUtils.createUserRoleUser();
        UUID reviewId = UUID.randomUUID();

        review = new Review(1L, trade, reviewer, reviewedUser, reviewId, 5, "good");

        reviewResponseDto = new ReviewResponseDto(review.getRating(), review.getComment(), reviewId);

    }

    @Test
    public void getAllMyReviews_thenReturnsAllReviews() {

        List<Review> reviews = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            reviews.add(review);
        }

        when(userDetails.getUsername()).thenReturn(reviewedUser.getUsername());
        when(usersRepository.findByEmail(userDetails.getUsername())).thenReturn(Optional.of(reviewedUser));
        when(reviewRepository.findAllByReviewedUserOrReviewer(reviewedUser, reviewedUser)).thenReturn(reviews);
        when(reviewResponseDtoMapper.toDto(any(Review.class))).thenReturn(reviewResponseDto);

        var result = reviewService.getAllMyReviews(userDetails);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(5, result.size());
        Assertions.assertEquals(review.getComment(), result.get(0).comment());
    }

    @Test
    public void getAllMyReviews_returnsEmptyList_whenNoReviews() {
        when(userDetails.getUsername()).thenReturn(reviewedUser.getUsername());
        when(usersRepository.findByEmail(userDetails.getUsername())).thenReturn(Optional.of(reviewedUser));
        when(reviewRepository.findAllByReviewedUserOrReviewer(reviewedUser, reviewedUser)).thenReturn(Collections.emptyList());

        var result = reviewService.getAllMyReviews(userDetails);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(0, result.size());

        verify(reviewResponseDtoMapper, never()).toDto(any(Review.class));

    }

    @Test
    public void getAllMyReviews_NoUserFound_throwsUserNotFoundException() {
        when(userDetails.getUsername()).thenReturn(reviewedUser.getUsername());
        when(usersRepository.findByEmail(userDetails.getUsername())).thenReturn(Optional.empty());
        Assertions.assertThrows(UserNotFoundException.class, () -> reviewService.getAllMyReviews(userDetails));
        verify(reviewRepository, never()).findAllByReviewedUserOrReviewer(any(), any());
        verify(reviewResponseDtoMapper, never()).toDto(any());
    }

    @Test
    public void postReview_thenReviewIsPosted_returnsReview() {
        trade.setSeller(reviewer);
        trade.setBuyer(reviewedUser);
        trade.setStatus(TradeStatus.COMPLETED);
        reviewer.setId(2);
        reviewer.setEmail("seller@seller.com");
        reviewedUser.setId(1);
        reviewedUser.setEmail("buyer@buyer.com");

        var reviewRequestDto = new ReviewRequestDto(review.getRating(), review.getComment());

        when(userDetails.getUsername()).thenReturn(reviewedUser.getUsername());
        when(usersRepository.findByEmail(userDetails.getUsername())).thenReturn(Optional.of(reviewedUser));
        when(tradeRepository.findByTradeId(trade.getTradeId())).thenReturn(Optional.of(trade));
        when(reviewRepository.existsByTradeAndReviewer(trade, reviewedUser)).thenReturn(Boolean.FALSE);
        when(reviewRepository.findAllByReviewedUser(reviewer)).thenReturn(List.of(review));
        when(reviewResponseDtoMapper.toDto(any(Review.class))).thenReturn(reviewResponseDto);

        ArgumentCaptor<Review> reviewCaptor = ArgumentCaptor.forClass(Review.class);
        ArgumentCaptor<Trade> tradeCaptor = ArgumentCaptor.forClass(Trade.class);
        ArgumentCaptor<Users> userCaptor = ArgumentCaptor.forClass(Users.class);

        when(reviewRepository.save(reviewCaptor.capture())).thenReturn(review);
        when(tradeRepository.save(tradeCaptor.capture())).thenReturn(trade);
        when(usersRepository.save(userCaptor.capture())).thenReturn(reviewer);

        var result = reviewService.postReview(userDetails, trade.getTradeId(), reviewRequestDto);


        Assertions.assertNotNull(result);
        Assertions.assertEquals(5, result.rating());
        Assertions.assertEquals("good", result.comment());

        // 2. Sprawdź, co zostało zapisane w recenzji
        Review capturedReview = reviewCaptor.getValue();
        Assertions.assertEquals(reviewedUser, capturedReview.getReviewer());
        Assertions.assertEquals(reviewer, capturedReview.getReviewedUser());
        Assertions.assertEquals(trade, capturedReview.getTrade());

        Trade capturedTrade = tradeCaptor.getValue();
        Assertions.assertTrue(capturedTrade.isBuyerLeftReview());
        Assertions.assertFalse(capturedTrade.isSellerLeftReview());

        Users capturedUser = userCaptor.getValue();
        Assertions.assertEquals(reviewer, capturedUser);
        Assertions.assertEquals(1, capturedUser.getRatingCount());
        Assertions.assertEquals(5.0, capturedUser.getAverageRating());

        verify(reviewRepository, times(1)).save(any(Review.class));
        verify(tradeRepository, times(1)).save(any(Trade.class));
        verify(usersRepository, times(1)).save(any(Users.class));
    }

    @Test
    public void postReview_thenUserIsNotFound_throwsUserNotFoundException() {
        var reviewRequestDto = new ReviewRequestDto(review.getRating(), review.getComment());

        when(userDetails.getUsername()).thenReturn(reviewedUser.getUsername());
        when(usersRepository.findByEmail(userDetails.getUsername())).thenReturn(Optional.empty());

        Assertions.assertThrows(UserNotFoundException.class, () -> reviewService.postReview(userDetails, trade.getTradeId(), reviewRequestDto));

        verify(reviewRepository, never()).save(any(Review.class));
        verify(tradeRepository, never()).save(any(Trade.class));
        verify(usersRepository, never()).save(any(Users.class));
    }

    @Test
    public void postReview_thenTradeIsNotFound_throwsEntityNotFoundException() {
        var reviewRequestDto = new ReviewRequestDto(review.getRating(), review.getComment());

        when(userDetails.getUsername()).thenReturn(reviewedUser.getUsername());
        when(usersRepository.findByEmail(userDetails.getUsername())).thenReturn(Optional.of(reviewedUser));
        when(tradeRepository.findByTradeId(trade.getTradeId())).thenReturn(Optional.empty());

        Assertions.assertThrows(EntityNotFoundException.class, () -> reviewService.postReview(userDetails, trade.getTradeId(), reviewRequestDto));

        verify(reviewRepository, never()).save(any(Review.class));
        verify(tradeRepository, never()).save(any(Trade.class));
        verify(usersRepository, never()).save(any(Users.class));
    }

    @Test
    public void postReview_thenUserIsTheSameAs_throwsEntityNotFoundException() {
        var stranger = UserUtils.createUserRoleUser();
        stranger.setId(999);
        stranger.setEmail("imnotapartofthistrade@gmail.com");
        var reviewRequestDto = new ReviewRequestDto(review.getRating(), review.getComment());
        trade.setStatus(TradeStatus.COMPLETED);

        trade.setSeller(stranger);
        trade.setBuyer(stranger);

        when(userDetails.getUsername()).thenReturn(reviewedUser.getUsername());
        when(usersRepository.findByEmail(userDetails.getUsername())).thenReturn(Optional.of(reviewedUser));
        when(tradeRepository.findByTradeId(trade.getTradeId())).thenReturn(Optional.of(trade));

        Assertions.assertThrows(SecurityException.class, () -> reviewService.postReview(userDetails, trade.getTradeId(), reviewRequestDto));

        verify(reviewRepository, never()).save(any(Review.class));
        verify(tradeRepository, never()).save(any(Trade.class));
        verify(usersRepository, never()).save(any(Users.class));
    }
    @Test
    public void postReview_thenTradeStatusIsNotComplete_throwsSecurityException() {
        var reviewRequestDto = new ReviewRequestDto(review.getRating(), review.getComment());
        trade.setSeller(reviewedUser);
        trade.setStatus(TradeStatus.CANCELLED);

        when(userDetails.getUsername()).thenReturn(reviewedUser.getUsername());
        when(usersRepository.findByEmail(userDetails.getUsername())).thenReturn(Optional.of(reviewedUser));
        when(tradeRepository.findByTradeId(trade.getTradeId())).thenReturn(Optional.of(trade));

        Assertions.assertThrows(SecurityException.class, () -> reviewService.postReview(userDetails, trade.getTradeId(), reviewRequestDto));

        verify(reviewRepository, never()).save(any(Review.class));
        verify(tradeRepository, never()).save(any(Trade.class));
        verify(usersRepository, never()).save(any(Users.class));

    }

    @Test
    public void postReview_thenReviewIsAlreadyPosted_throwsSecurityException() {
        var reviewRequestDto = new ReviewRequestDto(review.getRating(), review.getComment());
        trade.setSeller(reviewedUser);
        trade.setStatus(TradeStatus.COMPLETED);
        when(userDetails.getUsername()).thenReturn(reviewedUser.getUsername());
        when(usersRepository.findByEmail(userDetails.getUsername())).thenReturn(Optional.of(reviewedUser));
        when(tradeRepository.findByTradeId(trade.getTradeId())).thenReturn(Optional.of(trade));
        when(reviewRepository.existsByTradeAndReviewer(trade, reviewedUser)).thenReturn(Boolean.TRUE);

        Assertions.assertThrows(IllegalStateException.class, () -> reviewService.postReview(userDetails, trade.getTradeId(), reviewRequestDto));

        verify(reviewRepository, never()).save(any(Review.class));
        verify(tradeRepository, never()).save(any(Trade.class));
        verify(usersRepository, never()).save(any(Users.class));

    }
    @Test
    public void deleteReview_thenReviewIsDeleted() {
        when(userDetails.getUsername()).thenReturn(reviewedUser.getUsername());
        when(usersRepository.findByEmail(userDetails.getUsername())).thenReturn(Optional.of(reviewedUser));
        when(reviewRepository.findByReviewId(review.getReviewId())).thenReturn(Optional.of(review));

        reviewService.deleteReviewByAdmin(userDetails, review.getReviewId());
        verify(reviewRepository, times(1)).delete(any(Review.class));

    }
    @Test
    public void deleteReviewWithBadUser_thenReviewIsNotDeleted() {
        when(userDetails.getUsername()).thenReturn(reviewedUser.getUsername());
        when(usersRepository.findByEmail(userDetails.getUsername())).thenReturn(Optional.empty());

        Assertions.assertThrows(UserNotFoundException.class, () -> reviewService.deleteReviewByAdmin(userDetails, review.getReviewId()));

        verify(reviewRepository, never()).delete(any(Review.class));

    }
    @Test
    public void deleteReviewButReviewIsNotPresent_throwsEntityException() {
        when(userDetails.getUsername()).thenReturn(reviewedUser.getUsername());
        when(usersRepository.findByEmail(userDetails.getUsername())).thenReturn(Optional.of(reviewedUser));
        when(reviewRepository.findByReviewId(review.getReviewId())).thenReturn(Optional.empty());


        Assertions.assertThrows(EntityNotFoundException.class, () -> reviewService.deleteReviewByAdmin(userDetails, review.getReviewId()));

        verify(reviewRepository, never()).delete(any(Review.class));

    }

}

