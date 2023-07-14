package sopt.org.umbbaServer.domain.qna.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum QuestionFilterGroup {

    GROUP1(1L, "그룹1"),
    GROUP2(2L, "그룹2"),
    GROUP3(3L, "그룹3"),
    GROUP4(4L, "그룹4"),
    GROUP5(5L, "그룹5"),
    GROUP6(6L, "그룹6"),
    GROUP7(7L, "그룹7");

    private final Long groupId;
    private final String description;
}
