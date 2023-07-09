package sopt.org.umbbaServer.domain.user.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import sopt.org.umbbaServer.domain.parentchild.model.Parentchild;
import sopt.org.umbbaServer.domain.user.model.User;
import sopt.org.umbbaServer.domain.user.social.SocialPlatform;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends Repository<User, Long> {

    // CREATE
    void save(User user);

    // READ
    Optional<User> findById(Long id);
    boolean existsBySocialPlatformAndSocialId(SocialPlatform socialPlatform, String socialId);
    Optional<User> findBySocialPlatformAndSocialId(SocialPlatform socialPlatform, String socialId);

    /*@Query(value = "select user " +
            "from User user " +
            "where user.parentChild.id = :parentchild_id")*/
    List<User> findUserByParentChild(Parentchild parentchild);

    // UPDATE

    // DELETE
}
