package io.github.adrian.wieczorek.local_trade.unit;

import io.github.adrian.wieczorek.local_trade.model.CategoryEntity;
import io.github.adrian.wieczorek.local_trade.model.ImageEntity;
import io.github.adrian.wieczorek.local_trade.model.UsersEntity;
import io.github.adrian.wieczorek.local_trade.service.facade.NewAdvertisementFacade;
import io.github.adrian.wieczorek.local_trade.mappers.AdvertisementDtoMapper;
import io.github.adrian.wieczorek.local_trade.model.AdvertisementEntity;
import io.github.adrian.wieczorek.local_trade.repository.AdvertisementRepository;
import io.github.adrian.wieczorek.local_trade.repository.UsersRepository;
import io.github.adrian.wieczorek.local_trade.request.RequestAdvertisementDto;
import io.github.adrian.wieczorek.local_trade.response.ResponseAdvertisementDto;
import io.github.adrian.wieczorek.local_trade.response.SimpleAdvertisementResponseDto;
import io.github.adrian.wieczorek.local_trade.service.infrastructure.AdvertisementService;
import io.github.adrian.wieczorek.local_trade.service.infrastructure.NotificationEventPublisher;
import io.github.adrian.wieczorek.local_trade.service.infrastructure.S3Service;
import io.github.adrian.wieczorek.local_trade.testutils.AdUtils;
import io.github.adrian.wieczorek.local_trade.testutils.CategoryUtils;
import io.github.adrian.wieczorek.local_trade.testutils.UserUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NewAdvertisementEntityFacadeUnitTests {
    @InjectMocks
    private NewAdvertisementFacade newAdvertisementFacade;
    @Mock
    private UsersRepository usersRepository;
    @Mock
    private AdvertisementRepository advertisementRepository;
    @Mock
    private S3Service s3Service;
    @Mock
    private AdvertisementService advertisementService;
    @Mock
    AdvertisementDtoMapper advertisementDtoMapper;
    @Mock
    NotificationEventPublisher notificationEventPublisher;

    @Test
    public void testCreateNewAdvertisement() throws Exception {
        RequestAdvertisementDto advertisementDto = AdUtils.createRequestAdvertisementDto();
        UsersEntity user = UserUtils.createUserRoleUser();
        MockMultipartFile mockMultipartFile = new MockMultipartFile("file", "test".getBytes());
        UserDetails userDetails = Mockito.mock(UserDetails.class);
        CategoryEntity categoryEntity = CategoryUtils.createCategory();
        List<MultipartFile> multipartFiles = new ArrayList<>();
        AdvertisementEntity advertisementEntity = AdUtils.createAdvertisementRoleUserForIntegrationTests(categoryEntity,user);
        ImageEntity imageEntity = new ImageEntity();
        List<ImageEntity> imageEntities = new ArrayList<>();
        advertisementEntity.setImageEntities(imageEntities);
        List<String> imageUrls = advertisementEntity.getImageEntities().stream().map(ImageEntity::getUrl).toList();
        List<String> thumbnailUrls = advertisementEntity.getImageEntities().stream().map(ImageEntity::getThumbnailUrl).toList();
        SimpleAdvertisementResponseDto advertisementResponseDto = new SimpleAdvertisementResponseDto(advertisementEntity.getAdvertisementId(), advertisementEntity.getTitle());
        ResponseAdvertisementDto responseAdvertisementDto = new ResponseAdvertisementDto(
                advertisementEntity.getAdvertisementId(),
                advertisementEntity.getCategoryEntity().getId(),
                advertisementEntity.getPrice(),
                advertisementEntity.getTitle(),
                advertisementEntity.getImage(),
                advertisementEntity.getDescription(),
                advertisementEntity.isActive(),
                advertisementEntity.getLocation(),
                imageUrls,
                thumbnailUrls
        );

        for(int i = 0; i<5; i++) {
            multipartFiles.add(mockMultipartFile);
        }
        for (int i=0 ; i<5; i++) {
            imageEntities.add(new ImageEntity());
        }

        when(userDetails.getUsername()).thenReturn(user.getEmail());
        when(usersRepository.findByEmail(userDetails.getUsername())).thenReturn(Optional.of(user));
        when(advertisementService.addAd(eq(advertisementDto), eq(user))).thenReturn(advertisementResponseDto);
        when(s3Service.uploadFile(any(UUID.class), any(MultipartFile.class))).thenReturn(imageEntity);
        when(advertisementDtoMapper.toResponseAdvertisementDto(advertisementEntity)).thenReturn(responseAdvertisementDto);
        when(advertisementRepository.findByAdvertisementId(advertisementEntity.getAdvertisementId())).thenReturn(Optional.of(advertisementEntity));


        ResponseAdvertisementDto result = newAdvertisementFacade.addWholeAdvertisement(advertisementDto,multipartFiles,userDetails);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(advertisementEntity.getAdvertisementId(), result.advertisementId());
        Assertions.assertEquals(advertisementEntity.getCategoryEntity().getId(), result.categoryId());
        Assertions.assertEquals(advertisementEntity.getImage(), result.image());
        verify(usersRepository).findByEmail(user.getEmail());
        verify(advertisementService).addAd(eq(advertisementDto), eq(user));
        verify(s3Service, times(5)).uploadFile(any(UUID.class), any(MultipartFile.class));
        verifyNoMoreInteractions(s3Service, advertisementService, usersRepository);

    }

}
