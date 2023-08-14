package sopt.org.umbba.common.sqs.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import sopt.org.umbba.common.sqs.MessageType;

@Slf4j
@Getter
@SuperBuilder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ScheduleDto extends MessageDto{

    public static ScheduleDto of() {
        return ScheduleDto.builder()
                .type(MessageType.SCHEDULE)
                .build();
    }
}
