package sopt.org.umbbaServer.domain.parentchild.repository;

import org.springframework.data.repository.Repository;
import sopt.org.umbbaServer.domain.parentchild.model.Parentchild;

import java.util.Optional;

public interface ParentchildRepository extends Repository<Parentchild, Long> {

    // CREATE
    void save(Parentchild parentchild);

    // READ
    Optional<Parentchild> findById(Long id);
    Optional<Parentchild> findByInviteCode(String inviteCode);




    // UPDATE

    // DELETE
}
