package example.domain.images;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ImageType {
    PRODUCT("product/");

    private final String path;
}
