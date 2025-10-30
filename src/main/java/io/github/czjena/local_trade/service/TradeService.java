package io.github.czjena.local_trade.service;

import io.github.czjena.local_trade.enums.TradeStatus;
import io.github.czjena.local_trade.request.TradeInitiationRequestDto;
import io.github.czjena.local_trade.response.TradeResponseDto;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;

public interface TradeService {
    @Transactional
    TradeResponseDto tradeInitiation(UserDetails userDetails, TradeInitiationRequestDto tradeInitiationRequestDto);

    @Transactional
    TradeResponseDto tradeIsComplete(UserDetails userDetails, Long tradeId);

    @Transactional
    TradeResponseDto tradeIsCancelled(UserDetails userDetails, Long tradeId);

    @Transactional
    TradeResponseDto updateTradeStatus(UserDetails userDetails, Long tradeId, TradeStatus tradeStatus);
}
