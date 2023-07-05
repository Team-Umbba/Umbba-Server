package sopt.org.umbbaServer.domain.parentchild.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum ParentChildRelation {

    DAD_SON("아빠와 아들 관계"),
    DAD_DAU("아빠와 딸 관계"),
    MOM_SON("엄마와 아들 관계"),
    MOM_DAU("엄마와 딸 관계");

    private final String value;
}
