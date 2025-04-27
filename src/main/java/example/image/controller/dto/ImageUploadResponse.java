package example.image.controller.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ImageUploadResponse {

    private Long id;
    private String path;
    private String name;
    private Long productId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    public ImageUploadResponse(Long id,
                               String path,
                               String name,
                               Long productId,
                               LocalDateTime createdAt,
                               LocalDateTime updatedAt
    ) {
        this.id = id;
        this.path = path;
        this.name = name;
        this.productId = productId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static ImageUploadResponse of(Long id,
                                         String path,
                                         String name,
                                         LocalDateTime createdAt,
                                         LocalDateTime updatedAt
    ) {
        return new ImageUploadResponse(id, path, name, null, createdAt, updatedAt);
    }
}
