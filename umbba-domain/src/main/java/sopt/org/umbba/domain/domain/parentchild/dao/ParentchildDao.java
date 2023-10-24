package sopt.org.umbba.domain.domain.parentchild.dao;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.jpa.JPAExpressions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import sopt.org.umbba.domain.domain.parentchild.Parentchild;
import sopt.org.umbba.domain.domain.user.QUser;
import sopt.org.umbba.domain.domain.user.User;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

import static sopt.org.umbba.domain.domain.parentchild.QParentchild.parentchild;
import static sopt.org.umbba.domain.domain.user.QUser.user;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ParentchildDao {

    @PersistenceContext
    private EntityManager em;

    private final JPAQueryFactory queryFactory;

    public Optional<Parentchild> findByUserId(Long userId) {

        return Optional.ofNullable(queryFactory
                .selectFrom(parentchild)
                .leftJoin(user.parentChild, parentchild)
                .where(
                        userIdEq(userId)
                )
                .fetchOne());
    }

    public Optional<User> findMatchUserByUserId(Long userId) {

        /*QUser user = QUser.user; // 기존 user 엔티티에 대한 Querydsl 패스

        return Optional.ofNullable(queryFactory
                .selectFrom(user)
                .where(user.id.eq(userId)
                        .and(user.id.ne(user.id))) // 같은 엔티티는 제외하고 다른 유저를 조회
                .fetchOne());*/
        QUser user = QUser.user;
        QUser uc = new QUser("uc");

        return Optional.ofNullable(queryFactory
                .select(user)
                .from(user)
                .where(user.id.ne(userId)
                        .and(user.parentChild.eq(
                                JPAExpressions.select(uc.parentChild)
                                        .from(uc)
                                        .where(uc.id.eq(userId))
                        )))
                .fetchOne());
    }

    public List<String> findFcmTokensById(Long parentchildId) {

        return queryFactory
                .select(user.fcmToken)
                .from(user)
                .leftJoin(user.parentChild, parentchild)
                .where(
                        parentchildIdEq(parentchildId)
                )
                .fetch();
    }


    private BooleanExpression userIdEq(Long userId) {
        return userId != null ? user.id.eq(userId) : null;
    }

    private BooleanExpression parentchildIdEq(Long parentchildId) {
        return parentchildId != null ? parentchild.id.eq(parentchildId) : null;
    }

}
