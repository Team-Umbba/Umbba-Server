package sopt.org.umbba.domain.qna.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import sopt.org.umbba.domain.qna.model.QnA;

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
            List<QnA> qnaList = query
                    .setParameter("id", userId)
                    .getResultList();
            log.info("query 실행 결과: {}", qnaList.toString());

            return Optional.of(qnaList);
        } catch (NoResultException e) {

            return Optional.empty();
        }
    }

}
