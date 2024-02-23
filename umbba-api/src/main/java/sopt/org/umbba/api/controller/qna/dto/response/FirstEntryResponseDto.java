package sopt.org.umbba.api.controller.qna.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.Builder;
import lombok.Getter;
import sopt.org.umbba.domain.domain.user.User;

@Getter
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class FirstEntryResponseDto {

	private Boolean isFirstEntry;

	public static FirstEntryResponseDto of(User user) {
		return FirstEntryResponseDto.builder()
			.isFirstEntry(user.isFirstEntry())
			.build();
	}
}
