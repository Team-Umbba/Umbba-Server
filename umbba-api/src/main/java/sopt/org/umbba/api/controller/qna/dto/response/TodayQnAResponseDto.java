package sopt.org.umbba.api.controller.qna.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Getter;
import sopt.org.umbba.domain.domain.qna.QnA;
import sopt.org.umbba.domain.domain.qna.Question;
import sopt.org.umbba.domain.domain.user.User;

import java.time.Duration;
import java.time.LocalDateTime;

@Getter
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class TodayQnAResponseDto {

    private Long qnaId;
    private Integer index;
    private String section;
    private String topic;
    private String opponentQuestion;
    private String myQuestion;

    private String opponentAnswer;
    private String myAnswer;

    private Boolean isOpponentAnswer;
    private Boolean isMyAnswer;

    private String opponentUsername;
    private String myUsername;

    private Boolean isRerollTime;


    public static TodayQnAResponseDto of(User myUser, User opponentUser, int count, QnA todayQnA, Question todayQuestion) {
        String opponentQuestion;
        String myQuestion;
        String opponentAnswer;
        String myAnswer;
        boolean isOpponentAnswer;
        boolean isMyAnswer;

        boolean isRerollAvailable = true;

        // 하루에 한번만 질문 새로고침 가능
        LocalDateTime lastRerollChange = myUser.getLastRerollChange();
        LocalDateTime now = LocalDateTime.now();
        if (lastRerollChange != null) {
            Duration duration = Duration.between(lastRerollChange, now);
            long hoursPassed = duration.toHours();

            if (hoursPassed < 24) {
                isRerollAvailable = false;
            }
        }

        if (myUser.isMeChild()) {
            opponentQuestion = todayQuestion.getParentQuestion();
            myQuestion = todayQuestion.getChildQuestion();
            opponentAnswer = todayQnA.getParentAnswer();
            myAnswer = todayQnA.getChildAnswer();
            isOpponentAnswer = todayQnA.isParentAnswer();
            isMyAnswer = todayQnA.isChildAnswer();
        } else {
            opponentQuestion = todayQuestion.getChildQuestion();
            myQuestion = todayQuestion.getParentQuestion();
            opponentAnswer = todayQnA.getChildAnswer();
            myAnswer = todayQnA.getParentAnswer();
            isOpponentAnswer = todayQnA.isChildAnswer();
            isMyAnswer = todayQnA.isParentAnswer();
        }

        if (opponentUser != null) {
            return TodayQnAResponseDto.builder()
                    .qnaId(todayQnA.getId())
                    .index(count)
                    .section(todayQuestion.getSection().getValue())
                    .topic(todayQuestion.getTopic())
                    .opponentQuestion(opponentQuestion)
                    .myQuestion(myQuestion)
                    .opponentAnswer(opponentAnswer)
                    .myAnswer(myAnswer)
                    .isOpponentAnswer(isOpponentAnswer)
                    .isMyAnswer(isMyAnswer)
                    .opponentUsername(opponentUser.getUsername())
                    .myUsername(myUser.getUsername())
                    .isRerollTime(isRerollAvailable)
                    .build();
        } else {
            return TodayQnAResponseDto.builder()
                    .qnaId(todayQnA.getId())
                    .index(count)
                    .section(todayQuestion.getSection().getValue())
                    .topic(todayQuestion.getTopic())
                    .opponentQuestion(opponentQuestion)
                    .myQuestion(myQuestion)
                    .opponentAnswer(opponentAnswer)
                    .myAnswer(myAnswer)
                    .isOpponentAnswer(isOpponentAnswer)
                    .isMyAnswer(isMyAnswer)
                    .opponentUsername("상대방")
                    .myUsername(myUser.getUsername())
                    .isRerollTime(isRerollAvailable)
                    .build();
        }
    }
}


