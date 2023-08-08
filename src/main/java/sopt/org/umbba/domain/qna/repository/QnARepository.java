package sopt.org.umbba.domain.qna.repository;

import org.springframework.data.repository.Repository;
import sopt.org.umbba.domain.qna.model.QnA;

import java.util.Optional;

public interface QnARepository extends Repository<QnA, Long> {

    void save(QnA qnA);

    Optional<QnA> findQnAById(Long id);
}
