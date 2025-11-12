package io.github.adrian.wieczorek.local_trade.unit;

import io.github.adrian.wieczorek.local_trade.enums.TradeStatus;
import io.github.adrian.wieczorek.local_trade.exceptions.ConflictException;
import io.github.adrian.wieczorek.local_trade.exceptions.TradeAccessDenied;
import io.github.adrian.wieczorek.local_trade.exceptions.TradeReviewedConflictException;
import io.github.adrian.wieczorek.local_trade.exceptions.UserNotFoundException;
import io.github.adrian.wieczorek.local_trade.mappers.ReviewResponseDtoMapper;
import io.github.adrian.wieczorek.local_trade.model.ReviewEntity;
import io.github.adrian.wieczorek.local_trade.model.TradeEntity;
import io.github.adrian.wieczorek.local_trade.model.UsersEntity;
import io.github.adrian.wieczorek.local_trade.repository.ReviewRepository;
import io.github.adrian.wieczorek.local_trade.repository.TradeRepository;
import io.github.adrian.wieczorek.local_trade.repository.UsersRepository;
import io.github.adrian.wieczorek.local_trade.request.ReviewRequestDto;
import io.github.adrian.wieczorek.local_trade.response.ReviewResponseDto;
import io.github.adrian.wieczorek.local_trade.service.business.ReviewServiceImpl;
import io.github.adrian.wieczorek.local_trade.testutils.UserUtils;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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
    private ReviewEntity reviewEntity;
    private TradeEntity tradeEntity;
    private UsersEntity reviewer;
    private UsersEntity reviewedUser;
    private ReviewResponseDto reviewResponseDto;

    @BeforeEach
    void setUp() {
        tradeEntity = new TradeEntity();
        userDetails = mock(UserDetails.class);
        reviewer = UserUtils.createUserRoleUser();
        reviewedUser = UserUtils.createUserRoleUser();
        UUID reviewId = UUID.randomUUID();

        reviewEntity = new ReviewEntity(1L, tradeEntity, reviewer, reviewedUser, reviewId, 5, "good");

        reviewResponseDto = new ReviewResponseDto(reviewEntity.getRating(), reviewEntity.getComment(), reviewId);

    }

    @Test
    public void getAllMyReviews_thenReturnsAllReviews() {

        List<ReviewEntity> reviewEntities = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            reviewEntities.add(reviewEntity);
        }

        when(userDetails.getUsername()).thenReturn(reviewedUser.getUsername());
        when(usersRepository.findByEmail(userDetails.getUsername())).thenReturn(Optional.of(reviewedUser));
        when(reviewRepository.findAllByReviewedUserOrReviewer(reviewedUser, reviewedUser)).thenReturn(reviewEntities);
        when(reviewResponseDtoMapper.toDto(any(ReviewEntity.class))).thenReturn(reviewResponseDto);

        var result = reviewService.getAllMyReviews(userDetails);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(5, result.size());
        Assertions.assertEquals(reviewEntity.getComment(), result.get(0).comment());
    }

    @Test
    public void getAllMyReviews_returnsEmptyList_whenNoReviews() {
        when(userDetails.getUsername()).thenReturn(reviewedUser.getUsername());
        when(usersRepository.findByEmail(userDetails.getUsername())).thenReturn(Optional.of(reviewedUser));
        when(reviewRepository.findAllByReviewedUserOrReviewer(reviewedUser, reviewedUser)).thenReturn(Collections.emptyList());

        var result = reviewService.getAllMyReviews(userDetails);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(0, result.size());

        verify(reviewResponseDtoMapper, never()).toDto(any(ReviewEntity.class));

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
        tradeEntity.setSeller(reviewer);
        tradeEntity.setBuyer(reviewedUser);
        tradeEntity.setStatus(TradeStatus.COMPLETED);
        reviewer.setId(2);
        reviewer.setEmail("seller@seller.com");
        reviewedUser.setId(1);
        reviewedUser.setEmail("buyer@buyer.com");

        var reviewRequestDto = new ReviewRequestDto(reviewEntity.getRating(), reviewEntity.getComment());

        when(userDetails.getUsername()).thenReturn(reviewedUser.getUsername());
        when(usersRepository.findByEmail(userDetails.getUsername())).thenReturn(Optional.of(reviewedUser));
        when(tradeRepository.findByTradeId(tradeEntity.getTradeId())).thenReturn(Optional.of(tradeEntity));
        when(reviewRepository.existsByTradeEntityAndReviewer(tradeEntity, reviewedUser)).thenReturn(Boolean.FALSE);
        when(reviewRepository.findAllByReviewedUser(reviewer)).thenReturn(List.of(reviewEntity));
        when(reviewResponseDtoMapper.toDto(any(ReviewEntity.class))).thenReturn(reviewResponseDto);

        ArgumentCaptor<ReviewEntity> reviewCaptor = ArgumentCaptor.forClass(ReviewEntity.class);
        ArgumentCaptor<TradeEntity> tradeCaptor = ArgumentCaptor.forClass(TradeEntity.class);
        ArgumentCaptor<UsersEntity> userCaptor = ArgumentCaptor.forClass(UsersEntity.class);

        when(reviewRepository.save(reviewCaptor.capture())).thenReturn(reviewEntity);
        when(tradeRepository.save(tradeCaptor.capture())).thenReturn(tradeEntity);
        when(usersRepository.save(userCaptor.capture())).thenReturn(reviewer);

        var result = reviewService.postReview(userDetails, tradeEntity.getTradeId(), reviewRequestDto);


        Assertions.assertNotNull(result);
        Assertions.assertEquals(5, result.rating());
        Assertions.assertEquals("good", result.comment());

        // 2. Sprawdź, co zostało zapisane w recenzji
        ReviewEntity capturedReviewEntity = reviewCaptor.getValue();
        Assertions.assertEquals(reviewedUser, capturedReviewEntity.getReviewer());
        Assertions.assertEquals(reviewer, capturedReviewEntity.getReviewedUser());
        Assertions.assertEquals(tradeEntity, capturedReviewEntity.getTradeEntity());

        TradeEntity capturedTradeEntity = tradeCaptor.getValue();
        Assertions.assertTrue(capturedTradeEntity.isBuyerLeftReview());
        Assertions.assertFalse(capturedTradeEntity.isSellerLeftReview());

        UsersEntity capturedUser = userCaptor.getValue();
        Assertions.assertEquals(reviewer, capturedUser);
        Assertions.assertEquals(1, capturedUser.getRatingCount());
        Assertions.assertEquals(5.0, capturedUser.getAverageRating());

        verify(reviewRepository, times(1)).save(any(ReviewEntity.class));
        verify(tradeRepository, times(1)).save(any(TradeEntity.class));
        verify(usersRepository, times(1)).save(any(UsersEntity.class));
    }

    @Test
    public void postReview_thenUserIsNotFound_throwsUserNotFoundException() {
        var reviewRequestDto = new ReviewRequestDto(reviewEntity.getRating(), reviewEntity.getComment());

        when(userDetails.getUsername()).thenReturn(reviewedUser.getUsername());
        when(usersRepository.findByEmail(userDetails.getUsername())).thenReturn(Optional.empty());

        Assertions.assertThrows(UserNotFoundException.class, () -> reviewService.postReview(userDetails, tradeEntity.getTradeId(), reviewRequestDto));

        verify(reviewRepository, never()).save(any(ReviewEntity.class));
        verify(tradeRepository, never()).save(any(TradeEntity.class));
        verify(usersRepository, never()).save(any(UsersEntity.class));
    }

    @Test
    public void postReview_thenTradeIsNotFound_throwsEntityNotFoundException() {
        var reviewRequestDto = new ReviewRequestDto(reviewEntity.getRating(), reviewEntity.getComment());

        when(userDetails.getUsername()).thenReturn(reviewedUser.getUsername());
        when(usersRepository.findByEmail(userDetails.getUsername())).thenReturn(Optional.of(reviewedUser));
        when(tradeRepository.findByTradeId(tradeEntity.getTradeId())).thenReturn(Optional.empty());

        Assertions.assertThrows(EntityNotFoundException.class, () -> reviewService.postReview(userDetails, tradeEntity.getTradeId(), reviewRequestDto));

        verify(reviewRepository, never()).save(any(ReviewEntity.class));
        verify(tradeRepository, never()).save(any(TradeEntity.class));
        verify(usersRepository, never()).save(any(UsersEntity.class));
    }

    @Test
    public void postReview_thenUserIsTheSameAs_throwsEntityNotFoundException() {
        var stranger = UserUtils.createUserRoleUser();
        stranger.setId(999);
        stranger.setEmail("imnotapartofthistrade@gmail.com");
        var reviewRequestDto = new ReviewRequestDto(reviewEntity.getRating(), reviewEntity.getComment());
        tradeEntity.setStatus(TradeStatus.COMPLETED);

        tradeEntity.setSeller(stranger);
        tradeEntity.setBuyer(stranger);

        when(userDetails.getUsername()).thenReturn(reviewedUser.getUsername());
        when(usersRepository.findByEmail(userDetails.getUsername())).thenReturn(Optional.of(reviewedUser));
        when(tradeRepository.findByTradeId(tradeEntity.getTradeId())).thenReturn(Optional.of(tradeEntity));

        Assertions.assertThrows(TradeAccessDenied.class, () -> reviewService.postReview(userDetails, tradeEntity.getTradeId(), reviewRequestDto));

        verify(reviewRepository, never()).save(any(ReviewEntity.class));
        verify(tradeRepository, never()).save(any(TradeEntity.class));
        verify(usersRepository, never()).save(any(UsersEntity.class));
    }
    @Test
    public void postReview_thenTradeStatusIsNotComplete_throwsSecurityException() {
        var reviewRequestDto = new ReviewRequestDto(reviewEntity.getRating(), reviewEntity.getComment());
        tradeEntity.setSeller(reviewedUser);
        tradeEntity.setStatus(TradeStatus.CANCELLED);

        when(userDetails.getUsername()).thenReturn(reviewedUser.getUsername());
        when(usersRepository.findByEmail(userDetails.getUsername())).thenReturn(Optional.of(reviewedUser));
        when(tradeRepository.findByTradeId(tradeEntity.getTradeId())).thenReturn(Optional.of(tradeEntity));

        Assertions.assertThrows(ConflictException.class, () -> reviewService.postReview(userDetails, tradeEntity.getTradeId(), reviewRequestDto));

        verify(reviewRepository, never()).save(any(ReviewEntity.class));
        verify(tradeRepository, never()).save(any(TradeEntity.class));
        verify(usersRepository, never()).save(any(UsersEntity.class));

    }

    @Test
    public void postReview_thenReviewIsAlreadyPosted_throwsSecurityException() {
        var reviewRequestDto = new ReviewRequestDto(reviewEntity.getRating(), reviewEntity.getComment());
        tradeEntity.setSeller(reviewedUser);
        tradeEntity.setStatus(TradeStatus.COMPLETED);
        when(userDetails.getUsername()).thenReturn(reviewedUser.getUsername());
        when(usersRepository.findByEmail(userDetails.getUsername())).thenReturn(Optional.of(reviewedUser));
        when(tradeRepository.findByTradeId(tradeEntity.getTradeId())).thenReturn(Optional.of(tradeEntity));
        when(reviewRepository.existsByTradeEntityAndReviewer(tradeEntity, reviewedUser)).thenReturn(Boolean.TRUE);

        Assertions.assertThrows(TradeReviewedConflictException.class, () -> reviewService.postReview(userDetails, tradeEntity.getTradeId(), reviewRequestDto));

        verify(reviewRepository, never()).save(any(ReviewEntity.class));
        verify(tradeRepository, never()).save(any(TradeEntity.class));
        verify(usersRepository, never()).save(any(UsersEntity.class));

    }
    @Test
    public void deleteReview_thenReviewIsDeleted() {
        when(userDetails.getUsername()).thenReturn(reviewedUser.getUsername());
        when(usersRepository.findByEmail(userDetails.getUsername())).thenReturn(Optional.of(reviewedUser));
        when(reviewRepository.findByReviewId(reviewEntity.getReviewId())).thenReturn(Optional.of(reviewEntity));

        reviewService.deleteReviewByAdmin(userDetails, reviewEntity.getReviewId());
        verify(reviewRepository, times(1)).delete(any(ReviewEntity.class));

    }
    @Test
    public void deleteReviewWithBadUser_thenReviewIsNotDeleted() {
        when(userDetails.getUsername()).thenReturn(reviewedUser.getUsername());
        when(usersRepository.findByEmail(userDetails.getUsername())).thenReturn(Optional.empty());

        Assertions.assertThrows(UserNotFoundException.class, () -> reviewService.deleteReviewByAdmin(userDetails, reviewEntity.getReviewId()));

        verify(reviewRepository, never()).delete(any(ReviewEntity.class));

    }
    @Test
    public void deleteReviewButReviewIsNotPresent_throwsEntityException() {
        when(userDetails.getUsername()).thenReturn(reviewedUser.getUsername());
        when(usersRepository.findByEmail(userDetails.getUsername())).thenReturn(Optional.of(reviewedUser));
        when(reviewRepository.findByReviewId(reviewEntity.getReviewId())).thenReturn(Optional.empty());


        Assertions.assertThrows(EntityNotFoundException.class, () -> reviewService.deleteReviewByAdmin(userDetails, reviewEntity.getReviewId()));

        verify(reviewRepository, never()).delete(any(ReviewEntity.class));

    }

}

