package io.github.czjena.local_trade.unit;

import io.github.czjena.local_trade.enums.TradeStatus;
import io.github.czjena.local_trade.exceptions.UserNotFoundException;
import io.github.czjena.local_trade.mappers.TradeResponseDtoMapper;
import io.github.czjena.local_trade.model.Advertisement;
import io.github.czjena.local_trade.model.Trade;
import io.github.czjena.local_trade.model.Users;
import io.github.czjena.local_trade.repository.AdvertisementRepository;
import io.github.czjena.local_trade.repository.TradeRepository;
import io.github.czjena.local_trade.repository.UsersRepository;
import io.github.czjena.local_trade.request.TradeInitiationRequestDto;
import io.github.czjena.local_trade.response.SimpleAdvertisementResponseDto;
import io.github.czjena.local_trade.response.SimpleUserResponseDto;
import io.github.czjena.local_trade.response.TradeResponseDto;
import io.github.czjena.local_trade.service.TradeService;
import io.github.czjena.local_trade.service.TradeServiceImpl;
import io.github.czjena.local_trade.testutils.AdUtils;
import io.github.czjena.local_trade.testutils.UserUtils;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.mockito.Mockito.any;

@ExtendWith(MockitoExtension.class)
public class TradeServiceImplUnitTests {
    @InjectMocks
    TradeServiceImpl tradeService;
    @Mock
    TradeRepository tradeRepository;
    @Mock
    AdvertisementRepository advertisementRepository;
    @Mock
    UsersRepository usersRepository;
    @Mock
    TradeResponseDtoMapper tradeResponseDtoMapper;

    private Users seller;
    private Users buyer;
    private Trade newTrade;
    private Advertisement advertisement;
    private UserDetails mockUserDetails;
    private Users stranger;
    private TradeInitiationRequestDto tradeInitiationRequestDto;
    private TradeResponseDto tradeResponseDto;

    @BeforeEach
    public void setUp() {
        stranger = UserUtils.createUserRoleUser();
        buyer = UserUtils.createUserRoleUser();
        seller = UserUtils.createUserRoleUser();
        buyer.setEmail("buyer@gmail.com");
        seller.setEmail("seller@gmail.com");
        stranger.setEmail("stranger@gmail.com");
        buyer.setId(1);
        seller.setId(2);
        stranger.setId(3);
        mockUserDetails = mock(UserDetails.class);
        advertisement = AdUtils.createAdvertisement();
        advertisement.setUser(seller);
        SimpleUserResponseDto sellerSimpleUserResponseDto = new SimpleUserResponseDto(seller.getId(), seller.getEmail());
        SimpleUserResponseDto buyerSimpleUserResponseDto = new SimpleUserResponseDto(buyer.getId(), buyer.getEmail());
        SimpleAdvertisementResponseDto simpleAdvertisementResponseDto = new SimpleAdvertisementResponseDto(advertisement.getAdvertisementId(), advertisement.getTitle());
        tradeInitiationRequestDto = new TradeInitiationRequestDto(BigDecimal.valueOf(2),advertisement.getAdvertisementId());
        newTrade = Trade.builder()
                .seller(seller)
                .proposedPrice(tradeInitiationRequestDto.proposedPrice())
                .buyer(buyer)
                .advertisement(advertisement)
                .status(TradeStatus.PROPOSED)
                .sellerLeftReview(false)
                .buyerLeftReview(false)
                .build();
        tradeResponseDto = new TradeResponseDto(newTrade.getTradeId(),
                newTrade.getId(),newTrade.getStatus(),newTrade.getProposedPrice(),newTrade.getCreatedAt(), newTrade.isBuyerMarkedCompleted(),
                newTrade.isSellerMarkedCompleted(), buyerSimpleUserResponseDto, sellerSimpleUserResponseDto, simpleAdvertisementResponseDto);


    }

    @AfterEach
    public void tearDown() {
        reset(tradeRepository, tradeResponseDtoMapper);
    }


