package example.image.service;

import example.domain.images.Image;
import example.domain.images.repository.ImageQueryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ImageScheduler {

    private final ImageService imageService;
    private final ImageQueryRepository imageQueryRepository;

    @Transactional
    @Scheduled(cron = "0 0 0 * * MON") // 매주 월요일 00시 동작
    public void deleteOrphanImages() {
        LocalDateTime threshold = LocalDateTime.now().minusWeeks(1); // 일주일 이상 지난 이미지

        List<Image> oldUnlinkedImages = imageQueryRepository.findOldUnlinkedImages(threshold);

        if (!oldUnlinkedImages.isEmpty()) {
            imageService.deleteImage(oldUnlinkedImages);
            log.info("삭제된 고아 이미지 수 : {}개", oldUnlinkedImages.size());
        } else {
            log.info("고아 이미지가 없습니다.");
        }
    }
}
