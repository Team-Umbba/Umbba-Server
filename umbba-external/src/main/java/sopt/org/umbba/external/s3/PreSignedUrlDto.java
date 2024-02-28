package sopt.org.umbba.external.s3;

import lombok.Builder;

@Builder
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
