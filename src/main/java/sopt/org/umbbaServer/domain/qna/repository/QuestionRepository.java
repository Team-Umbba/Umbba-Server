package sopt.org.umbbaServer.domain.qna.repository;

import org.springframework.data.repository.Repository;
import sopt.org.umbbaServer.domain.qna.model.Question;
import sopt.org.umbbaServer.domain.qna.model.QuestionSection;
import sopt.org.umbbaServer.domain.qna.model.QuestionType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;


public interface QuestionRepository extends Repository<Question, Long> {

    Optional<Question> findById(Long id);

    List<Question> findBySectionAndType(QuestionSection section, QuestionType type);

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
}