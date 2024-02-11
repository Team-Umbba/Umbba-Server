package sopt.org.umbba.api.controller.qna.dto.response;

import static sopt.org.umbba.domain.domain.parentchild.ParentchildRelation.*;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.Builder;
import lombok.Getter;
import sopt.org.umbba.domain.domain.parentchild.Parentchild;
import sopt.org.umbba.domain.domain.parentchild.ParentchildRelation;
import sopt.org.umbba.domain.domain.qna.QnA;
import sopt.org.umbba.domain.domain.qna.QuestionSection;
import sopt.org.umbba.domain.domain.user.User;

@Getter
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MyUserInfoResponseDto {

	private String myUsername;
	private String myUserType;
	private String opponentUsername;
	private String opponentUserType;

	private String parentchildRelation;
	private Boolean isMeChild;

	private String section;
	private Long matchedDate;
	private Integer qnaCnt;

	public static MyUserInfoResponseDto of(User myUser, User opponentUser, Parentchild parentchild, QnA qnA, long date, int qnaCnt) {

		return MyUserInfoResponseDto.builder()
			.myUsername(myUser.getUsername())
			.myUserType(getUserType(parentchild.getRelation(), myUser.isMeChild()))
			.opponentUsername(opponentUser.getUsername())
			.opponentUserType(getUserType(parentchild.getRelation(), opponentUser.isMeChild()))
			.parentchildRelation(parentchild.getRelation().getValue())
			.isMeChild(myUser.isMeChild())
			.section(qnA.getQuestion().getSection().getValue())
			.matchedDate(date)  // 일수와 문답 수는 다를 수 있음
			.qnaCnt(qnaCnt).build();
	}

	// 아직 매칭된 유저가 없는 경우
	public static MyUserInfoResponseDto of(User myUser) {

		return MyUserInfoResponseDto.builder()
			.myUsername(myUser.getUsername())
			.section(QuestionSection.YOUNG.getValue())
			.matchedDate(0L)
			.qnaCnt(0).build();
	}
}
