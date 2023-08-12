package sopt.org.umbba.domain.domain.parentchild.repository;

import org.springframework.data.repository.Repository;
import sopt.org.umbba.domain.domain.parentchild.Parentchild;

import java.util.List;
import java.util.Optional;

public interface ParentchildRepository extends Repository<Parentchild, Long> {

    // CREATE
    void save(Parentchild parentchild);

    // READ
    Optional<Parentchild> findById(Long id);
    Optional<Parentchild> findByInviteCode(String inviteCode);

    List<Parentchild> findAll();


    // UPDATE

    // DELETE
}
