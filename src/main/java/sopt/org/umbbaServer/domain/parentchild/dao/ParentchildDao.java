package sopt.org.umbbaServer.domain.parentchild.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import sopt.org.umbbaServer.domain.parentchild.model.Parentchild;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

@Slf4j
@Repository
public class ParentchildDao {

    @PersistenceContext
    private EntityManager em;

    public Parentchild findByUserId(Long userId) {

        String jpql = "SELECT pc FROM Parentchild pc " +
                "JOIN User u ON u.parentChild = pc " +
                "WHERE u.id = :id";

        return em.createQuery(jpql, Parentchild.class)
                .setParameter("id", userId)
                .getSingleResult();
    }

}
