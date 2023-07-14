package sopt.org.umbbaServer.domain.qna.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sopt.org.umbbaServer.domain.parentchild.dao.ParentchildDao;
import sopt.org.umbbaServer.domain.parentchild.model.OnboardingAnswer;
import sopt.org.umbbaServer.domain.parentchild.model.Parentchild;
import sopt.org.umbbaServer.domain.parentchild.repository.ParentchildRepository;
import sopt.org.umbbaServer.domain.qna.controller.dto.request.TodayAnswerRequestDto;
import sopt.org.umbbaServer.domain.qna.controller.dto.response.QnAListResponseDto;
import sopt.org.umbbaServer.domain.qna.controller.dto.response.SingleQnAResponseDto;
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
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QnAService {

    private final QnARepository qnARepository;
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final QnADao qnADao;
    private final ParentchildDao parentchildDao;

    public TodayQnAResponseDto getTodayQnA(Long userId) {
        User myUser = getUserById(userId);
        Parentchild parentchild = getParentchildByUser(myUser);
        QnA todayQnA = getTodayQnAByParentchild(parentchild);
        Question todayQuestion = todayQnA.getQuestion();
        User opponentUser = getOpponentByParentchild(parentchild, userId);

        return TodayQnAResponseDto.of(myUser, opponentUser, todayQnA, todayQuestion, myUser.isMeChild());
    }

    @Transactional
    public void answerTodayQuestion(Long userId, TodayAnswerRequestDto request) {
        User myUser = getUserById(userId);
        Parentchild parentchild = getParentchildByUser(myUser);
        QnA todayQnA = getTodayQnAByParentchild(parentchild);
        User opponentUser = getOpponentByParentchild(parentchild, userId);

        if (myUser.isMeChild()) {
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

        return qnaList.stream()
                .filter(qna -> Objects.equals(qna.getQuestion().getSection().getSectionId(), sectionId))
                .map(qna -> {
                    String question = myUser.isMeChild() ? qna.getQuestion().getChildQuestion() : qna.getQuestion().getParentQuestion();
                    return QnAListResponseDto.builder()
                            .qnaId(qna.getId())
                            .index(qnaList.indexOf(qna) + 1)
                            .question(question)
                            .build();
                })
                .collect(Collectors.toList());
    }

    public SingleQnAResponseDto getSingleQna(Long userId, Long qnaId) {
        User myUser = getUserById(userId);
        Parentchild parentchild = getParentchildByUser(myUser);
        QnA targetQnA = getQnAById(qnaId); // 이거 qnA로 할건지 qna로 할건지 통일 필요
        Question todayQuestion = targetQnA.getQuestion();
        User opponentUser = getOpponentByParentchild(parentchild, userId);

        return SingleQnAResponseDto.of(myUser, opponentUser, targetQnA, todayQuestion, myUser.isMeChild());
    }

    @Transactional
    public void filterFirstQuestion(Long userId, List<OnboardingAnswer> onboardingAnswerList) {

        Parentchild parentchild = parentchildDao.findByUserId(userId); // TODO 예외처리 필요

        if (getUserById(userId).isMeChild()) {
            parentchild.changeChildOnboardingAnswerList(onboardingAnswerList);
        } else {
            parentchild.changeParentOnboardingAnswerList(onboardingAnswerList);
        }

        // 첫번째 질문은 MVP 단에서는 고정
        QnA newQnA = QnA.builder()
                .question(questionRepository.findById(1L/* 수정 필요 */).get()) // TODO 예외처리 필요
                .isParentAnswer(false)
                .isChildAnswer(false)
                .build();
        qnARepository.save(newQnA);

        parentchild.addQnA(newQnA);
    }

    @Transactional
    public void filterAllQuestion(Long userId, List<OnboardingAnswer> onboardingAnswerList) {

        Parentchild parentchild = parentchildDao.findByUserId(userId); // TODO 예외처리 필요

        if (getUserById(userId).isMeChild()) {
            parentchild.changeChildOnboardingAnswerList(onboardingAnswerList);
        } else {
            parentchild.changeParentOnboardingAnswerList(onboardingAnswerList);
        }

        List<OnboardingAnswer> childList = parentchild.getChildOnboardingAnswerList();
        List<OnboardingAnswer> parentList = parentchild.getParentOnboardingAnswerList();

        if (Objects.equals(childList.get(0).toString(), "YES"))


        // 첫번째 질문은 MVP 단에서는 고정
        QnA newQnA = QnA.builder()
                .question(questionRepository.findById(1L/* 수정 필요 */).get()) // TODO 예외처리 필요
                .isParentAnswer(false)
                .isChildAnswer(false)
                .build();
        qnARepository.save(newQnA);

        parentchild.addQnA(newQnA);
    }

    /*
     리팩토링을 위해 아래로 뺀 메서드들
     */
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

    private QnA getQnAById(Long qnaId) {
        return qnARepository.findQnAById(qnaId)
                .orElseThrow(() -> new CustomException(ErrorType.NOT_FOUND_QNA));
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


    // 메인페이지 정보
    public GetMainViewResponseDto getMainInfo(Long userId) {

        List<QnA> qnAList = qnADao.findQnASByUserId(userId);
        QnA lastQna = qnAList.get(qnAList.size());

        return GetMainViewResponseDto.of(lastQna, qnAList.size());

    }

}
