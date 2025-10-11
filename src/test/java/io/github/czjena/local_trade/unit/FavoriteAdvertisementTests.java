package io.github.czjena.local_trade.unit;

import io.github.czjena.local_trade.model.Advertisement;
import io.github.czjena.local_trade.model.Users;
import io.github.czjena.local_trade.repository.AdvertisementRepository;
import io.github.czjena.local_trade.repository.UsersRepository;
import io.github.czjena.local_trade.service.FavoriteAdvertisementService;
import io.github.czjena.local_trade.testutils.AdUtils;
import io.github.czjena.local_trade.testutils.AdUtilsIntegrationTests;
import io.github.czjena.local_trade.testutils.UserUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

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

    @BeforeEach
        // Ta metoda uruchomi się przed każdym @Test
    void setUp() {
        Users user = UserUtils.createUserRoleUser();
        Advertisement ad = AdUtils.createAdvertisement();
        ad.setFavoritedByUsers(new HashSet<>());

        when(usersRepository.findByUserId(user.getUserId())).thenReturn(user);
        when(advertisementRepository.findByAdvertisementId(ad.getAdvertisementId())).thenReturn(Optional.of(ad));
    }

    @Test
    @Disabled
    public void whenAddingFavoriteAdvertisement_thenSuccess() {
        Users user = UserUtils.createUserRoleUser();
        Advertisement ad = AdUtils.createAdvertisement();
        Set<Users> favoredBy = new HashSet<>();
        favoredBy.add(user);
        ad.setFavoritedByUsers(favoredBy);
        UserDetails mockUserDetails = mock(UserDetails.class);
        when(mockUserDetails.getUsername()).thenReturn(user.getName());

        favoriteAdvertisementService.addFavoriteAdvertisement(mockUserDetails, ad.getAdvertisementId());
        verify(advertisementRepository).save(ad);
        assertTrue(ad.getFavoritedByUsers().contains(user));
        assertEquals(1, ad.getFavoritedByUsers().size());
    }
    @Test
    @Disabled
    public void whenDeletingFavoriteAdvertisement_thenSuccess() {
        Users user = UserUtils.createUserRoleUser();
        Advertisement ad = AdUtils.createAdvertisement();
        Set<Users> favoredBy = new HashSet<>();
        favoredBy.add(user);
        ad.setFavoritedByUsers(favoredBy);

        assertEquals(1, ad.getFavoritedByUsers().size());
        assertTrue(ad.getFavoritedByUsers().contains(user));

        when(usersRepository.findByUserId(user.getUserId())).thenReturn(user);
        when(advertisementRepository.findByAdvertisementId(ad.getAdvertisementId())).thenReturn(Optional.of(ad));
        UserDetails mockUserDetails = mock(UserDetails.class);
        when(mockUserDetails.getUsername()).thenReturn(user.getName());

        favoriteAdvertisementService.deleteFavoriteAdvertisement(mockUserDetails, ad.getAdvertisementId());
        verify(advertisementRepository).save(ad);
        assertFalse(ad.getFavoritedByUsers().contains(user));
        assertEquals(0, ad.getFavoritedByUsers().size());
    }
}
