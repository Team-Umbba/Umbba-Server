package sopt.org.umbbaServer.domain.qna.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sopt.org.umbbaServer.domain.parentchild.dao.ParentchildDao;
import sopt.org.umbbaServer.domain.parentchild.model.Parentchild;
import sopt.org.umbbaServer.domain.qna.controller.dto.request.TodayAnswerRequestDto;
import sopt.org.umbbaServer.domain.qna.controller.dto.response.GetMainViewResponseDto;
import sopt.org.umbbaServer.domain.qna.controller.dto.response.QnAListResponseDto;
import sopt.org.umbbaServer.domain.qna.controller.dto.response.SingleQnAResponseDto;
import sopt.org.umbbaServer.domain.qna.controller.dto.response.TodayQnAResponseDto;
import sopt.org.umbbaServer.domain.qna.dao.QnADao;
import sopt.org.umbbaServer.domain.qna.model.*;
import sopt.org.umbbaServer.domain.qna.repository.QnARepository;
import sopt.org.umbbaServer.domain.qna.repository.QuestionRepository;
import sopt.org.umbbaServer.domain.user.model.User;
import sopt.org.umbbaServer.domain.user.repository.UserRepository;
import sopt.org.umbbaServer.domain.user.social.SocialPlatform;
import sopt.org.umbbaServer.global.exception.CustomException;
import sopt.org.umbbaServer.global.exception.ErrorType;
import sopt.org.umbbaServer.global.util.fcm.FCMService;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static sopt.org.umbbaServer.domain.qna.model.OnboardingAnswer.YES;
import static sopt.org.umbbaServer.domain.qna.model.QuestionGroup.*;
import static sopt.org.umbbaServer.domain.qna.model.QuestionSection.YOUNG;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QnAService {

    private final QnARepository qnARepository;
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final QnADao qnADao;
    private final ParentchildDao parentchildDao;
    private final FCMService fcmService;  //TODO Service에서 Service를 주입받는 부분 수정

    public TodayQnAResponseDto getTodayQnA(Long userId) {

        Optional<User> matchUser = parentchildDao.findMatchUserByUserId(userId);
        log.info("matchUser: {} -> parentchildDao.findMatchUserByUserId()의 결과", matchUser);

        // 유저의 상태에 따른 분기처리
        if (matchUser.isEmpty()) {
            return invitation(userId);
        }
        if (matchUser.get().getSocialPlatform().equals(SocialPlatform.WITHDRAW)) {
            return withdrawUser();
        }

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
            fcmService.pushOpponentReply(todayQnA.getQuestion().getParentQuestion(), opponentUser.getId());
        } else {
            todayQnA.saveParentAnswer(request.getAnswer());
            fcmService.pushOpponentReply(todayQnA.getQuestion().getChildQuestion(), opponentUser.getId());
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
                    return QnAListResponseDto.builder()
                            .qnaId(qna.getId())
                            .index(qnaList.indexOf(qna) + 1)
                            .topic(qna.getQuestion().getTopic())
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
    public void filterFirstQuestion(Long userId, List<String> onboardingAnswerStringList) {

        Parentchild parentchild = getParentchildByUserId(userId);

        // String을 Enum으로 변경
        List<OnboardingAnswer> onboardingAnswerList = onboardingAnswerStringList.stream()
                .map(OnboardingAnswer::of)
                .collect(Collectors.toList());

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

        parentchild.initQnA();
        parentchild.addQnA(newQnA);
    }

    @Transactional
    public void filterAllQuestion(Long userId, List<String> onboardingAnswerStringList) {

        Parentchild parentchild = getParentchildByUserId(userId);

        // String을 Enum으로 변경
        List<OnboardingAnswer> onboardingAnswerList = onboardingAnswerStringList.stream()
                .map(OnboardingAnswer::of)
                .collect(Collectors.toList());

        if (getUserById(userId).isMeChild()) {
            parentchild.changeChildOnboardingAnswerList(onboardingAnswerList);
        } else {
            parentchild.changeParentOnboardingAnswerList(onboardingAnswerList);
        }

        List<OnboardingAnswer> childList = parentchild.getChildOnboardingAnswerList();
        List<OnboardingAnswer> parentList = parentchild.getParentOnboardingAnswerList();

        // 질문 그룹을 선택
        QuestionGroup selectedGroup = selectGroup(childList, parentList);
        System.out.println("선택된 그룹: " + selectedGroup);

        for (QuestionSection section : QuestionSection.values()) {
            if (section == YOUNG) continue;

            List<Question> selectedQuestions = questionRepository.findBySectionAndGroupRandom(section, selectedGroup, section.getQuestionCount());

            for (Question question : selectedQuestions) {
                QnA newQnA = QnA.builder()
                        .question(question)
                        .isParentAnswer(false)
                        .isChildAnswer(false)
                        .build();
                qnARepository.save(newQnA);
                parentchild.addQnA(newQnA);
            }
        }
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

    private Parentchild getParentchildByUserId(Long userId) {

        return parentchildDao.findByUserId(userId).orElseThrow(
                () -> new CustomException(ErrorType.USER_HAVE_NO_PARENTCHILD)
        );
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

    private QuestionGroup selectGroup(List<OnboardingAnswer> childList, List<OnboardingAnswer> parentList) {

        // 그룹 선택 알고리즘
        if (childList.get(0) == YES && parentList.get(0) == YES) {
            return GROUP1;
        }
        if (childList.get(1) == YES && parentList.get(1) == YES) {
            return GROUP2;
        }
        if (childList.get(2) == YES && parentList.get(2) == YES) {
            return GROUP3;
        }
        if (childList.get(3) == YES && parentList.get(3) == YES) {
            return GROUP4;
        }
        if (childList.get(4) == YES && parentList.get(4) == YES) {
            return GROUP5;
        }
        if (childList.get(5) == YES && parentList.get(5) == YES) {
            return GROUP6;
        }
        return GROUP7;
    }

    // 메인페이지 정보
    public GetMainViewResponseDto getMainInfo(Long userId) {


        List<QnA> qnAList = qnADao.findQnASByUserId(userId).orElseThrow(
                () -> new CustomException(ErrorType.USER_HAVE_NO_QNALIST)
        );
        QnA lastQna = qnAList.get(qnAList.size()-1);

        return GetMainViewResponseDto.of(lastQna, qnAList.size());
    }

    private TodayQnAResponseDto invitation(Long userId) {

        User user = getUserById(userId);
        Parentchild parentchild = parentchildDao.findByUserId(userId).orElseThrow(
                () -> new CustomException(ErrorType.USER_HAVE_NO_PARENTCHILD)
        );

        return TodayQnAResponseDto.of(parentchild.getInviteCode(), user.getUsername(), "url");  // TODO url 설정 필요 (Firebase)
    }

    private TodayQnAResponseDto withdrawUser() {
        return TodayQnAResponseDto.of(false);
    }
}