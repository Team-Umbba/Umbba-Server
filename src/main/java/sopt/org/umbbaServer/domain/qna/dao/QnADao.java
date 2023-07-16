package sopt.org.umbbaServer.domain.qna.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import sopt.org.umbbaServer.domain.qna.model.QnA;
import sopt.org.umbbaServer.domain.qna.model.Question;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
public class QnADao {

    @PersistenceContext
    private EntityManager em;


    // READ

    // 유저 아이디로 QnA 리스트 조회하기
    public Optional<List<QnA>> findQnASByUserId(Long userId) {
        log.info("jpql 실행 전");
        String jpql = "SELECT q FROM Parentchild pc " +
                "JOIN pc.qnaList q " +
                "LEFT JOIN User u ON u.parentChild.id = pc.id " +
                "WHERE u.id = :id";

        try {
            TypedQuery<QnA> query = em.createQuery(jpql, QnA.class);

            log.info("query 실행 성공: {}", query);
            List<QnA> qnAList = query
                    .setParameter("id", userId)
                    .getResultList();
            log.info("query 실행 결과: {}", qnAList.toString());

            return Optional.ofNullable(qnAList);
        } catch (NoResultException e) {

            return Optional.empty();
        }
    }

    // 부모자식 관계 아이디로 오늘의 Question 조회하기
    public Optional<QnA> findQuestionByParentchildId(Long parentchildId) {

        String jpql = "SELECT q FROM Parentchild pc " +
                "JOIN pc.qnaList q " +
                "WHERE pc.id = :id " +
                "ORDER BY q.id DESC ";   // TODO 오늘의 질문 인덱스 (카운트) 필드로 조건 달기 변경

        try {
            TypedQuery<QnA> query = em.createQuery(jpql, QnA.class);

            log.info("query 실행 성공: {}", query);
            QnA qnA = query
                    .setParameter("id", parentchildId)
                    .setFirstResult(0)
                    .setMaxResults(1)
                    .getSingleResult();
            log.info("query 실행 결과: {}", qnA.toString());

            return Optional.ofNullable(qnA);
        } catch (NoResultException e) {

            return Optional.empty();
        }
    }

}
