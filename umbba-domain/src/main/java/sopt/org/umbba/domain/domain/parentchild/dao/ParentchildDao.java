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

        /*String jpql = "SELECT pc FROM Parentchild pc " +
                "JOIN User u ON u.parentChild = pc " +
                "WHERE u.id = :id";

        try {
            Parentchild parentchild = em.createQuery(jpql, Parentchild.class)
                    .setParameter("id", userId)
                    .getSingleResult();
            return Optional.ofNullable(parentchild);

        } catch (NoResultException e) {
            return Optional.empty();
        } finally {
            em.close();
        }*/

        return Optional.ofNullable(queryFactory
                .selectFrom(parentchild)
                .leftJoin(user.parentChild, parentchild)
                .where(
                        userIdEq(userId)
                )
                .fetchOne());

    }

    public Optional<User> findMatchUserByUserId(Long userId) {

        /*String jpql = "SELECT u FROM User u " +
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
        }*/

        QUser uc = new QUser("uc");

        return Optional.ofNullable(queryFactory
                .select(user)
                .from(user)
                .join(uc).on(uc.parentChild.eq(user.parentChild))
                .where(uc.id.eq(userId).and(uc.id.ne(user.id)))
                .fetchOne());


        /*QUser userSub = new QUser("userSub");

        return Optional.ofNullable(queryFactory
                .selectFrom(user)
                .where(user.parentChild.eq(
                                JPAExpressions
                                        .select(userSub.parentChild)
                                        .from(userSub)
                                        .fetchOne()),
                        userIdEq(userId)
                )
                .fetchOne());*/
    }

    public List<String> findFcmTokensById(Long parentchildId) {

        /*String jpql = "SELECT u.fcmToken FROM User u " +
                "JOIN Parentchild pc ON pc.id = u.parentChild.id " +
                "WHERE pc.id = :id";

        try {
            return em.createQuery(jpql, String.class)
                    .setParameter("id", parentchildId)
                    .getResultList();
        } catch (NoResultException e) {
            return new ArrayList<>();
        } finally {
            em.close();
        }
        */
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
