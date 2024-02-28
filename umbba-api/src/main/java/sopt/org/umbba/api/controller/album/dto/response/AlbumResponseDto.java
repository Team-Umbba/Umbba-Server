package sopt.org.umbba.api.controller.album.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.Builder;
import lombok.Getter;
import sopt.org.umbba.domain.domain.album.Album;

@Getter
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class AlbumResponseDto {
	
	private String title;
	private String content;
	private String writer;
	private String imgUrl;

	public static AlbumResponseDto of(Album album) {
		return AlbumResponseDto.builder()
			.title(album.getTitle())
			.content(album.getContent())
			.writer(album.getWriter())
			.imgUrl(album.getImgUrl())
			.build();
	}
}
