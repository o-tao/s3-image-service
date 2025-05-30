package example.image.service;

import example.domain.images.Image;
import example.domain.images.ImageType;
import example.domain.images.repository.ImageQueryRepository;
import example.domain.images.repository.ImageRepository;
import example.global.exception.CustomApplicationException;
import example.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectsRequest;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.ObjectIdentifier;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ImageService {

    private final S3Client s3Client;
    private final ImageRepository imageRepository;
    private final ImageQueryRepository imageQueryRepository;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    /**
     * [public 메서드]
     * - 외부에서 사용, DB에 저장된 imageName을 반환
     */
    @Transactional
    public Image upload(MultipartFile image, ImageType imageType) {
        // [Step 1] 유효성 검사
        validateImage(image);

        // [Step 2] 유효성 검증 완료 후 S3 업로드
        String imageName = uploadImageToS3(image, imageType);

        // [Step 3] S3에 업로드 된 파일 DB 저장, imageEntity 반환
        return createImage(imageType, imageName);
    }

    /**
     * [private 메서드]
     * - 파일 유효성 검증
     */
    private void validateImage(MultipartFile image) {
        // [Step 1-1] 파일 존재 유무 검증
        if (image == null || image.isEmpty()) {
            throw new CustomApplicationException(ErrorCode.NOT_EXIST_FILE);
        }

        // [Step 1-2] 확장자 존재 유무 검증
        String imageName = image.getOriginalFilename();
        if (imageName == null || !imageName.contains(".")) {
            throw new CustomApplicationException(ErrorCode.NOT_EXIST_FILE_EXTENSION);
        }

        // [Step 1-3] 허용되지 않는 확장자 검증
        String extension = imageName.substring(imageName.lastIndexOf(".") + 1).toLowerCase();
        List<String> allowedExtentionList = Arrays.asList("jpg", "jpeg", "png", "gif");
        if (!allowedExtentionList.contains(extension)) {
            throw new CustomApplicationException(ErrorCode.INVALID_FILE_EXTENSION);
        }
    }

    /**
     * [private 메서드]
     * - S3 업로드
     */
    private String uploadImageToS3(MultipartFile image, ImageType imageType) {
        String extension = Objects.requireNonNull(image.getOriginalFilename())
                .substring(image.getOriginalFilename().lastIndexOf(".") + 1); // 확장자 명
        String imageName = UUID.randomUUID() + "." + extension;
        // [Step 2-1] 이미지 파일 -> InputStream 변환
        try (InputStream inputStream = image.getInputStream()) {
            // PutObjectRequest 객체 생성
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName) // 버킷 이름
                    .key(imageType.getPath() + imageName) // 저장할 파일 이름
                    .acl(ObjectCannedACL.PUBLIC_READ) // 퍼블릭 읽기 권한
                    .contentType(image.getContentType()) // 이미지 MIME 타입
                    .build();

            // [Step 2-2] S3에 이미지 업로드
            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(inputStream, image.getSize()));

        } catch (Exception exception) {
            log.error(exception.getMessage(), exception);
            throw new CustomApplicationException(ErrorCode.IO_EXCEPTION_UPLOAD_FILE);
        }

        // [Step 2-3] s3에 저장된 imageName 반환
        return imageName;
    }

    /**
     * [private 메서드]
     * DB에 업로드된 이미지 저장
     */
    private Image createImage(ImageType imageType, String imageName) {
        // [Step 3-1] 이미지 저장, imageEntity 반환
        return imageRepository.save(Image.create(
                imageType.getPath(),
                imageName
        ));
    }

    /**
     * [public 메서드]
     * S3, DB 이미지 제거
     */
    @Transactional
    public void deleteImage(List<Image> images) {
        // [Step 1] 각 이미지 객체의 path와 name을 결합해 S3에서 삭제할 키 목록 생성
        List<String> keys = getFullKeys(images);

        try {
            // [Step 2] 생성한 키 목록을 기반으로 S3에서 파일을 삭제하기 위한 요청 객체 생성
            DeleteObjectsRequest deleteObjectsRequest = DeleteObjectsRequest.builder()
                    .bucket(bucketName)
                    .delete(delete -> delete.objects(
                            keys.stream()
                                    .map(key -> ObjectIdentifier.builder().key(key).build())
                                    .toList()
                    ))
                    .build();

            // [Step 3] S3 및 DB 이미지 제거
            s3Client.deleteObjects(deleteObjectsRequest);
            imageRepository.deleteAll(images);

        } catch (Exception exception) {
            log.error(exception.getMessage(), exception);
            throw new CustomApplicationException(ErrorCode.IO_EXCEPTION_DELETE_FILE);
        }
    }

    /**
     * [private 메서드]
     * 이미지 객체의 path와 name을 결합하여 S3에서 삭제할 키 목록 생성
     */
    private List<String> getFullKeys(List<Image> images) {
        return images.stream()
                .map(image -> image.getPath() + image.getName())
                .toList();
    }

    /**
     * [public 메서드]
     * - 이미지 ID를 기준으로 이미지 목록을 조회
     * - 조회된 이미지가 없다면 예외 발생, List응답은 빈값으로 처리되어 Optional처리가 되지 않아 Empty로 직접 체크
     */
    public List<Image> findAllByImageId(List<Long> imageIds) {
        List<Image> images = imageRepository.findAllById(imageIds);
        if (images.isEmpty()) throw new CustomApplicationException(ErrorCode.IMAGE_ID_MISSING);
        return images;
    }

    /**
     * [public 메서드]
     * - productId로 이미지 List 조회
     * - 이미지 없을 시 빈 리스트 응답
     */
    public List<Image> findImageByProductId(Long productId) {
        return imageRepository.findByProductId(productId);
    }

    /**
     * [public 메서드]
     * - image <-> productId 매핑해제
     */
    public void clearProductFromImages(Long productId) {
        imageQueryRepository.clearProductFromImages(productId);
    }

    /**
     * [public 메서드]
     * - image <-> productId 매핑
     */
    public void assignProduct(Long productId, List<Long> imageIds) {
        imageQueryRepository.assignProduct(productId, imageIds);
    }

    /**
     * [public 메서드]
     * - 고아 이미지 조회
     * - productId가 null 인 이미지 중 createdAt이 주어진 기준(threshold)보다 오래된 것들만 조회
     */
    public List<Image> findOldUnlinkedImages(LocalDateTime threshold) {
        return imageQueryRepository.findOldUnlinkedImages(threshold);
    }
}
