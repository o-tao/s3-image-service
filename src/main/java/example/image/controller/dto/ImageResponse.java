package example.image.controller.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import example.domain.images.Image;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ImageResponse {

    private Long id;
    private Long productId;
    private String path;
    private String name;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    public ImageResponse(Long id,
                         Long productId,
                         String path,
                         String name,
                         LocalDateTime createdAt,
                         LocalDateTime updatedAt
    ) {
        this.id = id;
        this.productId = productId;
        this.path = path;
        this.name = name;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static ImageResponse of(Image image) {
        return new ImageResponse(
                image.getId(),
                image.getProduct() != null ? image.getProduct().getId() : null,
                image.getPath(),
                image.getName(),
                image.getCreatedAt(),
                image.getUpdatedAt()
        );
    }
}
