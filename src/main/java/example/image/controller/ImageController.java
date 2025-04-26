package example.image.controller;

import example.image.controller.dto.ImageUploadResponse;
import example.image.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/image")
public class ImageController {

    private final ImageService imageService;

    @PostMapping("/upload")
    public ImageUploadResponse uploadImage(@RequestPart MultipartFile image) {
        String imageName = imageService.upload(image);
        return ImageUploadResponse.of(imageName);
    }
}
