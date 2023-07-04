package sopt.org.umbbaServer.domain.user.repository;

import org.springframework.data.repository.Repository;
import sopt.org.umbbaServer.domain.user.model.User;
import sopt.org.umbbaServer.domain.user.social.SocialPlatform;

import java.util.Optional;

public interface UserRepository extends Repository<User, Long> {

    // CREATE
    void save(User user);

    // READ
    Optional<User> findById(Long id);
    boolean existsBySocialPlatformAndSocialId(SocialPlatform socialPlatform, String socialId);
    Optional<User> findBySocialPlatformAndSocialId(SocialPlatform socialPlatform, String socialId);

    // UPDATE

    // DELETE
}
