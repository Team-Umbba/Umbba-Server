package sopt.org.umbba.common.sqs;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MessageType {

	public static final String MESSAGE_TYPE_HEADER = "TYPE";
	public static final String FCM_MULTI= "FCM_MULTI";
	public static final String FCM_SINGLE= "FCM_SINGLE";
	public static final String SLACK = "SLACK";
}
