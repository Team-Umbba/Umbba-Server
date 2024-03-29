package sopt.org.umbba.domain.domain.parentchild;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import sopt.org.umbba.common.exception.ErrorType;
import sopt.org.umbba.common.exception.model.CustomException;
import sopt.org.umbba.domain.domain.user.User;

import java.util.List;

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


    public static ParentchildRelation relation(String gender, String relationInfo, boolean isInvitorChild) {

        // 내가 부모다 - 누구와 함께 하겠어? "자식"
        if (!isInvitorChild) {
            if (gender.equals("남자")) {    // 아빠
                if (relationInfo.equals("아들")) {
                    return ParentchildRelation.DAD_SON;
                } else if (relationInfo.equals("딸")) {
                    return ParentchildRelation.DAD_DAU;
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

    // 아들 | 딸 | 엄마 | 아빠 구분
    public static String getUserType(ParentchildRelation relation, boolean isChild) {
        if (isChild) {
            return relation.childGender.equals("남자") ? "아들" : "딸";
        } else {
            return relation.parentGender.equals("남자") ? "아빠" : "엄마";
        }
    }


    // 자식 유저와 부모 유저의 gender와 isMeChild 필드를 통해 ParentchildRelation을 구분하는 로직
    public static boolean validate(List<User> parentChildUsers, ParentchildRelation relation) {

        User childUser = null;
        User parentUser = null;

        for (User user : parentChildUsers) {
            if (user.isMeChild()) {
                childUser = user;
            } else {
                parentUser = user;
            }
        }

        if (relation.getParentGender().equals(parentUser.getGender())
                && relation.getChildGender().equals(childUser.getGender())) {
            return true;
        }

        return false;
    }

}
