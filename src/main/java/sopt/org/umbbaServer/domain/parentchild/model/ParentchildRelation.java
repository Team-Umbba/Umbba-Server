package sopt.org.umbbaServer.domain.parentchild.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import sopt.org.umbbaServer.global.exception.CustomException;
import sopt.org.umbbaServer.global.exception.ErrorType;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum ParentchildRelation {

    DAD_SON("아빠와 아들 관계", "남자", "남자"),
    DAD_DAU("아빠와 딸 관계", "남자", "여자"),
    MOM_SON("엄마와 아들 관계", "여자", "남자"),
    MOM_DAU("엄마와 딸 관계", "여자", "여자");

    private final String value;
    private final String parentGender;
    private final String childGender;


    public static ParentchildRelation getRelation(String gender, String relationInfo, boolean isInvitorChild) {

        // 내가 부모다 - 누구와 함께 하겠어? "자식"
        if (!isInvitorChild) {
            if (gender.equals("남자")) {    // 아빠
                if (relationInfo.equals("아들")) {
                    return ParentchildRelation.DAD_SON;
                } else if (relationInfo.equals("딸")) {
                    return ParentchildRelation.DAD_DAU;   // TODO 클라에서 둘 중 하나의 값만 받도록 처리하니까 else if 구문 빼도 무관
                }
            } else if(gender.equals("여자")) {   // 엄마
                if (relationInfo.equals("아들")) {
                    return ParentchildRelation.MOM_SON;
                } else if (relationInfo.equals("딸")) {
                    return ParentchildRelation.DAD_DAU;
                }
            }
        } else {   // 내가 자식이다 - 누구와 함께 하겠어? "부모"
            if (gender.equals("남자")) {   // 아들
                if (relationInfo.equals("아빠")) {
                    return ParentchildRelation.DAD_SON;
                } else if (relationInfo.equals("엄마")) {
                    return ParentchildRelation.MOM_SON;
                }
            } else if(gender.equals("여자")) {   // 딸
                if (relationInfo.equals("아빠")) {
                    return ParentchildRelation.DAD_DAU;
                } else if (relationInfo.equals("엄마")) {
                    return ParentchildRelation.MOM_DAU;
                }
            }
        }

        throw new CustomException(ErrorType.INVALID_PARENT_CHILD_RELATION_INFO);
    }
}
