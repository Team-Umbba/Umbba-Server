package sopt.org.umbba.common.sqs.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;
import sopt.org.umbba.common.sqs.MessageType;

import javax.servlet.http.HttpServletRequest;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@SuperBuilder
public class SlackDto extends MessageDto {

	private Exception error;
	private HttpServletRequest request;
	private String requestMethod;
	private String requestURI;

	public static SlackDto of(Exception error, String requestMethod, String requestURI) {
		return SlackDto.builder()
			.type(MessageType.SLACK)
			.error(error)
			.requestMethod(requestMethod)
			.requestURI(requestURI)
			.build();
	}
}
