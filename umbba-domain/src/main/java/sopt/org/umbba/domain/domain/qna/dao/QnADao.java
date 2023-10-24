package sopt.org.umbba.domain.domain.qna.dao;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import sopt.org.umbba.domain.domain.qna.QnA;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

import static sopt.org.umbba.domain.domain.parentchild.QParentchild.parentchild;
import static sopt.org.umbba.domain.domain.user.QUser.user;

@Slf4j
@Repository
@RequiredArgsConstructor
public class QnADao {

    @PersistenceContext
    private EntityManager em;

    private final JPAQueryFactory queryFactory;


    // READ

    // 유저 아이디로 QnA 리스트 조회하기
    public List<QnA> findQnASByUserId(Long userId) {
        return queryFactory
                .select(parentchild.qnaList)
                .from(parentchild)
                .leftJoin(user.parentChild, parentchild)
                .where(userIdEq(userId))
                .fetchOne();
    }

    private BooleanExpression userIdEq(Long userId) {
        return userId != null ? user.id.eq(userId) : null;
    }

}