    @Test
    public void tradeInitiation_thenReturnCreatedTrade() {
        when(tradeResponseDtoMapper.tradeToTradeResponseDto(any())).thenReturn(tradeResponseDto);
        when(mockUserDetails.getUsername()).thenReturn(buyer.getEmail());
        when(usersRepository.findByEmail(mockUserDetails.getUsername())).thenReturn(Optional.of(buyer));
        when(advertisementRepository.findByAdvertisementId(advertisement.getAdvertisementId())).thenReturn(Optional.of(advertisement));
        when(tradeRepository.existsByAdvertisementAndBuyer(advertisement, buyer)).thenReturn(false);
        when(tradeRepository.save(any(Trade.class))).thenReturn(newTrade);


        var trade  = tradeService.tradeInitiation(mockUserDetails, tradeInitiationRequestDto);

        verify(tradeRepository, times(1)).save(any(Trade.class));

        Assertions.assertNotNull(trade);
        Assertions.assertEquals(TradeStatus.PROPOSED, trade.status());
        Assertions.assertEquals(buyer.getId(), trade.buyerSimpleDto().id());
        Assertions.assertEquals(seller.getId(), trade.sellerSimpleDto().id());
        Assertions.assertEquals(advertisement.getAdvertisementId(), trade.simpleAdvertisementResponseDto().advertisementId());
        Assertions.assertEquals(advertisement.getTitle(), trade.simpleAdvertisementResponseDto().title());
        Assertions.assertEquals(newTrade.getProposedPrice(),trade.proposedPrice());
        Assertions.assertEquals(newTrade.getCreatedAt(),trade.createdAt());
        Assertions.assertEquals(newTrade.isBuyerMarkedCompleted(),trade.buyerMarkedCompleted());
        Assertions.assertEquals(newTrade.isSellerMarkedCompleted(),trade.sellerMarkedCompleted());
    }

    @Test
    public void tradeInitiationAndProposedPriceIsNull_thenReturnAdvertisementPrice() {
       tradeInitiationRequestDto = new TradeInitiationRequestDto(null,advertisement.getAdvertisementId());
        when(tradeResponseDtoMapper.tradeToTradeResponseDto(any())).thenAnswer(invocation -> {
            Trade t = invocation.getArgument(0);
            return new TradeResponseDto(
                    t.getTradeId(),
                    t.getId(),
                    t.getStatus(),
                    t.getProposedPrice(),
                    t.getCreatedAt(),
                    t.isBuyerMarkedCompleted(),
                    t.isSellerMarkedCompleted(),
                    new SimpleUserResponseDto(t.getBuyer().getId(), t.getBuyer().getEmail()),
                    new SimpleUserResponseDto(t.getSeller().getId(), t.getSeller().getEmail()),
                    new SimpleAdvertisementResponseDto(t.getAdvertisement().getAdvertisementId(), t.getAdvertisement().getTitle())
            );
        });
        when(mockUserDetails.getUsername()).thenReturn(buyer.getEmail());
        when(usersRepository.findByEmail(mockUserDetails.getUsername())).thenReturn(Optional.of(buyer));
        when(advertisementRepository.findByAdvertisementId(advertisement.getAdvertisementId())).thenReturn(Optional.of(advertisement));
        when(tradeRepository.existsByAdvertisementAndBuyer(advertisement, buyer)).thenReturn(false);
        when(tradeRepository.save(any(Trade.class))).thenReturn(newTrade);


        var trade  = tradeService.tradeInitiation(mockUserDetails, tradeInitiationRequestDto);

        verify(tradeRepository, times(1)).save(any(Trade.class));
        Assertions.assertNotNull(trade);
        Assertions.assertEquals(trade.proposedPrice(), advertisement.getPrice());

    }


    @Test
    public void tradeInitiation_whenUserNotFound_thenReturnNotFoundTrade() {
        when(usersRepository.findByEmail(mockUserDetails.getUsername())).thenReturn(Optional.empty());
        Assertions.assertThrows(UserNotFoundException.class, () -> tradeService.tradeInitiation(mockUserDetails, tradeInitiationRequestDto));
        verify(tradeRepository, never()).save(any(Trade.class));
    }


    @Test
    public void tradeInitiationWithSameUser_throwsIllegalStateException() {
        when(usersRepository.findByEmail(any())).thenReturn(Optional.of(buyer));
        when(mockUserDetails.getUsername()).thenReturn(buyer.getEmail());
        when(advertisementRepository.findByAdvertisementId(any())).thenReturn(Optional.of(advertisement));
        advertisement.setUser(buyer);
        Assertions.assertThrows(IllegalArgumentException.class, () -> tradeService.tradeInitiation(mockUserDetails,  tradeInitiationRequestDto));
        verify(tradeRepository, never()).save(any(Trade.class));
    }

