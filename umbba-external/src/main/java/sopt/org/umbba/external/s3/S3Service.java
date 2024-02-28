package sopt.org.umbba.external.s3;

import static sopt.org.umbba.common.exception.ErrorType.*;

import java.net.URL;
import java.time.Duration;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;
import sopt.org.umbba.common.exception.model.CustomException;

@Slf4j
@Component
public class S3Service {

	private static final Long PRE_SIGNED_URL_EXPIRE_MINUTE = 1L;  // 만료시간 1분
	private static final String IMAGE_EXTENSION = ".jpg";

	private final String bucketName;

	private final S3Client s3Client;
	private final S3Presigner s3Presigner;

	public S3Service(@Value("${cloud.aws.s3.bucketImg}") final String bucketName, final S3Client s3Client, final S3Presigner s3Presigner) {
		this.bucketName = bucketName;
		this.s3Client = s3Client;
		this.s3Presigner = s3Presigner;
	}

	// 이미지 저장을 위한 PreSigned Url 발급
	public PreSignedUrlDto getPreSignedUrl(final S3BucketPrefix prefix) {
		final String fileName = generateImageFileName();   // UUID 문자열
		final String key = prefix.getValue() + fileName;

		try {
			PutObjectRequest request = PutObjectRequest.builder()
				.bucket(bucketName)
				.key(key).build();

			PutObjectPresignRequest preSignedUrlRequest = PutObjectPresignRequest.builder()
				.signatureDuration(Duration.ofMinutes(PRE_SIGNED_URL_EXPIRE_MINUTE))
				.putObjectRequest(request).build();

			String url = s3Presigner.presignPutObject(preSignedUrlRequest).url().toString();
			return PreSignedUrlDto.of(fileName, url);
		} catch (RuntimeException e) {
			throw new CustomException(FAIL_TO_GET_IMAGE_PRE_SIGNED_URL);
		}
	}

	private String generateImageFileName() {
		return UUID.randomUUID() + IMAGE_EXTENSION;
	}

	// S3 버킷으로부터 이미지 삭제
	public void deleteImage(String key) {
		try {
			s3Client.deleteObject((DeleteObjectRequest.Builder builder) ->
				builder.bucket(bucketName)
					.key(key).build());
		} catch (RuntimeException e) {
			throw new CustomException(FAIL_TO_DELETE_IMAGE);
		}
	}

	// 파일명으로부터 S3 Bucket URL 조회
	public String getS3ImgUrl(String prefix, String fileName) {

		String imageKey = prefix + fileName;

		try {
			GetUrlRequest request = GetUrlRequest.builder()
				.bucket(bucketName)
				.key(imageKey)
				.build();

			URL imageUrl = s3Client.utilities().getUrl(request);

			String urlWithKey = "https://" + bucketName + ".s3.ap-northeast-2.amazonaws.com/" + imageKey;
			if (urlWithKey.equals(imageUrl.toString())) {
				log.info("S3에 저장된 이미지 Url: {}", imageUrl);
				return imageUrl.toString();
			}
			throw new CustomException(S3_BUCKET_GET_IMAGE_ERROR);
		} catch (S3Exception e) {
			throw new CustomException(S3_BUCKET_GET_IMAGE_ERROR);
		}
	}

	private String getUrlByFileName(String prefix, String fileName) {
		return "https://"+bucketName+".s3.ap-northeast-2.amazonaws.com/"+prefix+fileName;
	}
}
