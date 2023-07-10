package sopt.org.umbbaServer.domain.parentchild.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import sopt.org.umbbaServer.domain.parentchild.model.Parentchild;
import sopt.org.umbbaServer.domain.user.model.User;

import java.util.List;
import java.util.Optional;

public interface ParentchildRepository extends Repository<Parentchild, Long> {

    // CREATE
    void save(Parentchild parentchild);

    @Query("SELECT u FROM User u WHERE u.parentChild = :parentChild")
    List<User> findUsersByParentChild(@Param("parentChild") Parentchild parentChild);

    // READ
    Optional<Parentchild> findById(Long id);
    Optional<Parentchild> findByInviteCode(String inviteCode);




    // UPDATE

    // DELETE
}
