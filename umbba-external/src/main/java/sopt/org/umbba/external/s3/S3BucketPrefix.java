package sopt.org.umbba.external.s3;

import static sopt.org.umbba.common.exception.ErrorType.*;

import java.util.Arrays;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import sopt.org.umbba.common.exception.model.CustomException;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum S3BucketPrefix {
	ALBUM_PREFIX("album/");

	private final String value;

	public static S3BucketPrefix of(String value) {
		return Arrays.stream(S3BucketPrefix.values())
			.filter(prefix -> value.equals(prefix.value))
			.findFirst()
			.orElseThrow(() -> new CustomException(INVALID_BUCKET_PREFIX));
	}
}
