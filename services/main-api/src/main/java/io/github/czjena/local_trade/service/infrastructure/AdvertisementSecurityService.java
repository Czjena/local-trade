package io.github.czjena.local_trade.service.infrastructure;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.UUID;

public interface AdvertisementSecurityService {
    boolean isOwner(Authentication authentication, Integer advertisement);

    boolean isOwner(UserDetails userDetails, UUID advertisementId);
}