    @Test
    public void tradeInitiationNoAdvertisement_throwsEntityNotFoundException() {
        when(usersRepository.findByEmail(any())).thenReturn(Optional.of(buyer));
        when(advertisementRepository.findByAdvertisementId(any())).thenReturn(Optional.empty());
        Assertions.assertThrows(EntityNotFoundException.class, () -> tradeService.tradeInitiation(mockUserDetails, tradeInitiationRequestDto));
        verify(tradeRepository, never()).save(any(Trade.class));
    }
    @Test
    public void tradeInitiationTradeAlreadyExists_throwsIllegalArgumentException() {
        when(usersRepository.findByEmail(any())).thenReturn(Optional.of(buyer));
        when(advertisementRepository.findByAdvertisementId(any())).thenReturn(Optional.of(advertisement));
        when(tradeRepository.existsByAdvertisementAndBuyer(advertisement, buyer)).thenReturn(true);
        Assertions.assertThrows(IllegalArgumentException.class, () -> tradeService.tradeInitiation(mockUserDetails,  tradeInitiationRequestDto));
        verify(tradeRepository, never()).save(any(Trade.class));
    }
    @Test
    public void tradeCancelledButUserIsNotFound_throwsUserNotFoundException() {
        when(usersRepository.findByEmail(any())).thenReturn(Optional.empty());
        Assertions.assertThrows(UserNotFoundException.class, () -> tradeService.tradeIsCancelled(mockUserDetails, 2L));
        verify(tradeRepository, never()).save(any(Trade.class));
    }
    @Test
    public void tradeIsCancelled_thenTradeIsCancelled() {
        when(usersRepository.findByEmail(any())).thenReturn(Optional.of(seller));
        when(tradeRepository.findById(any())).thenReturn(Optional.of(newTrade));
        newTrade.setCreatedAt(LocalDateTime.now().minusDays(1));
        tradeService.tradeIsCancelled(mockUserDetails, 2L);
        Assertions.assertEquals(TradeStatus.CANCELLED, newTrade.getStatus());
        verify(tradeRepository, times(1)).save(any(Trade.class));
    }
    @Test
    public void tradeIsCancelledAndTradeIsProposed_throwsIllegalStateException() {
        newTrade.setStatus(TradeStatus.COMPLETED);
        when(usersRepository.findByEmail(any())).thenReturn(Optional.of(seller));
        when(tradeRepository.findById(any())).thenReturn(Optional.of(newTrade));
        Assertions.assertThrows(IllegalArgumentException.class, () -> tradeService.tradeIsCancelled(mockUserDetails, 2L));
        verify(tradeRepository, never()).save(any(Trade.class));
    }
    @Test
    public void tradeIsCancelledAndSellerIsTheSameAsBuyer_throwsSecurityException() {
        when(usersRepository.findByEmail(any())).thenReturn(Optional.of(stranger));
        when(tradeRepository.findById(any())).thenReturn(Optional.of(newTrade));
        Assertions.assertThrows(SecurityException.class, () -> tradeService.tradeIsCancelled(mockUserDetails, 2L));
        verify(tradeRepository, never()).save(any(Trade.class));
    }
    @Test
    public void tradeIsCancelledAndItsTooSoon_throwsIllegalArgumentException() {
        when(usersRepository.findByEmail(any())).thenReturn(Optional.of(seller));
        when(tradeRepository.findById(any())).thenReturn(Optional.of(newTrade));
        newTrade.setCreatedAt(LocalDateTime.now());
        Assertions.assertThrows(IllegalArgumentException.class, () -> tradeService.tradeIsCancelled(mockUserDetails, 2L));
        verify(tradeRepository, never()).save(any(Trade.class));
    }
    @Test
    public void tradeIsCompleted_thenTradeIsCompleted() {
        newTrade.setSellerMarkedCompleted(true);
        newTrade.setCreatedAt(LocalDateTime.now().minusDays(1));

        when(usersRepository.findByEmail(any())).thenReturn(Optional.of(buyer));
        when(tradeRepository.findById(any())).thenReturn(Optional.of(newTrade));

        newTrade.setCreatedAt(LocalDateTime.now().minusDays(1));
        tradeService.tradeIsComplete(mockUserDetails, 2L);
        Assertions.assertEquals(TradeStatus.COMPLETED, newTrade.getStatus());
        Assertions.assertTrue(newTrade.isBuyerMarkedCompleted());
        verify(tradeRepository, times(1)).save(newTrade);
    }

