package sopt.org.umbba.common.sqs;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MessageType {

	public static final String MESSAGE_TYPE_HEADER = "TYPE";
	public static final String FIREBASE = "FIREBASE";
	public static final String SCHEDULE = "SCHEDULE";
	public static final String SLACK = "SLACK";
}
