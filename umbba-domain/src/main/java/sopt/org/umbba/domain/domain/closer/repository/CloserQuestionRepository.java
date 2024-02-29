package sopt.org.umbba.domain.domain.closer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sopt.org.umbba.domain.domain.closer.CloserQuestion;


public interface CloserQuestionRepository extends JpaRepository<CloserQuestion, Long> {
}