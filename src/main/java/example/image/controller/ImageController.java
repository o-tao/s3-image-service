package example.image.controller;

import example.domain.images.Image;
import example.image.controller.dto.ImageResponse;
import example.image.service.ImageScheduler;
import example.image.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/image")
public class ImageController {

    private final ImageService imageService;
    private final ImageScheduler imageScheduler;

    @PostMapping("/upload")
    public ImageResponse uploadImage(@RequestPart MultipartFile imageFile) {
        Image image = imageService.upload(imageFile);
        return ImageResponse.of(
                image.getId(),
                null, // 초기 업로드 시 productId가 null로 저장되기때문에 null 고정 응답
                image.getPath(),
                image.getName(),
                image.getCreatedAt(),
                image.getUpdatedAt()
        );
    }

    @DeleteMapping("/delete")
    public String deleteImage() {
        try {
            imageScheduler.deleteOrphanImages(); // 스케줄러 메서드를 수동 호출
            return "고아 이미지 삭제 작업이 완료되었습니다.";
        } catch (Exception e) {
            return "고아 이미지 삭제 작업에 실패했습니다: " + e.getMessage();
        }
    }
}
