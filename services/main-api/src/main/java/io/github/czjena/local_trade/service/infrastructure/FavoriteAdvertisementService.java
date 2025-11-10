package io.github.czjena.local_trade.service.infrastructure;

import io.github.czjena.local_trade.dto.FavoriteAdvertisementDto;
import io.github.czjena.local_trade.model.Users;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;

public interface FavoriteAdvertisementService {
    @Transactional(readOnly = true)
    Set<FavoriteAdvertisementDto> getFavoriteAdvertisements(UserDetails userDetails);
    @Transactional
    void addFavoriteAdvertisement(UserDetails userDetails, UUID advertisementId);
    @Transactional
    void deleteFavoriteAdvertisement(UserDetails userDetails, UUID advertisementId);
    @Transactional(readOnly = true)
    Users getUser(UserDetails userDetails);
}
