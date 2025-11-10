package io.github.czjena.local_trade.controller;

import io.github.czjena.local_trade.request.TradeInitiationRequestDto;
import io.github.czjena.local_trade.request.TradeStatusRequestDto;
import io.github.czjena.local_trade.response.TradeResponseDto;
import io.github.czjena.local_trade.service.infrastructure.TradeService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/trades")
public class TradeController {
    private final TradeService tradeService;

    public TradeController(TradeService tradeService) {
        this.tradeService = tradeService;
    }
    @PreAuthorize("isAuthenticated()")
    @PostMapping()
    public ResponseEntity<TradeResponseDto> tradeInitiation(@AuthenticationPrincipal UserDetails userDetails, @Valid @RequestBody TradeInitiationRequestDto tradeRequestDto) {
     TradeResponseDto tradeResponseDto = tradeService.tradeInitiation(userDetails, tradeRequestDto);
     URI  location = ServletUriComponentsBuilder
             .fromCurrentRequest()
             .path("{id}")
             .buildAndExpand(tradeResponseDto.id())
             .toUri();
     return ResponseEntity.created(location).body(tradeResponseDto);
    }

    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/{id}")
    public ResponseEntity<TradeResponseDto> updateTradeStatus(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long id, @Valid @RequestBody TradeStatusRequestDto tradeRequestDto) {
        return ResponseEntity.ok(tradeService.updateTradeStatus(userDetails, id, tradeRequestDto.tradeStatus()));
    }
}
