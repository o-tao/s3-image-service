package example.product.controller.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductResponse<T> {

    private Long id;
    private String name;
    private int price;
    private String description;
    private List<T> images;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    public ProductResponse(Long id,
                           String name,
                           int price,
                           String description,
                           List<T> images,
                           LocalDateTime createdAt,
                           LocalDateTime updatedAt
    ) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.description = description;
        this.images = images;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static <T> ProductResponse<T> of(Long id,
                                            String name,
                                            int price,
                                            String description,
                                            List<T> images,
                                            LocalDateTime createdAt,
                                            LocalDateTime updatedAt
    ) {
        return new ProductResponse<>(id, name, price, description, images, createdAt, updatedAt);
    }
}
