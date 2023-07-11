package sopt.org.umbbaServer.domain.qna.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import sopt.org.umbbaServer.domain.qna.model.QnA;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
public class QnADao {

    @PersistenceContext
    private EntityManager em;


    // READ

    // 유저 아이디로 QnA 리스트 조회하기
    public List<QnA> findQnASByUserId(Long userId) {
        log.info("jpql 실행 전");
        String jpql = "SELECT q FROM Parentchild pc " +
                "JOIN pc.qnaList q " +
                "LEFT JOIN User u ON u.parentChild.id = pc.id " +
                "WHERE u.id = :id";

        TypedQuery<QnA> query = em.createQuery(jpql, QnA.class);

        log.info("query 실행 성공: {}", query);
        List<QnA> qnAList = query
                .setParameter("id", userId)
                .getResultList();
        log.info("query 실행 결과: {}", qnAList.toString());

        return qnAList;
    }

    // 유저 아이디로 최근 QnA 조회하기
}
