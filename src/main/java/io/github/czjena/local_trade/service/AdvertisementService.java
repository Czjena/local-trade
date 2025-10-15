package io.github.czjena.local_trade.service;

import io.github.czjena.local_trade.dto.AdvertisementDto;
import io.github.czjena.local_trade.dto.AdvertisementUpdateDto;
import io.github.czjena.local_trade.mappers.AdvertisementMapper;
import io.github.czjena.local_trade.mappers.AdvertisementMapperToAdvertisementUpdateDto;
import io.github.czjena.local_trade.model.Advertisement;
import io.github.czjena.local_trade.model.Category;
import io.github.czjena.local_trade.model.Users;
import io.github.czjena.local_trade.repository.AdvertisementRepository;
import io.github.czjena.local_trade.repository.CategoryRepository;
import io.github.czjena.local_trade.repository.UsersRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
public class AdvertisementService {

    private final AdvertisementRepository advertisementRepository;
    private final CategoryRepository categoryRepository;
    private final AdvertisementMapper advertisementMapper;
    private final UsersRepository usersRepository;

    public AdvertisementService(AdvertisementRepository advertisementRepository, CategoryRepository categoryRepository,AdvertisementMapper advertisementMapper, UsersRepository usersRepository) {
        this.advertisementRepository = advertisementRepository;
        this.categoryRepository = categoryRepository;
        this.advertisementMapper = advertisementMapper;
        this.usersRepository = usersRepository;
    }

    public Advertisement addAd(AdvertisementDto dto, UserDetails userDetails) {
        Users user = usersRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        Advertisement ad = Advertisement.builder()
                .category(categoryRepository.findById(dto.categoryId()).orElseThrow(() -> new EntityNotFoundException("Category not found")))
                .price(dto.price())
                .title(dto.title())
                .image(dto.image())
                .description(dto.description())
                .active(dto.active())
                .location(dto.location())
                .user(user)
                .build();

        return advertisementRepository.save(ad);
    }

    public Advertisement getAdvertisementById(Integer advertisementId) {
        return advertisementRepository.findById(advertisementId)
                .orElseThrow(() -> new EntityNotFoundException("Advertisement not found"));
    }

    public AdvertisementUpdateDto changeAdvertisement(AdvertisementUpdateDto dto, UserDetails userDetails, Integer advertisementId) {
        Users user = usersRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        Advertisement ad = advertisementRepository.findByUserAndId(user, advertisementId)
                .orElseThrow(() -> new EntityNotFoundException("Advertisement not found"));
        if (!ad.getUser().equals(user)) {
            throw new AccessDeniedException("Access denied");
        }
        advertisementMapper.updateAdvertisementFromDtoSkipNull(dto, ad);
        AdvertisementUpdateDto updatedDto = AdvertisementMapperToAdvertisementUpdateDto.toDto(ad);
        advertisementRepository.save(ad);
        return updatedDto;
    }

    public void deleteAdvertisement(UserDetails userDetails, Integer advertisementId) {
        Users user = usersRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        Advertisement ad = advertisementRepository.findByUserAndId(user, advertisementId)
                .orElseThrow(() -> new EntityNotFoundException("Advertisement not found"));
        advertisementRepository.delete(ad);
    }


}