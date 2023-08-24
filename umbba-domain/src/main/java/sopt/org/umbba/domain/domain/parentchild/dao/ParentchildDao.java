package sopt.org.umbba.domain.domain.parentchild.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import sopt.org.umbba.domain.domain.parentchild.Parentchild;
import sopt.org.umbba.domain.domain.user.User;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
public class ParentchildDao implements AutoCloseable {

    @PersistenceContext
    private EntityManager em;


    @Override
    public void close() throws Exception {

    }

    public Optional<Parentchild> findByUserId(Long userId) {

        String jpql = "SELECT pc FROM Parentchild pc " +
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
        }

    }

    public Optional<User> findMatchUserByUserId(Long userId) {

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

        String jpql = "SELECT u.fcmToken FROM User u " +
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
    }


}
