package sopt.org.umbbaServer.domain.qna.dao;

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

@Repository
public class QnADao {

    @PersistenceContext
    private EntityManager em;


    // READ

    // 유저 아이디로 QnA 리스트 조회하기
    public List<QnA> findQnASByUserId(Long userId) {

        String jpql = "SELECT pc FROM Parentchild pc JOIN FETCH pc.qnaList " +
                "LEFT JOIN User u ON u.parentChild.id = pc.id " +
                "WHERE u.id = :id";

        TypedQuery query = em.createQuery(jpql, QnA.class)
                .setParameter("id", userId);

        List<QnA> qnAList = query.getResultList();

        return qnAList;
    }

    // 유저 아이디로 최근 QnA 조회하기
}
