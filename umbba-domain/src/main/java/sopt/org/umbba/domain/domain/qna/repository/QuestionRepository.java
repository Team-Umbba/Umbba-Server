package sopt.org.umbba.domain.domain.qna.repository;

import org.springframework.data.repository.Repository;
import sopt.org.umbba.domain.domain.qna.Question;
import sopt.org.umbba.domain.domain.qna.QuestionSection;
import sopt.org.umbba.domain.domain.qna.QuestionType;

import java.util.*;
import java.util.stream.Collectors;


public interface QuestionRepository extends Repository<Question, Long> {

    Optional<Question> findById(Long id);

    List<Question> findBySectionAndType(QuestionSection section, QuestionType type);

    List<Question> findByType(QuestionType type);

    default List<Question> findBySectionAndTypeRandom(QuestionSection section, QuestionType type, int size) {
        List<Question> matchingQuestions = findBySectionAndType(section, type);
        List<Question> selectedQuestions = new ArrayList<>();

        int totalMatchingQuestions = matchingQuestions.size();
        int numQuestionsToSelect = Math.min(totalMatchingQuestions, size);
        Random random = new Random();

        for (int i = 0; i < numQuestionsToSelect; i++) {
            int randomIndex = random.nextInt(totalMatchingQuestions);
            Question selectedQuestion = matchingQuestions.get(randomIndex);
            selectedQuestions.add(selectedQuestion);
            matchingQuestions.remove(randomIndex);
            totalMatchingQuestions--;
        }

        return selectedQuestions;
    }

    default List<Question> findByTypeOrderBySectionId(QuestionType type) {
        return findByType(type)
                .stream()
                .sorted(Comparator.comparing(question -> question.getSection().getSectionId()))
                .collect(Collectors.toList());
    }
}