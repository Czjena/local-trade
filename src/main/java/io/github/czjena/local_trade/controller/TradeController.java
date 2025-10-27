package io.github.czjena.local_trade.controller;

import io.github.czjena.local_trade.model.Trade;
import io.github.czjena.local_trade.request.TradeInitiationRequestDto;
import io.github.czjena.local_trade.response.TradeResponseDto;
import io.github.czjena.local_trade.service.TradeService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/trades")
public class TradeController {
    private TradeService tradeService;

    public TradeController(TradeService tradeService) {
        this.tradeService = tradeService;
    }

    @PostMapping()
    public ResponseEntity<TradeResponseDto> tradeInitiation(@AuthenticationPrincipal UserDetails userDetails, @Valid @RequestBody TradeInitiationRequestDto tradeRequestDto) {
       return ResponseEntity.ok(tradeService.tradeInitiation(userDetails, tradeRequestDto));
    }

}
