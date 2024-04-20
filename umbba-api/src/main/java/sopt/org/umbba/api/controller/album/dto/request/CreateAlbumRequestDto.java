package sopt.org.umbba.api.controller.album.dto.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CreateAlbumRequestDto {

	@NotBlank(message = "제목은 필수 입력 값입니다.")
	@Size(max = 15)
	private String title;

	@NotBlank(message = "소개글은 필수 입력 값입니다.")
	@Size(max = 32)
	private String content;

	@NotBlank(message = "이미지 파일명은 필수 입력 값입니다.")
	private String imgFileName;
}
