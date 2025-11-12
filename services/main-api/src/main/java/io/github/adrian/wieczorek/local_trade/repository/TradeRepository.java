package io.github.adrian.wieczorek.local_trade.repository;

import io.github.adrian.wieczorek.local_trade.model.AdvertisementEntity;
import io.github.adrian.wieczorek.local_trade.model.TradeEntity;
import io.github.adrian.wieczorek.local_trade.model.UsersEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TradeRepository extends JpaRepository<TradeEntity, Long> {
    boolean existsByAdvertisementEntityAndBuyer(AdvertisementEntity advertisementEntity, UsersEntity buyer);
    List<TradeEntity> findAllByBuyerOrSeller(UsersEntity user, UsersEntity userAgain);
    Optional<TradeEntity> findByBuyerAndSeller(UsersEntity user, UsersEntity userAgain);
    Optional<TradeEntity> findByTradeId(UUID tradeId);
}
