package sopt.org.umbba.common.sqs;

public class MessageUtils {

	public static <P1> String generate(String message, P1 param1) {
		return String.format(message, param1);
	}

	public static <P1, P2> String generate(String message, P1 param1, P2 param2) {
		return String.format(message, param1, param2);
	}

	public static <P1, P2, P3> String generate(String message, P1 param1, P2 param2, P3 param3) {
		return String.format(message, param1, param2, param3);
	}

}
