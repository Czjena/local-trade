package io.github.czjena.local_trade.repository;

import io.github.czjena.local_trade.model.Advertisement;
import io.github.czjena.local_trade.model.Trade;
import io.github.czjena.local_trade.model.Users;
import io.github.czjena.local_trade.response.ResponseAdvertisementDto;
import io.github.czjena.local_trade.response.TradeResponseDto;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TradeRepository extends JpaRepository<Trade, Long> {
    boolean existsByAdvertisementAndBuyer(Advertisement advertisement, Users buyer);

    List<Trade> findAllByBuyerOrSeller(Users user, Users userAgain);
    Optional<Trade> findByBuyerAndSeller(Users user, Users userAgain);

    Optional<Trade> findByTradeId(UUID tradeId);
}
