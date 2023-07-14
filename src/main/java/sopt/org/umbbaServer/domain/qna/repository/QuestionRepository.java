package sopt.org.umbbaServer.domain.qna.repository;

import org.springframework.data.repository.Repository;
import sopt.org.umbbaServer.domain.qna.model.Question;
import sopt.org.umbbaServer.domain.qna.model.QuestionGroup;
import sopt.org.umbbaServer.domain.qna.model.QuestionSection;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;


public interface QuestionRepository extends Repository<Question, Long> {

    Optional<Question> findById(Long id);

    List<Question> findBySectionAndGroup(QuestionSection section, QuestionGroup group);

    default List<Question> findBySectionAndGroupRandom(QuestionSection section, QuestionGroup group, int size) {
        List<Question> matchingQuestions = findBySectionAndGroup(section, group);
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