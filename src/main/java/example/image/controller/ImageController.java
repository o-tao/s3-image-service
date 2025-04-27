package example.image.controller;

import example.domain.images.Image;
import example.image.controller.dto.ImageUploadResponse;
import example.image.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/image")
public class ImageController {

    private final ImageService imageService;

    @PostMapping("/upload")
    public ImageUploadResponse uploadImage(@RequestPart MultipartFile imageFile) {
        Image image = imageService.upload(imageFile);
        return ImageUploadResponse.of(
                image.getId(),
                image.getPath(),
                image.getName(),
                image.getCreatedAt(),
                image.getUpdatedAt()
        );
    }
}
