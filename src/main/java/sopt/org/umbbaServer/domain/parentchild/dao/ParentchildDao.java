package sopt.org.umbbaServer.domain.parentchild.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import sopt.org.umbbaServer.domain.parentchild.model.Parentchild;
import sopt.org.umbbaServer.domain.user.model.User;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

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

    public User findMatchUserByUserId(Long userId) {

        String jpql = "SELECT u FROM User u " +
                "JOIN User uc ON uc.parentChild = u.parentChild " +
                "WHERE uc.id = :id AND uc.id != u.id";

        return em.createQuery(jpql, User.class)
                .setParameter("id", userId)
                .getSingleResult();
    }

}
