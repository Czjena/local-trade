package io.github.adrian.wieczorek.local_trade.service.advertisement.service;

import io.github.adrian.wieczorek.local_trade.service.advertisement.AdvertisementEntity;
import io.github.adrian.wieczorek.local_trade.service.advertisement.AdvertisementRepository;
import io.github.adrian.wieczorek.local_trade.service.advertisement.dto.AdvertisementUpdateDto;
import io.github.adrian.wieczorek.local_trade.service.user.UsersEntity;
import io.github.adrian.wieczorek.local_trade.service.category.CategoryRepository;
import io.github.adrian.wieczorek.local_trade.service.user.UsersRepository;
import io.github.adrian.wieczorek.local_trade.service.advertisement.dto.RequestAdvertisementDto;
import io.github.adrian.wieczorek.local_trade.service.advertisement.dto.ResponseAdvertisementDto;
import io.github.adrian.wieczorek.local_trade.service.advertisement.dto.SimpleAdvertisementResponseDto;
import io.github.adrian.wieczorek.local_trade.service.advertisement.mapper.AdvertisementDtoMapper;
import io.github.adrian.wieczorek.local_trade.service.advertisement.mapper.AdvertisementMapper;
import io.github.adrian.wieczorek.local_trade.service.advertisement.mapper.AdvertisementMapperToAdvertisementUpdateDto;
import io.github.adrian.wieczorek.local_trade.service.advertisement.mapper.SimpleAdvertisementDtoMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;


@Service
@RequiredArgsConstructor
public class AdvertisementServiceImpl implements AdvertisementService {

    private final AdvertisementRepository advertisementRepository;
    private final CategoryRepository categoryRepository;
    private final AdvertisementMapper advertisementMapper;
    private final UsersRepository usersRepository;
    private final SimpleAdvertisementDtoMapper simpleAdvertisementDtoMapper;

    @Override
    @Transactional
    public SimpleAdvertisementResponseDto addAd(RequestAdvertisementDto dto, UserDetails userDetails) {
        UsersEntity user = usersRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        AdvertisementEntity ad = AdvertisementEntity.builder()
                .categoryEntity(categoryRepository.findById(dto.categoryId()).orElseThrow(() -> new EntityNotFoundException("Category not found")))
                .price(dto.price())
                .title(dto.title())
                .image(dto.image())
                .description(dto.description())
                .active(dto.active())
                .location(dto.location())
                .user(user)
                .build();

         AdvertisementEntity savedAd = advertisementRepository.save(ad);

         return simpleAdvertisementDtoMapper.advertisementToSimpleDto(savedAd);
    }

    @Override
    @Transactional
    public AdvertisementUpdateDto changeAdvertisement(AdvertisementUpdateDto dto, UserDetails userDetails, Integer advertisementId) {
        UsersEntity user = usersRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        AdvertisementEntity ad = advertisementRepository.findByUserAndId(user, advertisementId)
                .orElseThrow(() -> new EntityNotFoundException("Advertisement not found"));
        if (!ad.getUser().equals(user)) {
            throw new AccessDeniedException("Access denied");
        }
        advertisementMapper.updateAdvertisementFromDtoSkipNull(dto, ad);
        AdvertisementUpdateDto updatedDto = AdvertisementMapperToAdvertisementUpdateDto.toDto(ad);
        advertisementRepository.save(ad);
        return updatedDto;
    }

    @Override
    @Transactional
    public void deleteAdvertisement(UserDetails userDetails, Integer advertisementId) {
        UsersEntity user = usersRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        AdvertisementEntity ad = advertisementRepository.findByUserAndId(user, advertisementId)
                .orElseThrow(() -> new EntityNotFoundException("Advertisement not found"));
        advertisementRepository.delete(ad);
    }


}