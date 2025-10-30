package io.github.czjena.local_trade.unit;

import io.github.czjena.local_trade.facade.NewAdvertisementFacade;
import io.github.czjena.local_trade.mappers.AdvertisementDtoMapper;
import io.github.czjena.local_trade.model.Advertisement;
import io.github.czjena.local_trade.model.Category;
import io.github.czjena.local_trade.model.Image;
import io.github.czjena.local_trade.model.Users;
import io.github.czjena.local_trade.repository.AdvertisementRepository;
import io.github.czjena.local_trade.repository.UsersRepository;
import io.github.czjena.local_trade.request.RequestAdvertisementDto;
import io.github.czjena.local_trade.response.ResponseAdvertisementDto;
import io.github.czjena.local_trade.service.AdvertisementService;
import io.github.czjena.local_trade.service.S3Service;
import io.github.czjena.local_trade.testutils.AdUtils;
import io.github.czjena.local_trade.testutils.CategoryUtils;
import io.github.czjena.local_trade.testutils.UserUtils;
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
public class NewAdvertisementFacadeUnitTests {
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

    @Test
    public void testCreateNewAdvertisement() throws Exception {
        RequestAdvertisementDto advertisementDto = AdUtils.createRequestAdvertisementDto();
        Users user = UserUtils.createUserRoleUser();
        MockMultipartFile mockMultipartFile = new MockMultipartFile("file", "test".getBytes());
        UserDetails userDetails = Mockito.mock(UserDetails.class);
        Category category = CategoryUtils.createCategory();
        List<MultipartFile> multipartFiles = new ArrayList<>();
        Advertisement advertisement = AdUtils.createAdvertisementRoleUserForIntegrationTests(category,user);
        Image image = new Image();
        List<Image> images = new ArrayList<>();
        advertisement.setImages(images);
        List<String> imageUrls = advertisement.getImages().stream().map(Image::getUrl).toList();
        List<String> thumbnailUrls = advertisement.getImages().stream().map(Image::getThumbnailUrl).toList();
        ResponseAdvertisementDto responseAdvertisementDto = new ResponseAdvertisementDto(
                advertisement.getAdvertisementId(),
                advertisement.getCategory().getId(),
                advertisement.getPrice(),
                advertisement.getTitle(),
                advertisement.getImage(),
                advertisement.getDescription(),
                advertisement.isActive(),
                advertisement.getLocation(),
                imageUrls,
                thumbnailUrls
        );

        for(int i = 0; i<5; i++) {
            multipartFiles.add(mockMultipartFile);
        }
        for (int i=0 ; i<5; i++) {
            images.add(new Image());
        }

        when(userDetails.getUsername()).thenReturn(user.getEmail());
        when(usersRepository.findByEmail(userDetails.getUsername())).thenReturn(Optional.of(user));
        when(advertisementService.addAd(eq(advertisementDto), eq(user))).thenReturn(advertisement);
        when(s3Service.uploadFile(any(UUID.class), any(MultipartFile.class))).thenReturn(image);
        when(advertisementDtoMapper.toResponseAdvertisementDto(advertisement)).thenReturn(responseAdvertisementDto);


        ResponseAdvertisementDto result = newAdvertisementFacade.addWholeAdvertisement(advertisementDto,multipartFiles,userDetails);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(advertisement.getAdvertisementId(), result.advertisementId());
        Assertions.assertEquals(advertisement.getCategory().getId(), result.categoryId());
        Assertions.assertEquals(advertisement.getImage(), result.image());
        verify(usersRepository).findByEmail(user.getEmail());
        verify(advertisementService).addAd(eq(advertisementDto), eq(user));
        verify(s3Service, times(5)).uploadFile(any(UUID.class), any(MultipartFile.class));
        verifyNoMoreInteractions(s3Service, advertisementService, usersRepository);

    }

}
