package sopt.org.umbba.domain.domain.parentchild.dao;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.jpa.JPAExpressions;
import javax.persistence.NoResultException;
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

        /*QUser user = QUser.user;
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
                .fetchOne());*/

        String jpql = "SELECT u FROM User u " +
                "JOIN User uc ON uc.parentChild = u.parentChild " +
                "WHERE uc.id = :id AND uc.id != u.id";

        try {
            User user = em.createQuery(jpql, User.class)
                    .setParameter("id", userId)
                    .getSingleResult();
            return Optional.ofNullable(user);
        } catch (NoResultException e) {
            return Optional.empty();
        } finally {
            em.close();
        }
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
