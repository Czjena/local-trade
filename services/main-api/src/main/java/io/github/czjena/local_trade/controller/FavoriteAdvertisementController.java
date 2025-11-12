package io.github.czjena.local_trade.controller;

import io.github.czjena.local_trade.dto.FavoriteAdvertisementDto;
import io.github.czjena.local_trade.service.infrastructure.FavoriteAdvertisementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/favorite")
@RequiredArgsConstructor
public class FavoriteAdvertisementController {

    private final FavoriteAdvertisementService favoriteAdvertisementService;


    @PreAuthorize("isAuthenticated()")
    @GetMapping("/me")
    public ResponseEntity<Set<FavoriteAdvertisementDto>> getMyFavoriteAdvertisements(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(favoriteAdvertisementService.getFavoriteAdvertisements(userDetails));
    }

    @PostMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> addToMyFavoriteAdvertisements(@AuthenticationPrincipal UserDetails userDetails, @PathVariable @NonNull UUID id) {
        favoriteAdvertisementService.addFavoriteAdvertisement(userDetails, id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteFromMyFavoriteAdvertisements(@AuthenticationPrincipal UserDetails userDetails, @PathVariable @NonNull UUID id) {
        favoriteAdvertisementService.deleteFavoriteAdvertisement(userDetails, id);
        return ResponseEntity.noContent().build();

    }
}
