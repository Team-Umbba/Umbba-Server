package sopt.org.umbbaServer.domain.qna.repository;

import org.springframework.data.repository.Repository;
import sopt.org.umbbaServer.domain.qna.model.QnA;

public interface QnARepository extends Repository<QnA, Long> {

    void save(QnA qnA);
}
