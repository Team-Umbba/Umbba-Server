package sopt.org.umbba.external.s3;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class PreSignedUrlDto {

	private String fileName;
	private String url;

	public static PreSignedUrlDto of(String fileName, String url) {
		return PreSignedUrlDto.builder()
			.fileName(fileName)
			.url(url)
			.build();
	}
}
