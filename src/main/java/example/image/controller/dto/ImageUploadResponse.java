package example.image.controller.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ImageUploadResponse {

    private String imageName;

    public ImageUploadResponse(String imageName) {
        this.imageName = imageName;
    }

    public static ImageUploadResponse of(String imageName) {
        return new ImageUploadResponse(imageName);
    }
}
