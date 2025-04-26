package example.domain.images;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Path {
    PRODUCT_IMAGE_PATH("product/");

    private final String value;
}
