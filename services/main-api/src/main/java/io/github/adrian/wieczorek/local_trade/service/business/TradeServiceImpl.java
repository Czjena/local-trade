package io.github.adrian.wieczorek.local_trade.service.business;

import io.github.adrian.wieczorek.local_trade.enums.TradeStatus;
import io.github.adrian.wieczorek.local_trade.exceptions.UserNotFoundException;
import io.github.adrian.wieczorek.local_trade.mappers.TradeResponseDtoMapper;
import io.github.adrian.wieczorek.local_trade.model.AdvertisementEntity;
import io.github.adrian.wieczorek.local_trade.model.TradeEntity;
import io.github.adrian.wieczorek.local_trade.model.UsersEntity;
import io.github.adrian.wieczorek.local_trade.repository.AdvertisementRepository;
import io.github.adrian.wieczorek.local_trade.repository.TradeRepository;
import io.github.adrian.wieczorek.local_trade.repository.UsersRepository;
import io.github.adrian.wieczorek.local_trade.request.TradeInitiationRequestDto;
import io.github.adrian.wieczorek.local_trade.response.TradeResponseDto;
import io.github.adrian.wieczorek.local_trade.service.infrastructure.TradeService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TradeServiceImpl implements TradeService {

    private final AdvertisementRepository advertisementRepository;
    private final UsersRepository usersRepository;
    private final TradeRepository tradeRepository;
    private final TradeResponseDtoMapper tradeResponseDtoMapper;



    @Transactional
    @Override
    public TradeResponseDto tradeInitiation(UserDetails userDetails, TradeInitiationRequestDto tradeInitiationRequestDto) {
        UsersEntity buyer = usersRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        AdvertisementEntity advertisementEntity = advertisementRepository.findByAdvertisementId(tradeInitiationRequestDto.advertisementId())
                .orElseThrow(() -> new EntityNotFoundException("Advertisement not found"));

        UsersEntity seller = advertisementEntity.getUser();

        BigDecimal newPrice = Optional.ofNullable(tradeInitiationRequestDto.proposedPrice()).orElse(advertisementEntity.getPrice());

        if(tradeRepository.existsByAdvertisementEntityAndBuyer(advertisementEntity,buyer)){
            throw new IllegalArgumentException("Trade already exists");
        }
        if(seller.getId().equals(buyer.getId())){
            throw new IllegalArgumentException("Seller and buyer are the same");
        }
        TradeEntity newTradeEntity = TradeEntity.builder()
                .seller(seller)
                .buyer(buyer)
                .proposedPrice(newPrice)
                .advertisementEntity(advertisementEntity)
                .status(TradeStatus.PROPOSED)
                .sellerLeftReview(false)
                .buyerLeftReview(false)
                .build();
        tradeRepository.save(newTradeEntity);
        return tradeResponseDtoMapper.tradeToTradeResponseDto(newTradeEntity);
    }

    @Transactional
    @Override
    public TradeResponseDto tradeIsComplete(UserDetails userDetails, Long tradeId) {
        TradeEntity tradeEntity = tradeRepository.findById(tradeId).orElseThrow(() -> new EntityNotFoundException("Trade not found"));
        UsersEntity loggedInUser = usersRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        if (!tradeEntity.getStatus().equals(TradeStatus.PROPOSED)) {
            throw new IllegalArgumentException("Trade is NOT PROPOSED, Current status is " + tradeEntity.getStatus());
        }
        boolean isBuyer = loggedInUser.getId().equals(tradeEntity.getBuyer().getId());
        boolean isSeller = loggedInUser.getId().equals(tradeEntity.getSeller().getId());

        if (!isBuyer && !isSeller) {
            throw new IllegalArgumentException("Only buyer and seller can complete this trade");
        }

        if (isBuyer) {
            tradeEntity.setBuyerMarkedCompleted(true);
        } else {
            tradeEntity.setSellerMarkedCompleted(true);
        }
        if (tradeEntity.isBuyerMarkedCompleted() && tradeEntity.isSellerMarkedCompleted()) {
            if (LocalDateTime.now().isAfter(tradeEntity.getCreatedAt().plusHours(1))) {
            tradeEntity.setStatus(TradeStatus.COMPLETED);
            }
        }
        tradeRepository.save(tradeEntity);
        return tradeResponseDtoMapper.tradeToTradeResponseDto(tradeEntity);
    }

    @Transactional
    @Override
    public TradeResponseDto tradeIsCancelled(UserDetails userDetails, Long tradeId) {
            UsersEntity loggedInUser = usersRepository.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new UserNotFoundException("User not found"));
            TradeEntity tradeEntity =  tradeRepository.findById(tradeId).orElseThrow(() -> new EntityNotFoundException("Trade not found"));
            Integer buyerId = tradeEntity.getBuyer().getId();
            Integer sellerId= tradeEntity.getSeller().getId();

            boolean isBuyer = loggedInUser.getId().equals(buyerId);
            boolean isSeller = loggedInUser.getId().equals(sellerId);

            if(!isBuyer && !isSeller){
                throw new SecurityException("User is not a part of this trade and cannot cancel it");
            }
            if(!tradeEntity.getStatus().equals(TradeStatus.PROPOSED)){
                throw new IllegalArgumentException("Trade is NOT PROPOSED, Current status is " + tradeEntity.getStatus());
            }
            if(LocalDateTime.now().isBefore(tradeEntity.getCreatedAt().plusHours(2))){
                throw new IllegalArgumentException("Trade is too new to cancel");
            }

            tradeEntity.setStatus(TradeStatus.CANCELLED);
            return tradeResponseDtoMapper.tradeToTradeResponseDto(tradeRepository.save(tradeEntity));
    }
    @Transactional
    @Override
    public TradeResponseDto updateTradeStatus(UserDetails userDetails, Long tradeId, TradeStatus tradeStatus) {
        return switch (tradeStatus) {
            case COMPLETED -> this.tradeIsComplete(userDetails, tradeId);
            case CANCELLED -> this.tradeIsCancelled(userDetails, tradeId);
            default -> throw new IllegalArgumentException("Trade status not implemented");
        };
    }
    @Transactional(readOnly = true)
    @Override
    public List<TradeResponseDto> getAllMyTrades(UserDetails userDetails){
        UsersEntity user = usersRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        List<TradeEntity> tradeEntities = tradeRepository.findAllByBuyerOrSeller(user,user);

       return  tradeEntities.stream()
                .map(tradeResponseDtoMapper::tradeToTradeResponseDto)
                .toList();

    }
}
