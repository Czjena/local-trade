package io.github.czjena.local_trade.service;

import io.github.czjena.local_trade.dto.AdvertisementDto;
import io.github.czjena.local_trade.dto.AdvertisementUpdateDto;
import io.github.czjena.local_trade.mappers.AdvertisementMapper;
import io.github.czjena.local_trade.model.Advertisement;
import io.github.czjena.local_trade.model.Category;
import io.github.czjena.local_trade.model.Users;
import io.github.czjena.local_trade.repository.AdvertisementRepository;
import io.github.czjena.local_trade.repository.CategoryRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;


@Service
public class AdvertisementService {

    private final AdvertisementRepository advertisementRepository;
    private final CategoryRepository categoryRepository;
    private final AdvertisementMapper advertisementMapper;

    public AdvertisementService(AdvertisementRepository advertisementRepository, CategoryRepository categoryRepository,AdvertisementMapper advertisementMapper) {
        this.advertisementRepository = advertisementRepository;
        this.categoryRepository = categoryRepository;
        this.advertisementMapper = advertisementMapper;
    }

    public Advertisement addAd(AdvertisementDto dto, Users user) {
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

    public Advertisement changeAdvertisement(AdvertisementUpdateDto dto, Users user, Integer advertisementId) {
        Advertisement ad = advertisementRepository.findByUserAndId(user, advertisementId)
                .orElseThrow(() -> new EntityNotFoundException("Advertisement not found"));
        if (!ad.getUser().equals(user)) {
            throw new AccessDeniedException("Access denied");
        }
        advertisementMapper.updateAdvertisementFromDtoSkipNull(dto, ad);
        return advertisementRepository.save(ad);
    }

    public void deleteAdvertisement(Users user, Integer advertisementId) {
        Advertisement ad = advertisementRepository.findByUserAndId(user, advertisementId)
                .orElseThrow(() -> new EntityNotFoundException("Advertisement not found"));
        advertisementRepository.delete(ad);
    }


}