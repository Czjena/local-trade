package io.github.czjena.local_trade.service;

import io.github.czjena.local_trade.dto.FavoriteAdvertisementDto;
import io.github.czjena.local_trade.model.Users;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;

public interface FavoriteAdvertisementService {
    @Transactional
    Set<FavoriteAdvertisementDto> getFavoriteAdvertisements(UserDetails userDetails);

    @Transactional
    void addFavoriteAdvertisement(UserDetails userDetails, UUID advertisementId);
    @Transactional
    void deleteFavoriteAdvertisement(UserDetails userDetails, UUID advertisementId);
    @Transactional
    Users getUser(UserDetails userDetails);
}
