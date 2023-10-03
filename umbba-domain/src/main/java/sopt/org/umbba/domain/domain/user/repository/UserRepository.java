package sopt.org.umbba.domain.domain.user.repository;

import org.springframework.data.repository.Repository;
import sopt.org.umbba.domain.domain.parentchild.Parentchild;
import sopt.org.umbba.domain.domain.user.SocialPlatform;
import sopt.org.umbba.domain.domain.user.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends Repository<User, Long> {

    // CREATE
    void save(User user);

    // READ
    Optional<User> findById(Long id);
    boolean existsBySocialPlatformAndSocialId(SocialPlatform socialPlatform, String socialId);
    Optional<User> findBySocialPlatformAndSocialId(SocialPlatform socialPlatform, String socialId);
    Optional<User> findByFcmToken(String fcmToken);

    // DELETE
    void deleteById(Long id);

    /*@Query(value = "select user " +
            "from User user " +
            "where user.parentChild.id = :parentchild_id")*/
    List<User> findUserByParentChild(Parentchild parentchild);


    // UPDATE

    // DELETE
}
