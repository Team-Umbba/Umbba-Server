package sopt.org.umbba.domain.domain.qna.repository;

import org.springframework.data.repository.Repository;
import sopt.org.umbba.domain.domain.qna.QnA;

import java.util.Optional;

public interface QnARepository extends Repository<QnA, Long> {

    void save(QnA qnA);

    Optional<QnA> findQnAById(Long id);

    void deleteById(Long id);
}
