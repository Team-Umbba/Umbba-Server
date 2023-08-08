package sopt.org.umbba.common.sqs.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;
import sopt.org.umbba.common.sqs.MessageType;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@SuperBuilder
public class FirebaseDto extends MessageDto {

	private String fcmToken;
	private String title;
	private String body;

	public static FirebaseDto of(String fcmToken, String title, String body) {
		return FirebaseDto.builder()
			.type(MessageType.FIREBASE)
			.fcmToken(fcmToken)
			.title(title)
			.body(body)
			.build();
	}
}
