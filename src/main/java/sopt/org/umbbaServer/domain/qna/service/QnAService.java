package sopt.org.umbbaServer.domain.qna.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sopt.org.umbbaServer.domain.parentchild.model.Parentchild;
import sopt.org.umbbaServer.domain.parentchild.repository.ParentchildRepository;
import sopt.org.umbbaServer.domain.qna.controller.dto.request.TodayAnswerRequestDto;
import sopt.org.umbbaServer.domain.qna.controller.dto.response.GetMainViewResponseDto;
import sopt.org.umbbaServer.domain.qna.controller.dto.response.TodayQnAResponseDto;
import sopt.org.umbbaServer.domain.qna.dao.QnADao;
import sopt.org.umbbaServer.domain.qna.model.QnA;
import sopt.org.umbbaServer.domain.qna.model.Question;
import sopt.org.umbbaServer.domain.qna.repository.QnARepository;
import sopt.org.umbbaServer.domain.qna.repository.QuestionRepository;
import sopt.org.umbbaServer.domain.user.model.User;
import sopt.org.umbbaServer.domain.user.repository.UserRepository;
import sopt.org.umbbaServer.global.exception.CustomException;
import sopt.org.umbbaServer.global.exception.ErrorType;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QnAService {

    private final QnARepository qnARepository;
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final ParentchildRepository parentchildRepository;
    private final QnADao qnADao;

    public TodayQnAResponseDto getTodayQnA(Long userId) {
        User myUser = getUserById(userId);
        Parentchild parentchild = getParentchildByUser(myUser);
        QnA todayQnA = getQnAByParentchild(parentchild);
        Question todayQuestion = todayQnA.getQuestion();
        User opponentUser = getOpponentByParentchild(parentchild, userId);

        // 현재 회원이 자식이면 isMeChild가 true, 부모면 false
        boolean isMeChild = myUser.getBornYear() >= opponentUser.getBornYear();

        return TodayQnAResponseDto.of(myUser, opponentUser, todayQnA, todayQuestion, isMeChild);
    }

    @Transactional
    public void answerTodayQuestion(TodayAnswerRequestDto request, Long userId) {
        User myUser = getUserById(userId);
        Parentchild parentchild = getParentchildByUser(myUser);
        QnA todayQnA = getQnAByParentchild(parentchild);
        User opponentUser = getOpponentByParentchild(parentchild, userId);

        boolean isMeChild = myUser.getBornYear() >= opponentUser.getBornYear();

        if (isMeChild) {
            todayQnA.saveChildAnswer(request.getAnswer());
        } else {
            todayQnA.saveParentAnswer(request.getAnswer());
        }
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

    private QnA getQnAByParentchild(Parentchild parentchild) {
        List<QnA> qnAList = parentchild.getQnaList();
        if (qnAList == null || qnAList.isEmpty()) {
            throw new CustomException(ErrorType.PARENTCHILD_HAVE_NO_QNALIST);
        }

        return qnAList.get(qnAList.size() - 1); // 가장 최근의 QnA를 가져옴
    }

    private User getOpponentByParentchild(Parentchild parentchild, Long userId) {
        // Parentchild에 속한 User들 중 자신이 아닌 객체를 가져옴
        List<User> opponentUserList = parentchildRepository.findUsersByParentChild(parentchild)
                .stream()
                .filter(user -> !user.getId().equals(userId))
                .collect(Collectors.toList());
        if (opponentUserList.isEmpty()) {
            throw new CustomException(ErrorType.PARENTCHILD_HAVE_NO_OPPONENT);
        }

        return opponentUserList.get(0);
    }


    // 메인페이지 정보
    public GetMainViewResponseDto getMainInfo(Long userId) {

        List<QnA> qnAList = qnADao.findQnASByUserId(userId);
        QnA lastQna = qnAList.get(qnAList.size()-1);

        return GetMainViewResponseDto.of

    }
}
