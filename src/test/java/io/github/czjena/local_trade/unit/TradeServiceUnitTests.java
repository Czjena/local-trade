package io.github.czjena.local_trade.unit;

import io.github.czjena.local_trade.enums.TradeStatus;
import io.github.czjena.local_trade.model.Advertisement;
import io.github.czjena.local_trade.model.Trade;
import io.github.czjena.local_trade.model.Users;
import io.github.czjena.local_trade.repository.AdvertisementRepository;
import io.github.czjena.local_trade.repository.TradeRepository;
import io.github.czjena.local_trade.repository.UsersRepository;
import io.github.czjena.local_trade.service.AdvertisementSecurityService;
import io.github.czjena.local_trade.service.TradeService;
import io.github.czjena.local_trade.testutils.AdUtils;
import io.github.czjena.local_trade.testutils.UserUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TradeServiceUnitTests {
    @Mock
    AdvertisementSecurityService advertisementSecurityService;
    @InjectMocks
    TradeService tradeService;
    @Mock
    TradeRepository tradeRepository;
    @Mock
    AdvertisementRepository advertisementRepository;
    @Mock
    UsersRepository usersRepository;

    @Test
    public void tradeInitiation_thenReturnCreatedTrade() {
        Users buyer = UserUtils.createUserRoleUser();
        Users seller = UserUtils.createUserRoleUser();
        buyer.setEmail("buyer@gmail.com");
        seller.setEmail("seller@gmail.com");
        buyer.setId(1);
        seller.setId(2);
        UserDetails mockUserDetails = mock(UserDetails.class);
        Advertisement advertisement = AdUtils.createAdvertisement();
        advertisement.setUser(buyer);
        Trade  newTrade = Trade.builder()
                .seller(seller)
                .buyer(buyer)
                .advertisement(advertisement)
                .status(TradeStatus.PROPOSED)
                .sellerLeftReview(false)
                .buyerLeftReview(false)
                .build();

        when(mockUserDetails.getUsername()).thenReturn(seller.getEmail());
        when(usersRepository.findByEmail(mockUserDetails.getUsername())).thenReturn(Optional.of(seller));
        when(advertisementSecurityService.isOwner(mockUserDetails,advertisement.getAdvertisementId())).thenReturn(true);
        when(advertisementRepository.findByAdvertisementId(advertisement.getAdvertisementId())).thenReturn(Optional.of(advertisement));
        when(tradeRepository.existsByAdvertisementAndBuyer(advertisement,buyer)).thenReturn(false);
        when(tradeRepository.save(any(Trade.class))).thenReturn(newTrade);

        Trade trade = tradeService.tradeInitiation(mockUserDetails,buyer,advertisement.getAdvertisementId());

        verify(tradeRepository, times(1)).save(any(Trade.class));
        Assertions.assertNotNull(trade);
        Assertions.assertEquals(TradeStatus.PROPOSED,trade.getStatus());
        Assertions.assertEquals(buyer.getId(),trade.getBuyer().getId());
        Assertions.assertEquals(seller.getId(),trade.getSeller().getId());
        Assertions.assertEquals(advertisement.getAdvertisementId(),trade.getAdvertisement().getAdvertisementId());
        Assertions.assertEquals(trade,newTrade);
    }
}
