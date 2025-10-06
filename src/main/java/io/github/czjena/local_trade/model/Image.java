package io.github.czjena.local_trade.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;
@Entity
@Table(name = "images")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "advertisement_id", nullable = false)
    private Advertisement advertisement;
    @Column(length = 1024)
    private String key;
    @Column(length = 1024)
    private String url;
    @Column(length = 1024)
    private String thumbnailKey;
    @Column(length = 1024)
    private String thumbnailUrl;

    private Integer sortOrder;

    private LocalDateTime createdAt = LocalDateTime.now();

    private String contentType;

    private Long size;

    private UUID imageId = UUID.randomUUID();
}
