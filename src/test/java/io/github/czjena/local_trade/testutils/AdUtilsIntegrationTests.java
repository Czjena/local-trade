package io.github.czjena.local_trade.testutils;

import io.github.czjena.local_trade.model.Advertisement;
import io.github.czjena.local_trade.model.Category;
import io.github.czjena.local_trade.model.Users;
import io.github.czjena.local_trade.repository.AdvertisementRepository;
import io.github.czjena.local_trade.repository.CategoryRepository;
import io.github.czjena.local_trade.repository.UsersRepository;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.UUID;

@Component
public class AdUtilsIntegrationTests {
    private final CategoryRepository categoryRepository;
    private final UsersRepository usersRepository;
    private final AdvertisementRepository advertisementRepository;

    public AdUtilsIntegrationTests(CategoryRepository categoryRepository, UsersRepository usersRepository, AdvertisementRepository advertisementRepository) {
        this.categoryRepository = categoryRepository;
        this.usersRepository = usersRepository;
        this.advertisementRepository = advertisementRepository;
    }
    public  Advertisement createAdWithUserAndCategoryAutomaticRoleUser(String title, String description, BigDecimal price) {
        Category category = categoryRepository.save(CategoryUtils.createCategoryForIntegrationTests());
        Users user = usersRepository.save(UserUtils.createUserRoleUser());
        return advertisementRepository.save(Advertisement.builder()
                .title(title)
                .description(description)
                .price(price)
                .active(true)
                .location("location test")
                .user(user)
                .category(category)
                .advertisementId(UUID.randomUUID())
                .favoritedByUsers(new HashSet<>())
                .build());
    }

    public  Advertisement createIntegrationAd(Users user,Category category) {
        return advertisementRepository.save(Advertisement.builder()
                .title("test")
                .description("test")
                .price(BigDecimal.TEN)
                .active(true)
                .location("test")
                .user(user)
                .category(category)
                .advertisementId(UUID.randomUUID())
                .favoritedByUsers(new HashSet<>())
                .build());
    }
}
