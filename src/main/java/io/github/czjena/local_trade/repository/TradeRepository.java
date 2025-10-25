package io.github.czjena.local_trade.repository;

import io.github.czjena.local_trade.model.Advertisement;
import io.github.czjena.local_trade.model.Trade;
import io.github.czjena.local_trade.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface TradeRepository extends JpaRepository<Trade, Long> {
    boolean existsByAdvertisementAndBuyer(Advertisement advertisement, Users buyer);
}
