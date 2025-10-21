package io.github.czjena.local_trade.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import jakarta.persistence.Id;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(exclude = "favoritedByUsers")
public class Advertisement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToMany(mappedBy = "advertisement", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Image> images = new ArrayList<>();

    private UUID advertisementId =  UUID.randomUUID();

    @NotBlank
    private String title;

    @NotBlank
    @Size(max = 2000)
    private String description;

    @Size(max = 500)
    private String image;


    private BigDecimal price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @NotBlank
    private String location;

    @CreationTimestamp
    private LocalDateTime createdAt;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "advertisement_favorites", joinColumns = @JoinColumn(name = "advertisement_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<Users> favoritedByUsers = new HashSet<>();

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    private boolean active = true;
    @ManyToOne(fetch = FetchType.LAZY) // wielu ads -> 1 user
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Advertisement that = (Advertisement) o;
        return Objects.equals(id, that.id);
    }
    @Override
    public int hashCode() {
        return id  != null ? id.hashCode() : getClass().hashCode();
    }
}
