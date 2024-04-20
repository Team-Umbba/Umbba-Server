package sopt.org.umbba.common.sqs.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
public class MessageDto {

	protected String type;
}
