package example.image.service;

import example.domain.images.Image;
import example.domain.images.ImageRepository;
import example.domain.images.Path;
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
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.InputStream;
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

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    /**
     * [public 메서드]
     * - 외부에서 사용, DB에 저장된 imageName을 반환
     */
    @Transactional
    public String upload(MultipartFile image) {
        // [Step 1] 유효성 검사
        validateImage(image);

        // [Step 2] 유효성 검증 완료 후 S3 업로드
        String imageName = uploadImageToS3(image);

        // [Step 3] S3에 업로드 된 파일 DB 저장, imageName 반환
        return createImage(imageName);
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
        String imageName =  image.getOriginalFilename();
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
    private String uploadImageToS3(MultipartFile image) {
        String extension = Objects.requireNonNull(image.getOriginalFilename())
                .substring(image.getOriginalFilename().lastIndexOf(".") + 1); // 확장자 명
        String imageName = UUID.randomUUID() + "." + extension;
        // [Step 2-1] 이미지 파일 -> InputStream 변환
        try (InputStream inputStream = image.getInputStream()) {
            // PutObjectRequest 객체 생성
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName) // 버킷 이름
                    .key(Path.PRODUCT_IMAGE_PATH.getValue() + imageName) // 저장할 파일 이름
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
    private String createImage(String imageName) {
        // [Step 3-1] DB 저장을 위한 entity 객체생성
        Image createImage = Image.create(Path.PRODUCT_IMAGE_PATH.getValue(), imageName);

        // [Step 3-2] 이미지 저장
        Image image = imageRepository.save(createImage);

        // [Step 3-3] imageName 반환
        return image.getName();
    }
}
