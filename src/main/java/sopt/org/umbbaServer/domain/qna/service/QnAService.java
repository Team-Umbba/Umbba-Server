package sopt.org.umbbaServer.domain.qna.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sopt.org.umbbaServer.domain.parentchild.dao.ParentchildDao;
import sopt.org.umbbaServer.domain.parentchild.model.Parentchild;
import sopt.org.umbbaServer.domain.qna.controller.dto.request.TodayAnswerRequestDto;
import sopt.org.umbbaServer.domain.qna.controller.dto.response.*;
import sopt.org.umbbaServer.domain.qna.model.*;
import sopt.org.umbbaServer.domain.qna.repository.QnARepository;
import sopt.org.umbbaServer.domain.qna.repository.QuestionRepository;
import sopt.org.umbbaServer.domain.user.model.User;
import sopt.org.umbbaServer.domain.user.repository.UserRepository;
import sopt.org.umbbaServer.domain.user.social.SocialPlatform;
import sopt.org.umbbaServer.global.exception.CustomException;
import sopt.org.umbbaServer.global.exception.ErrorType;
import sopt.org.umbbaServer.global.util.fcm.FCMService;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static sopt.org.umbbaServer.domain.qna.model.OnboardingAnswer.*;
import static sopt.org.umbbaServer.domain.qna.model.QuestionSection.*;
import static sopt.org.umbbaServer.domain.qna.model.QuestionType.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QnAService {

    private final QnARepository qnARepository;
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final ParentchildDao parentchildDao;
    private final FCMService fcmService;  //TODO Service에서 Service를 주입받는 부분 수정

    public TodayQnAResponseDto getTodayQnA(Long userId) {

        User myUser = getUserById(userId);
        Parentchild parentchild = getParentchildByUser(myUser);
        QnA todayQnA = getTodayQnAByParentchild(parentchild);
        Question todayQuestion = todayQnA.getQuestion();
        User opponentUser = getOpponentByParentchild(parentchild, userId);

        return TodayQnAResponseDto.of(myUser, opponentUser, parentchild.getCount(), todayQnA, todayQuestion);
    }

    public GetInvitationResponseDto getInvitation(Long userId) {

        Optional<User> matchUser = parentchildDao.findMatchUserByUserId(userId);
        log.info("matchUser: {} -> parentchildDao.findMatchUserByUserId()의 결과", matchUser);

        // 유저의 상태에 따른 분기처리
        if (matchUser.isEmpty()) {
            return invitation(userId);
        }
        else if (matchUser.get().getUsername() == null) {
            return invitation(userId);
        }
        else if (matchUser.get().getSocialPlatform().equals(SocialPlatform.WITHDRAW)) {
            return withdrawUser();
        }

        return GetInvitationResponseDto.of();
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

        return qnaList.stream()
                .limit(parentchild.getCount() - 1)  // index까지만 요소를 처리
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

        User opponentUser = getOpponentByParentchild(parentchild, userId);
        QnA targetQnA = getQnAById(qnaId);
        Question todayQuestion = targetQnA.getQuestion();

        List<QnA> qnaList = getQnAListByParentchild(parentchild);

        return SingleQnAResponseDto.of(myUser, opponentUser, qnaList.indexOf(targetQnA) + 1, targetQnA, todayQuestion);
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
                .question(questionRepository.findByType(FIX).get(0))
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

        // 커스텀되기 전의 메인 질문 리스트를 가져옴
        if (parentchild.getQnaList().size() == 1) {
            List<Question> mainQuestions = questionRepository.findByTypeOrderBySectionId(MAIN);
            for (Question mainQuestion : mainQuestions) {
                QnA newQnA = QnA.builder()
                        .question(mainQuestion)
                        .isParentAnswer(false)
                        .isChildAnswer(false)
                        .build();
                qnARepository.save(newQnA);
                parentchild.addQnA(newQnA);
            }
        }

        // 선택 질문에 따라 질문 리스트가 커스텀됨
        customQuestion(childList, parentList, parentchild.getQnaList());

        log.info("선택된 질문 리스트");
        List<QnA> forLogging= parentchild.getQnaList();
        for (QnA qnA : forLogging) {
            log.info(qnA.getQuestion().getParentQuestion());
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
        List<QnA> qnaList = parentchild.getQnaList();
        if (qnaList == null || qnaList.isEmpty()) {
            throw new CustomException(ErrorType.PARENTCHILD_HAVE_NO_QNALIST);
        }

        return qnaList;
    }

    private QnA getTodayQnAByParentchild(Parentchild parentchild) {
        List<QnA> qnaList = parentchild.getQnaList();
        if (qnaList == null || qnaList.isEmpty()) {
            throw new CustomException(ErrorType.PARENTCHILD_HAVE_NO_QNALIST);
        }

        return qnaList.get(parentchild.getCount() - 1); // 가장 최근의 QnA를 가져옴
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

    @Transactional
    private void customQuestion(List<OnboardingAnswer> childList, List<OnboardingAnswer> parentList, List<QnA> qnAList) {

        // Type 1 : 1번째 선택 질문인 거주 현황에 대해 한명이라도 아니/애매해라고 답한 경우
        if (childList.get(0) != YES || parentList.get(0) !=YES) {
            log.info("Type1의 질문 세트가 적용됨");
            Question selectedQuestion = questionRepository.findBySectionAndTypeRandom(SCHOOL, TYPE1, 1).get(0);
            log.debug("이거 들어감: " + selectedQuestion.getParentQuestion());
            qnAList.get(1).changeQuestion(selectedQuestion);
        }
        // Type 2 : 2번째 선택 질문인 학력 현황에 대해 한명이라도 아니/애매해라고 답한 경우
        else if (childList.get(1) != YES || parentList.get(1) != YES) {
            log.info("Type2의 질문 세트가 적용됨");
            Question selectedQuestion = questionRepository.findBySectionAndTypeRandom(SCHOOL, TYPE2, 1).get(0);
            log.debug("이거 들어감: " + selectedQuestion.getParentQuestion());
            qnAList.get(1).changeQuestion(selectedQuestion);
        }

        // Type 3 : 3번째 선택 질문인 결혼 가치관에 대해 한명이라도 아니라고 답한 경우
        if (childList.get(2) == NO || parentList.get(2) == NO) {
            log.info("Type3의 질문 세트가 적용됨");
            Question selectedQuestion = questionRepository.findBySectionAndTypeRandom(COUPLE, TYPE3, 1).get(0);
            log.debug("이거 들어감: " + selectedQuestion.getParentQuestion());
            qnAList.get(4).changeQuestion(selectedQuestion);
        }

        // Type 5 : 5번째 선택 질문인 후회 여부에 대해 한명이라도 아니라고 답한 경우
        if (childList.get(4) == NO || parentList.get(4) == NO) {
            log.info("Type5의 질문 세트가 적용됨");
            Question selectedQuestion = questionRepository.findBySectionAndTypeRandom(GOLDEN, TYPE5, 1).get(0);
            log.debug("이거 들어감: " + selectedQuestion.getParentQuestion());
            qnAList.get(3).changeQuestion(selectedQuestion);
        }
        // Type 4 : 4번째 선택 질문인 포기 여부에 대해 한명이라도 아니/애매해라고 답한 경우
        else if (childList.get(3) != YES || parentList.get(3) != YES) {
            log.info("Type4의 질문 세트가 적용됨");
            Question selectedQuestion = questionRepository.findBySectionAndTypeRandom(GOLDEN, TYPE4, 1).get(0);
            log.debug("이거 들어감: " + selectedQuestion.getParentQuestion());
            qnAList.get(3).changeQuestion(selectedQuestion);

            selectedQuestion = questionRepository.findBySectionAndTypeRandom(MARRIAGE, TYPE4, 1).get(0);
            log.debug("이거 들어감: " + selectedQuestion.getParentQuestion());
            qnAList.get(5).changeQuestion(selectedQuestion);

            selectedQuestion = questionRepository.findBySectionAndTypeRandom(MARRIAGE2, TYPE4, 1).get(0);
            log.debug("이거 들어감: " + selectedQuestion.getParentQuestion());
            qnAList.get(6).changeQuestion(selectedQuestion);
        }
    }


    /*
    리팩토링을 위해 아래로 뺀 메서드들 끝
     */

    // 메인페이지 정보
    public GetMainViewResponseDto getMainInfo(Long userId) {

        Parentchild parentchild = getParentchildByUserId(userId);
        List<QnA> qnaList = getQnAListByParentchild(parentchild);

        QnA lastQna = qnaList.get(parentchild.getCount()-1);
        log.info("getCount(): {}", parentchild.getCount());

        return GetMainViewResponseDto.of(lastQna, parentchild.getCount());
    }

    private GetInvitationResponseDto invitation(Long userId) {

        User user = getUserById(userId);
        Parentchild parentchild = parentchildDao.findByUserId(userId).orElseThrow(
                () -> new CustomException(ErrorType.USER_HAVE_NO_PARENTCHILD)
        );

        return GetInvitationResponseDto.of(parentchild.getInviteCode(), user.getUsername(), "http://umbba.site/");  // TODO url 설정 필요 (Firebase)
    }

    private GetInvitationResponseDto withdrawUser() {
        return GetInvitationResponseDto.of(false);
    }


    /**
     * 데모데이 테스트용 메서드
     */
    public void resetQnA(Long parentchildId) {

        //

    }

    public void resetOnboard(Long userId) {

    }
}