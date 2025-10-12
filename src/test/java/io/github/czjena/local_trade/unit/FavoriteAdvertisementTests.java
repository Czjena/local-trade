package io.github.czjena.local_trade.unit;

import com.fasterxml.jackson.databind.introspect.TypeResolutionContext;
import io.github.czjena.local_trade.exceptions.UserNotFoundException;
import io.github.czjena.local_trade.model.Advertisement;
import io.github.czjena.local_trade.model.Users;
import io.github.czjena.local_trade.repository.AdvertisementRepository;
import io.github.czjena.local_trade.repository.UsersRepository;
import io.github.czjena.local_trade.service.FavoriteAdvertisementService;
import io.github.czjena.local_trade.testutils.AdUtils;
import io.github.czjena.local_trade.testutils.UserUtils;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class FavoriteAdvertisementTests {

    @Mock
    UsersRepository  usersRepository;
    @Mock
    AdvertisementRepository advertisementRepository;
    @InjectMocks
    FavoriteAdvertisementService favoriteAdvertisementService;

    @Test
    public void whenAddingFavoriteAdvertisement_thenSuccess() {
        Users user = UserUtils.createUserRoleUser();
        Advertisement ad = AdUtils.createAdvertisement();
        UserDetails mockUserDetails = mock(UserDetails.class);
        when(mockUserDetails.getUsername()).thenReturn(user.getName());
        when(usersRepository.findByName(user.getName())).thenReturn(Optional.of(user));
        when(advertisementRepository.findByAdvertisementId(ad.getAdvertisementId())).thenReturn(Optional.of(ad));


        favoriteAdvertisementService.addFavoriteAdvertisement(mockUserDetails, ad.getAdvertisementId());
        verify(advertisementRepository).save(ad);
        assertTrue(ad.getFavoritedByUsers().contains(user));
        assertEquals(1, ad.getFavoritedByUsers().size());
    }

    @Test
    public void whenAddingFavoriteAdvertisement_thenNoUserWithThatUserNameFound() {
        Users user = UserUtils.createUserRoleUser();
        user.setName("user");
        Advertisement ad = AdUtils.createAdvertisement();
        UserDetails mockUserDetails = mock(UserDetails.class);
        when(mockUserDetails.getUsername()).thenReturn(user.getName());
        when(usersRepository.findByName(user.getName())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> {
            favoriteAdvertisementService.addFavoriteAdvertisement(mockUserDetails, ad.getAdvertisementId());
        });
        verify(advertisementRepository, never()).save(any(Advertisement.class));
    }

    @Test
    public void whenAddingFavoriteAdvertisement_thenAdvertisementNotFound() {
        Users user = UserUtils.createUserRoleUser();
        Advertisement ad = AdUtils.createAdvertisement();
        UserDetails mockUserDetails = mock(UserDetails.class);
        when(mockUserDetails.getUsername()).thenReturn(user.getName());
        when(usersRepository.findByName(user.getName())).thenReturn(Optional.of(user));
        when(advertisementRepository.findByAdvertisementId(ad.getAdvertisementId())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            favoriteAdvertisementService.addFavoriteAdvertisement(mockUserDetails, ad.getAdvertisementId());
        });
        verify(advertisementRepository, never()).save(any(Advertisement.class));
    }


    @Test
    public void whenDeletingFavoriteAdvertisement_thenSuccess() {
        Users user = UserUtils.createUserRoleUser();
        Advertisement ad = AdUtils.createAdvertisement();

        user.getFavoritedAdvertisements().add(ad);
        ad.getFavoritedByUsers().add(user);

        assertEquals(1, ad.getFavoritedByUsers().size());
        assertTrue(ad.getFavoritedByUsers().contains(user));

        when(usersRepository.findByName(user.getName())).thenReturn(Optional.of(user));
        when(advertisementRepository.findByAdvertisementId(ad.getAdvertisementId())).thenReturn(Optional.of(ad));
        UserDetails mockUserDetails = mock(UserDetails.class);
        when(mockUserDetails.getUsername()).thenReturn(user.getName());

        favoriteAdvertisementService.deleteFavoriteAdvertisement(mockUserDetails, ad.getAdvertisementId());
        verify(advertisementRepository).save(ad);
        Assertions.assertFalse(ad.getFavoritedByUsers().contains(user));
        assertEquals(0, ad.getFavoritedByUsers().size());
    }
    @Test
    public void whenDeletingFavoriteAdvertisement_thenUserNotFound() {
        Users user = UserUtils.createUserRoleUser();
        Advertisement ad = AdUtils.createAdvertisement();

        when(usersRepository.findByName(user.getName())).thenReturn(Optional.empty());
        UserDetails mockUserDetails = mock(UserDetails.class);
        when(mockUserDetails.getUsername()).thenReturn(user.getName());

        assertThrows(UserNotFoundException.class, () -> {favoriteAdvertisementService.deleteFavoriteAdvertisement(mockUserDetails, ad.getAdvertisementId());
        });
        verify(advertisementRepository, never()).save(any(Advertisement.class));
    }
    @Test
    public void whenDeletingFavoriteAdvertisement_thenAdvertisementNotFound() {
        Users user = UserUtils.createUserRoleUser();
        Advertisement ad = AdUtils.createAdvertisement();

        when(usersRepository.findByName(user.getName())).thenReturn(Optional.of(user));
        when(advertisementRepository.findByAdvertisementId(ad.getAdvertisementId())).thenReturn(Optional.empty());
        UserDetails mockUserDetails = mock(UserDetails.class);
        when(mockUserDetails.getUsername()).thenReturn(user.getName());

        assertThrows(EntityNotFoundException.class, () -> {favoriteAdvertisementService.deleteFavoriteAdvertisement(mockUserDetails, ad.getAdvertisementId());
        });
        verify(advertisementRepository, never()).save(any(Advertisement.class));
    }

}
