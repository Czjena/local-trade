package io.github.czjena.local_trade.testutils;

import io.github.czjena.local_trade.enums.TradeStatus;
import io.github.czjena.local_trade.model.Advertisement;
import io.github.czjena.local_trade.model.Trade;
import io.github.czjena.local_trade.model.Users;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class TradeUtils {
    public static Trade createTestTrade(Users seller, Users buyer, Advertisement ad) {
        Trade trade = new Trade();

        trade.setTradeId(UUID.fromString("11111111-2222-3333-4444-555555555555"));

        trade.setSeller(seller);
        trade.setBuyer(buyer);
        trade.setAdvertisement(ad);

        trade.setStatus(TradeStatus.PROPOSED);
        trade.setProposedPrice(new BigDecimal("99.99"));

        trade.setSellerLeftReview(false);
        trade.setBuyerLeftReview(false);
        trade.setSellerMarkedCompleted(false);
        trade.setBuyerMarkedCompleted(false);

        trade.setCreatedAt(LocalDateTime.of(2025, 1, 10, 12, 0));
        trade.setUpdatedAt(LocalDateTime.of(2025, 1, 10, 12, 0));

        return trade;
    }

    }
