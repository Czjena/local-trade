package io.github.czjena.local_trade.service;

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
import io.github.czjena.local_trade.response.TradeResponseDto;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TradeServiceImpl implements TradeService {

    private final AdvertisementRepository advertisementRepository;
    private final UsersRepository usersRepository;
    private final TradeRepository tradeRepository;
    private final TradeResponseDtoMapper tradeResponseDtoMapper;


    public TradeServiceImpl(TradeRepository tradeRepository, UsersRepository usersRepository, AdvertisementRepository advertisementRepository, TradeResponseDtoMapper tradeResponseDtoMapper) {
        this.usersRepository = usersRepository;
        this.advertisementRepository = advertisementRepository;
        this.tradeRepository = tradeRepository;
        this.tradeResponseDtoMapper = tradeResponseDtoMapper;

    }

    @Transactional
    @Override
    public TradeResponseDto tradeInitiation(UserDetails userDetails, TradeInitiationRequestDto tradeInitiationRequestDto) {
        Users buyer = usersRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        Advertisement advertisement = advertisementRepository.findByAdvertisementId(tradeInitiationRequestDto.advertisementId())
                .orElseThrow(() -> new EntityNotFoundException("Advertisement not found"));

        Users seller = advertisement.getUser();

        BigDecimal newPrice = Optional.ofNullable(tradeInitiationRequestDto.proposedPrice()).orElse(advertisement.getPrice());

        if(tradeRepository.existsByAdvertisementAndBuyer(advertisement,buyer)){
            throw new IllegalArgumentException("Trade already exists");
        }
        if(seller.getId().equals(buyer.getId())){
            throw new IllegalArgumentException("Seller and buyer are the same");
        }
        Trade  newTrade = Trade.builder()
                .seller(seller)
                .buyer(buyer)
                .proposedPrice(newPrice)
                .advertisement(advertisement)
                .status(TradeStatus.PROPOSED)
                .sellerLeftReview(false)
                .buyerLeftReview(false)
                .build();
        tradeRepository.save(newTrade);
        return tradeResponseDtoMapper.tradeToTradeResponseDto(newTrade);
    }

    @Transactional
    @Override
    public TradeResponseDto tradeIsComplete(UserDetails userDetails, Long tradeId) {
        Trade trade = tradeRepository.findById(tradeId).orElseThrow(() -> new EntityNotFoundException("Trade not found"));
        Users loggedInUser = usersRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        if (!trade.getStatus().equals(TradeStatus.PROPOSED)) {
            throw new IllegalArgumentException("Trade is NOT PROPOSED, Current status is " + trade.getStatus());
        }
        boolean isBuyer = loggedInUser.getId().equals(trade.getBuyer().getId());
        boolean isSeller = loggedInUser.getId().equals(trade.getSeller().getId());

        if (!isBuyer && !isSeller) {
            throw new IllegalArgumentException("Only buyer and seller can complete this trade");
        }

        if (isBuyer) {
            trade.setBuyerMarkedCompleted(true);
        } else {
            trade.setSellerMarkedCompleted(true);
        }
        if (trade.isBuyerMarkedCompleted() && trade.isSellerMarkedCompleted()) {
            if (LocalDateTime.now().isAfter(trade.getCreatedAt().plusHours(1))) {
            trade.setStatus(TradeStatus.COMPLETED);
            }
        }
        tradeRepository.save(trade);
        return tradeResponseDtoMapper.tradeToTradeResponseDto(trade);
    }

    @Transactional
    @Override
    public TradeResponseDto tradeIsCancelled(UserDetails userDetails, Long tradeId) {
            Users loggedInUser = usersRepository.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new UserNotFoundException("User not found"));
            Trade trade =  tradeRepository.findById(tradeId).orElseThrow(() -> new EntityNotFoundException("Trade not found"));
            Integer buyerId = trade.getBuyer().getId();
            Integer sellerId= trade.getSeller().getId();

            boolean isBuyer = loggedInUser.getId().equals(buyerId);
            boolean isSeller = loggedInUser.getId().equals(sellerId);

            if(!isBuyer && !isSeller){
                throw new SecurityException("User is not a part of this trade and cannot cancel it");
            }
            if(!trade.getStatus().equals(TradeStatus.PROPOSED)){
                throw new IllegalArgumentException("Trade is NOT PROPOSED, Current status is " + trade.getStatus());
            }
            if(LocalDateTime.now().isBefore(trade.getCreatedAt().plusHours(2))){
                throw new IllegalArgumentException("Trade is too new to cancel");
            }

            trade.setStatus(TradeStatus.CANCELLED);
            return tradeResponseDtoMapper.tradeToTradeResponseDto(tradeRepository.save(trade));
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
        Users user = usersRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        List<Trade> trades = tradeRepository.findAllByBuyerOrSeller(user,user);

       return  trades.stream()
                .map(tradeResponseDtoMapper::tradeToTradeResponseDto)
                .toList();

    }
}
