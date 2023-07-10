package sopt.org.umbbaServer.domain.qna.repository;

import org.springframework.data.repository.Repository;
import sopt.org.umbbaServer.domain.qna.model.Question;

import java.util.Optional;

public interface QuestionRepository extends Repository<Question, Long> {

    Optional<Question> findById(Long id);
}
