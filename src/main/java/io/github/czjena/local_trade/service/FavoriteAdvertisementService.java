package io.github.czjena.local_trade.service;

import io.github.czjena.local_trade.dto.FavoriteAdvertisementDto;
import io.github.czjena.local_trade.exceptions.UserNotFoundException;
import io.github.czjena.local_trade.mappers.FavoriteAdvertisementMapper;
import io.github.czjena.local_trade.model.Advertisement;
import io.github.czjena.local_trade.model.Users;
import io.github.czjena.local_trade.repository.AdvertisementRepository;
import io.github.czjena.local_trade.repository.UsersRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
public class FavoriteAdvertisementService {
    private final AdvertisementRepository advertisementRepository;
    private final UsersRepository usersRepository;
    private final FavoriteAdvertisementMapper favoriteAdvertisementMapper;

    public FavoriteAdvertisementService(AdvertisementRepository advertisementRepository, UsersRepository usersRepository, FavoriteAdvertisementMapper favoriteAdvertisementMapper) {
        this.advertisementRepository = advertisementRepository;
        this.usersRepository = usersRepository;
        this.favoriteAdvertisementMapper = favoriteAdvertisementMapper;
    }

    public Set<FavoriteAdvertisementDto> getFavoriteAdvertisements(UserDetails userDetails) {
        return usersRepository.findByName(userDetails.getUsername())
                .map(users -> users.getFavoritedAdvertisements()
                        .stream()
                        .map(favoriteAdvertisementMapper::toDto)
                        .collect(Collectors.toSet())).orElseThrow(() -> new UserNotFoundException("No user found with username: " + userDetails.getUsername()));
    }

    public void addFavoriteAdvertisement(UserDetails userDetails, UUID advertisementId) {
        Users user = getUser(userDetails);
        Advertisement ad = advertisementRepository.findByAdvertisementId(advertisementId)
                .orElseThrow(() -> new EntityNotFoundException("Advertisement not found "));
        Set<Users> favoritedByUsers = ad.getFavoritedByUsers();
        favoritedByUsers.add(user);
        advertisementRepository.save(ad);
    }


    public void deleteFavoriteAdvertisement(UserDetails userDetails, UUID advertisementId) {
        Users user = getUser(userDetails);
        Advertisement ad = advertisementRepository.findByAdvertisementId(advertisementId)
                .orElseThrow(() -> new EntityNotFoundException("Advertisement not found "));
        Set<Users> favoritedByAdvertisementId = ad.getFavoritedByUsers();
        favoritedByAdvertisementId.remove(user);
        advertisementRepository.save(ad);
    }

    private Users getUser(UserDetails userDetails) {
        return usersRepository.findByName(userDetails.getUsername())
                .orElseThrow(() -> new UserNotFoundException("No user found with username: " + userDetails.getUsername()));
    }

}