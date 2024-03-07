package sopt.org.umbba.domain.domain.closer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sopt.org.umbba.domain.domain.closer.CloserQuestion;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;


public interface CloserQuestionRepository extends JpaRepository<CloserQuestion, Long> {

    default Optional<CloserQuestion> findRandomExceptIds(List<Long> ids) {
        Random random = new Random();
        List<CloserQuestion> allQuestions = findAll();

        if (ids.isEmpty()) {
            int randomIndex = random.nextInt(allQuestions.size());
            return Optional.ofNullable(allQuestions.get(randomIndex));
        }

        List<CloserQuestion> filteredQuestions = allQuestions.stream()
                .filter(question -> !ids.contains(question.getId()))
                .collect(Collectors.toList());

        if (filteredQuestions.isEmpty()) {
            return Optional.empty();
        }

        int randomIndex = random.nextInt(filteredQuestions.size());
        return Optional.of(filteredQuestions.get(randomIndex));
    }
}