package sopt.org.umbbaServer.domain.qna.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sopt.org.umbbaServer.domain.parentchild.model.Parentchild;
import sopt.org.umbbaServer.domain.parentchild.repository.ParentchildRepository;
import sopt.org.umbbaServer.domain.qna.controller.dto.request.TodayAnswerRequestDto;
import sopt.org.umbbaServer.domain.qna.controller.dto.response.QnAListResponseDto;
import sopt.org.umbbaServer.domain.qna.controller.dto.response.TodayQnAResponseDto;
import sopt.org.umbbaServer.domain.qna.model.QnA;
import sopt.org.umbbaServer.domain.qna.model.Question;
import sopt.org.umbbaServer.domain.qna.repository.QnARepository;
import sopt.org.umbbaServer.domain.qna.repository.QuestionRepository;
import sopt.org.umbbaServer.domain.user.model.User;
import sopt.org.umbbaServer.domain.user.repository.UserRepository;
import sopt.org.umbbaServer.global.exception.CustomException;
import sopt.org.umbbaServer.global.exception.ErrorType;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QnAService {

    private final QnARepository qnARepository;
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final ParentchildRepository parentchildRepository;

    public TodayQnAResponseDto getTodayQnA(Long userId) {
        User myUser = getUserById(userId);
        Parentchild parentchild = getParentchildByUser(myUser);
        QnA todayQnA = getTodayQnAByParentchild(parentchild);
        Question todayQuestion = todayQnA.getQuestion();
        User opponentUser = getOpponentByParentchild(parentchild, userId);

        // 현재 회원이 자식이면 isMeChild가 true, 부모면 false
        boolean isMeChild = myUser.getBornYear() >= opponentUser.getBornYear();

        return TodayQnAResponseDto.of(myUser, opponentUser, todayQnA, todayQuestion, isMeChild);
    }

    @Transactional
    public void answerTodayQuestion(Long userId, TodayAnswerRequestDto request) {
        User myUser = getUserById(userId);
        Parentchild parentchild = getParentchildByUser(myUser);
        QnA todayQnA = getTodayQnAByParentchild(parentchild);
        User opponentUser = getOpponentByParentchild(parentchild, userId);

        boolean isMeChild = myUser.getBornYear() >= opponentUser.getBornYear();

        if (isMeChild) {
            todayQnA.saveChildAnswer(request.getAnswer());
        } else {
            todayQnA.saveParentAnswer(request.getAnswer());
        }
    }

    public List<QnAListResponseDto> getQnaList(Long userId, Long sectionId) {
        User myUser = getUserById(userId);
        Parentchild parentchild = getParentchildByUser(myUser);
        List<QnA> qnaList = getQnAListByParentchild(parentchild);
        User opponentUser = getOpponentByParentchild(parentchild, userId);

        boolean isMeChild = myUser.getBornYear() >= opponentUser.getBornYear();

        return qnaList.stream()
                .filter(qna -> Objects.equals(qna.getQuestion().getSection().getSectionId(), sectionId))
                .map(qna -> {
                    String question = isMeChild ? qna.getQuestion().getChildQuestion() : qna.getQuestion().getParentQuestion();
                    return QnAListResponseDto.builder()
                            .index(qnaList.indexOf(qna) + 1)
                            .question(question)
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public void createQnA() {
        QnA newQnA = QnA.builder()
                .question(questionRepository.findById(1L).get()) // 필터 로직 추가되어야함
                .parentAnswer("부모 답변")
                .childAnswer("자식 답변")
                .isParentAnswer(true)
                .isChildAnswer(false)
                .build();
        qnARepository.save(newQnA);

        Parentchild parentchild = parentchildRepository.findById(1L).get();
        parentchild.addQnA(newQnA);
    }

    // 리팩토링을 위해 아래로 뺀 메서드들

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorType.INVALID_USER));
    }

    private Parentchild getParentchildByUser(User user) {
        Parentchild parentchild = user.getParentChild();
        if (parentchild == null) {
            throw new CustomException(ErrorType.USER_HAVE_NO_PARENTCHILD);
        }

        return parentchild;
    }

    private List<QnA> getQnAListByParentchild(Parentchild parentchild) {
        List<QnA> qnAList = parentchild.getQnaList();
        if (qnAList == null || qnAList.isEmpty()) {
            throw new CustomException(ErrorType.PARENTCHILD_HAVE_NO_QNALIST);
        }

        return qnAList;
    }

    private QnA getTodayQnAByParentchild(Parentchild parentchild) {
        List<QnA> qnAList = parentchild.getQnaList();
        if (qnAList == null || qnAList.isEmpty()) {
            throw new CustomException(ErrorType.PARENTCHILD_HAVE_NO_QNALIST);
        }

        return qnAList.get(qnAList.size() - 1); // 가장 최근의 QnA를 가져옴
    }

    private User getOpponentByParentchild(Parentchild parentchild, Long userId) {
        // Parentchild에 속한 User들 중 자신이 아닌 객체를 가져옴
        List<User> opponentUserList = userRepository.findUserByParentChild(parentchild)
                .stream()
                .filter(user -> !user.getId().equals(userId))
                .collect(Collectors.toList());
        if (opponentUserList.isEmpty()) {
            throw new CustomException(ErrorType.PARENTCHILD_HAVE_NO_OPPONENT);
        }

        return opponentUserList.get(0);
    }
}