    @Test
    public void tradeIsCompletedAndTradeStatusIsWrong_throwsIllegalStateException() {
        when(usersRepository.findByEmail(any())).thenReturn(Optional.of(buyer));
        when(tradeRepository.findById(any())).thenReturn(Optional.of(newTrade));
        newTrade.setStatus(TradeStatus.PROCESSING);
        Assertions.assertThrows(IllegalArgumentException.class, () -> tradeService.tradeIsComplete(mockUserDetails, 2L));
        verify(tradeRepository, never()).save(any(Trade.class));
    }
    @Test
    public void tradeIsCompletedUserNotFound_throwsUserNotFoundException() {
        when(usersRepository.findByEmail(any())).thenReturn(Optional.empty());
        when(tradeRepository.findById(any())).thenReturn(Optional.of(newTrade));
        Assertions.assertThrows(UserNotFoundException.class, () -> tradeService.tradeIsComplete(mockUserDetails, 2L));
        verify(tradeRepository, never()).save(any(Trade.class));
    }
    @Test
    public void tradeIsCompletedTradeIsNotFound_throwsEntityNotFoundException() {
        when(tradeRepository.findById(any())).thenReturn(Optional.empty());
        Assertions.assertThrows(EntityNotFoundException.class, () -> tradeService.tradeIsComplete(mockUserDetails, 2L));
        verify(tradeRepository, never()).save(
                any(Trade.class));
    }
    @Test
    public void tradeIsCompletedAndItsTooSoon_thenTradeStatusIsNotChanged() {
        when(usersRepository.findByEmail(any())).thenReturn(Optional.of(buyer));
        when(tradeRepository.findById(any())).thenReturn(Optional.of(newTrade));
        newTrade.setBuyerMarkedCompleted(true);
        newTrade.setSellerMarkedCompleted(true);
        newTrade.setCreatedAt(LocalDateTime.now());
        tradeService.tradeIsComplete(mockUserDetails, 2L);
        Assertions.assertEquals(TradeStatus.PROPOSED,newTrade.getStatus());
    }
    @Test
    public void getAllMyTrades_returnsAllTradesIntoAList() {
        List<Trade> trades = new ArrayList<>();
        for(int i = 0; i < 10; i++) {
            trades.add(newTrade);
        }

        when(mockUserDetails.getUsername()).thenReturn(buyer.getUsername());
        when(usersRepository.findByEmail(any())).thenReturn(Optional.of(buyer));
        when(tradeRepository.findAllByBuyerOrSeller(any(Users.class),any(Users.class))).thenReturn(trades);
        when(tradeResponseDtoMapper.tradeToTradeResponseDto(any(Trade.class)))
                .thenReturn(tradeResponseDto);


        List<TradeResponseDto> result = tradeService.getAllMyTrades(mockUserDetails);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(trades.size(), result.size());
        Assertions.assertEquals(trades.get(0).getId(), result.get(0).id());
    }
    @Test
    public void getAllMyTradesWithNoUsers_throwsUserNotFoundException() {
        when(usersRepository.findByEmail(any())).thenReturn(Optional.empty());
        Assertions.assertThrows(UserNotFoundException.class, () -> tradeService.getAllMyTrades(mockUserDetails));
        verify(tradeRepository, never()).save(any(Trade.class));
    }
    @Test
    public void getAllMyTrades_returnsEmptyList() {
        List<Trade> trades = new ArrayList<>();

        when(mockUserDetails.getUsername()).thenReturn(buyer.getUsername());
        when(usersRepository.findByEmail(any())).thenReturn(Optional.of(buyer));
        when(tradeRepository.findAllByBuyerOrSeller(any(Users.class),any(Users.class))).thenReturn(trades);

        List<TradeResponseDto> result = tradeService.getAllMyTrades(mockUserDetails);

        Assertions.assertNotNull(result);
        verify(tradeResponseDtoMapper, never()).tradeToTradeResponseDto(any(Trade.class));
    }

}
