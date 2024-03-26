package sopt.org.umbba.api.service.qna;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sopt.org.umbba.api.controller.qna.dto.response.MyUserInfoResponseDto;
import sopt.org.umbba.api.controller.qna.dto.request.TodayAnswerRequestDto;
import sopt.org.umbba.api.controller.qna.dto.response.*;
import sopt.org.umbba.api.service.notification.NotificationService;
import sopt.org.umbba.common.exception.ErrorType;
import sopt.org.umbba.common.exception.model.CustomException;
import sopt.org.umbba.domain.domain.closer.CloserQnA;
import sopt.org.umbba.domain.domain.closer.CloserQuestion;
import sopt.org.umbba.domain.domain.closer.repository.CloserQnARepository;
import sopt.org.umbba.domain.domain.closer.repository.CloserQuestionRepository;
import sopt.org.umbba.domain.domain.parentchild.Parentchild;
import sopt.org.umbba.domain.domain.parentchild.dao.ParentchildDao;
import sopt.org.umbba.domain.domain.qna.*;
import sopt.org.umbba.domain.domain.qna.repository.QnARepository;
import sopt.org.umbba.domain.domain.qna.repository.QuestionRepository;
import sopt.org.umbba.domain.domain.user.SocialPlatform;
import sopt.org.umbba.domain.domain.user.User;
import sopt.org.umbba.domain.domain.user.repository.UserRepository;

import javax.validation.constraints.NotNull;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.*;
import java.util.stream.Collectors;

import static sopt.org.umbba.common.exception.ErrorType.NEED_MORE_QUESTION;
import static sopt.org.umbba.domain.domain.qna.OnboardingAnswer.NO;
import static sopt.org.umbba.domain.domain.qna.OnboardingAnswer.YES;
import static sopt.org.umbba.domain.domain.qna.QuestionSection.*;
import static sopt.org.umbba.domain.domain.qna.QuestionType.*;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QnAService {

    private final QnARepository qnARepository;
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final ParentchildDao parentchildDao;
    private final NotificationService notificationService;

    private final CloserQuestionRepository closerQuestionRepository;
    private final CloserQnARepository closerQnARepository;

    public TodayQnAResponseDto getTodayQnA(Long userId) {

        User myUser = getUserById(userId);
        Parentchild parentchild = getParentchildByUser(myUser);
        QnA todayQnA = getTodayQnAByParentchild(parentchild);
        Question todayQuestion = todayQnA.getQuestion();

        User opponentUser;
        List<User> opponentUserList = userRepository.findUserByParentChild(parentchild)
                .stream()
                .filter(user -> !user.getId().equals(userId))
                .collect(Collectors.toList());
        if (opponentUserList.isEmpty()) {
            return TodayQnAResponseDto.of(myUser, null, parentchild.getCount(), todayQnA, todayQuestion);
        } else {
            opponentUser = opponentUserList.get(0);
            return TodayQnAResponseDto.of(myUser, opponentUser, parentchild.getCount(), todayQnA, todayQuestion);
        }
    }

    public GetInvitationResponseDto getInvitation(Long userId) {

        Optional<User> matchUser = parentchildDao.findMatchUserByUserId(userId);
        log.info("matchUser: {} -> parentchildDao.findMatchUserByUserId()의 결과", matchUser);

        // 유저의 상태에 따른 분기처리
        if (!checkFirstAnswerCompleted(userId)) {
            return firstTutorialQnA();
        }
        else if (matchUser.isEmpty()) {
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
    private boolean checkFirstAnswerCompleted(Long userId) {
        User user = getUserById(userId);
        QnA firstQnA = user.getParentChild().getQnaList().get(0);

        if (user.isMeChild() && firstQnA.isChildAnswer()) {
            return true;
        } else if (!user.isMeChild() && firstQnA.isParentAnswer()) {
            return true;
        }
        return false;
    }

    @Transactional
    public void answerTodayQuestion(Long userId, TodayAnswerRequestDto request) {
        User myUser = getUserById(userId);
        Parentchild parentchild = getParentchildByUser(myUser);
        QnA todayQnA = getTodayQnAByParentchild(parentchild);

        List<User> opponentUserList = userRepository.findUserByParentChild(parentchild)
                .stream()
                .filter(user -> !user.getId().equals(userId))
                .collect(Collectors.toList());

        if (opponentUserList.isEmpty()) {
            if (myUser.isMeChild()) {
                todayQnA.saveChildAnswer(request.getAnswer());
            } else {
                todayQnA.saveParentAnswer(request.getAnswer());
            }
        } else {
            User opponentUser = opponentUserList.get(0);

            if (myUser.isMeChild()) {
                todayQnA.saveChildAnswer(request.getAnswer());
                notificationService.pushOpponentReply(todayQnA.getQuestion().getChildQuestion(), opponentUser.getId());
//            fcmService.pushOpponentReply(todayQnA.getQuestion().getChildQuestion(), opponentUser.getId());
            } else {
                todayQnA.saveParentAnswer(request.getAnswer());
                notificationService.pushOpponentReply(todayQnA.getQuestion().getParentQuestion(), opponentUser.getId());
//            fcmService.pushOpponentReply(todayQnA.getQuestion().getParentQuestion(), opponentUser.getId());
            }
        }
    }

    // 콕찌르기와 같이 특정 이벤트로 리마인드 알림을 발신할 경우
    @Transactional
    public void remindQuestion(Long userId) {
        User myUser = getUserById(userId);
        Parentchild parentchild = getParentchildByUser(myUser);
        User opponentUser = getOpponentByParentchild(parentchild, userId);
        QnA todayQnA = getTodayQnAByParentchild(parentchild);


        notificationService.pushOpponentRemind(opponentUser.getId(), todayQnA.getQuestion().getTopic());
    }

    public List<QnAListResponseDto> getQnaList(Long userId, Long sectionId) {
        User myUser = getUserById(userId);
        if (sectionId < 1L || sectionId > 5L) {
            throw new CustomException(ErrorType.NOT_FOUND_SECTION);
        }

        Parentchild parentchild = getParentchildByUser(myUser);
        List<QnA> qnaList = getQnAListByParentchild(parentchild);

        QnA todayQnA = getTodayQnAByParentchild(parentchild);
        int doneIndex = parentchild.getCount() - 1;
        if (todayQnA.isChildAnswer() && todayQnA.isParentAnswer()) {
            doneIndex += 1;
        }

        return qnaList.stream()
                .limit(doneIndex)  // 현재 답변 완료된 index까지 보이도록
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
    public void filterFirstQuestion(Long userId) {

        Parentchild parentchild = getUserById(userId).getParentChild();
        if (parentchild == null) {
            throw new CustomException(ErrorType.USER_HAVE_NO_PARENTCHILD);
        }

        // 첫번째 질문은 MVP 단에서는 고정
        QnA newQnA = QnA.builder()
                .question(questionRepository.findByType(FIX).get(0))
                .isParentAnswer(false)
                .isChildAnswer(false)
                .build();
        qnARepository.save(newQnA);

        parentchild.setQna(newQnA);
    }

    @Transactional
    public void filterAllQuestion(Long userId) {

        Parentchild parentchild = getUserById(userId).getParentChild();
        if (parentchild == null) {
            throw new CustomException(ErrorType.USER_HAVE_NO_PARENTCHILD);
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
                parentchild.setQna(newQnA);
            }
        }

        if (childList.size() >= 5 && parentList.size() >= 5) {
            // 선택 질문에 따라 질문 리스트가 커스텀됨
            customQuestion(childList, parentList, parentchild.getQnaList());
        }

        log.info("선택된 질문 리스트");
        List<QnA> forLogging= parentchild.getQnaList();
        for (QnA qnA : forLogging) {
            log.info(qnA.getQuestion().getParentQuestion());
        }

        // 가까워지기 QnA도 추가
        if (parentchild.getCloserQnaList().isEmpty()) {
            CloserQuestion firstCloserQuestion = closerQuestionRepository.findRandomExceptIds(new ArrayList<>())
                    .orElseThrow(() -> new CustomException(ErrorType.NO_MORE_CLOSER_QUESTION));

            CloserQnA newCloserQnA = CloserQnA.builder()
                    .closerQuestion(firstCloserQuestion)
                    .isParentAnswer(false)
                    .isChildAnswer(false)
                    .build();
            closerQnARepository.save(newCloserQnA);
            parentchild.addCloserQna(newCloserQnA);
        }
    }

    // 마이페이지 - 부모자식 관계 정보 조회
    public MyUserInfoResponseDto getUserInfo(final Long userId) {

        User myUser = getUserById(userId);
        Parentchild parentchild = getParentchildByUser(myUser);
        List<User> opponentUserList = userRepository.findUserByParentChild(parentchild)
            .stream()
            .filter(user -> !user.getId().equals(userId))
            .collect(Collectors.toList());

        // 매칭된 상대 유저가 없는 경우
        if (opponentUserList.isEmpty()) {
            return MyUserInfoResponseDto.of(myUser, parentchild);
        }

        User opponentUser = getOpponentByParentchild(parentchild, userId);
        QnA todayQnA = getTodayQnAByParentchild(parentchild);

        int qnaCnt = parentchild.getCount();
        if (!todayQnA.isChildAnswer() || !todayQnA.isParentAnswer()) {
            qnaCnt -= 1;
        }

        LocalDateTime firstQnADate = parentchild.getQnaList().get(0).getCreatedAt();
        long qnaDate = ChronoUnit.DAYS.between(firstQnADate, LocalDateTime.now());

        return MyUserInfoResponseDto.of(myUser, opponentUser, parentchild, todayQnA, qnaDate, qnaCnt);
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
    public void customQuestion(List<OnboardingAnswer> childList, List<OnboardingAnswer> parentList, List<QnA> qnAList) {

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


    // 메인페이지 정보
    public GetMainViewResponseDto getMainInfo(Long userId) {

        // updateUserFirstEntry(userId);
        User user = getUserById(userId);
        Parentchild parentchild = user.getParentChild();
        List<QnA> qnaList = getQnAListByParentchild(parentchild);

        QnA currentQnA = qnaList.get(parentchild.getCount()-1);
        log.info("getCount(): {}", parentchild.getCount());

        if (parentchild.getCount() == 7 && (currentQnA.isParentAnswer() && currentQnA.isChildAnswer()) && !user.isEndingDone()) {
            return GetMainViewResponseDto.of(currentQnA, -1);  // 유효하지 않은 -1로 반환 시 엔딩이벤트
        } else if (parentchild.getCount() == 8) {
            QnA lastQnA = qnaList.get(6);
            if ((lastQnA.isParentAnswer() && lastQnA.isChildAnswer()) && !user.isEndingDone()) {
                return GetMainViewResponseDto.of(currentQnA, -1);
            }
        }

        return GetMainViewResponseDto.of(currentQnA, parentchild.getCount());
    }

    @Transactional
    public FirstEntryResponseDto updateUserFirstEntry(Long userId) {
        User user = getUserById(userId);
        if (!user.isFirstEntry()) {
            return FirstEntryResponseDto.of(false);
        }
        user.updateIsFirstEntry();
        return FirstEntryResponseDto.of(true);
    }

    @Transactional
    public void restartQna(Long userId) {
        User user = getUserById(userId);
        user.updateIsEndingDone();
        Parentchild parentchild = user.getParentChild();

        if (parentchild.getCount() == 8) {
            // 상대측이 이미 답변 이어가기를 호출했다면 실행할 필요 X
            return;
        }

        List<QnA> qnaList = getQnAListByParentchild(parentchild);

        // 1. 메인 타입과 미사용 타입에 대해서 불러오기
        List<QuestionType> types = Arrays.asList(MAIN, YET);

        // 2. 내가 이미 주고받은 질문 제외하기
        List<Long> doneQuestionIds = qnaList.stream()
                .map(qna -> qna.getQuestion().getId())
                .collect(Collectors.toList());

        // 5. 이 경우 아예 추가될 질문이 없으므로 예외 발생시킴
        List<Question> targetQuestions = questionRepository.findByTypeInAndIdNotIn(types, doneQuestionIds);
        if (targetQuestions.isEmpty()) {
            throw new CustomException(NEED_MORE_QUESTION);
        }

        QuestionSection section = qnaList.get(parentchild.getCount() - 1).getQuestion().getSection();
        List<Question> differentSectionQuestions = targetQuestions.stream()
            .filter(question -> !question.getSection().equals(section))
            .collect(Collectors.toList());

        Random random = new Random();
        Question randomQuestion;
        if (!differentSectionQuestions.isEmpty()) {
            // 3. 최근에 주고받은 질문의 section과 다른 질문들 중에서 랜덤하게 추출
            randomQuestion = differentSectionQuestions.get(random.nextInt(differentSectionQuestions.size()));
        } else {
            // 4. 없다면 동일한 section의 질문 중에서라도 랜덤하게 추출
            List<Question> equalSectionQuestions = targetQuestions.stream()
                .filter(question -> question.getSection().equals(section))
                .collect(Collectors.toList());
            randomQuestion = equalSectionQuestions.get(random.nextInt(equalSectionQuestions.size()));
        }

        // 새로운 질문 추가!
        QnA newQnA = QnA.builder()
                .question(randomQuestion)
                .isParentAnswer(false)
                .isChildAnswer(false)
                .build();
        qnARepository.save(newQnA);
        parentchild.addQna(newQnA);
        parentchild.addCount();
    }

    public RerollCheckResponseDto rerollCheck(Long userId) {
        User user = getUserById(userId);
        Parentchild parentchild = user.getParentChild();

        // 7일 이후가 아닌 경우 질문 새로고침 불가능
        if (parentchild.getCount() <= 7) {
            throw new CustomException(ErrorType.INVALID_REROLL_BEFORE_SEVEN);
        }
        // 답변이 진행됐을 경우 질문 새로고침 불가능
        List<QnA> qnaList = parentchild.getQnaList();
        QnA currentQnA = qnaList.get(parentchild.getCount() - 1);
        if (currentQnA.isParentAnswer() || currentQnA.isChildAnswer()) {
            throw new CustomException(ErrorType.INVALID_REROLL_AFTER_ANSWER);
        }

        // 1. 메인 타입과 미사용 타입에 대해서 불러오기
        List<QuestionType> types = Arrays.asList(MAIN, YET);

        // 2. 내가 이미 주고받은 질문 제외하기
        List<Long> doneQuestionIds = qnaList.stream()
            .map(qna -> qna.getQuestion().getId())
            .collect(Collectors.toList());

        // 5. 이 경우 아예 추가될 질문이 없으므로 예외 발생시킴
        List<Question> targetQuestions = questionRepository.findByTypeInAndIdNotIn(types, doneQuestionIds);
        if (targetQuestions.isEmpty()) {
            throw new CustomException(NEED_MORE_QUESTION);
        }

        QuestionSection section = qnaList.get(parentchild.getCount() - 1).getQuestion().getSection();
        List<Question> equalSectionQuestions = targetQuestions.stream()
            .filter(question -> question.getSection().equals(section))
            .collect(Collectors.toList());

        Random random = new Random();
        Question randomQuestion;
        if (!equalSectionQuestions.isEmpty()) {
            // 3. 최근에 주고받은 질문의 section과 같은 질문들 중에서 랜덤하게 추출
            randomQuestion = equalSectionQuestions.get(random.nextInt(equalSectionQuestions.size()));
        } else {
            // 4. 없다면 다른 section의 질문 중에서라도 랜덤하게 추출
            List<Question> differentSectionQuestions = targetQuestions.stream()
                .filter(question -> !question.getSection().equals(section))
                .collect(Collectors.toList());
            randomQuestion = differentSectionQuestions.get(random.nextInt(differentSectionQuestions.size()));
        }

        return RerollCheckResponseDto.of(randomQuestion);
    }

    @Transactional
    public void rerollChange(Long userId, Long questionId) {
        User user = getUserById(userId);
        Question question = questionRepository.findById(questionId)
            .orElseThrow(() -> new CustomException(ErrorType.NOT_FOUND_QUESTION));

        LocalDateTime lastRerollChange = user.getLastRerollChange();
        LocalDateTime now = LocalDateTime.now();

        if (lastRerollChange != null) {
            Duration duration = Duration.between(lastRerollChange, now);
            long hoursPassed = duration.toHours();

            if (hoursPassed < 24) {
                throw new CustomException(ErrorType.INVALID_REROLL_ONCE_A_DAY);
            }
        }

        Parentchild parentchild = user.getParentChild();
        List<QnA> qnaList = parentchild.getQnaList();
        QnA currentQnA = qnaList.get(parentchild.getCount() - 1);

        currentQnA.changeQuestion(question);
    }

    @NotNull
    private Parentchild getParentchild(Long userId) {
        Parentchild parentchild = getUserById(userId).getParentChild();
        if (parentchild == null) {
            throw new CustomException(ErrorType.USER_HAVE_NO_PARENTCHILD);
        }
        return parentchild;
    }

    private GetInvitationResponseDto invitation(Long userId) {

        User user = getUserById(userId);
        Parentchild parentchild = parentchildDao.findByUserId(userId).orElseThrow(
                () -> new CustomException(ErrorType.USER_HAVE_NO_PARENTCHILD)
        );

        return GetInvitationResponseDto.of(parentchild.getInviteCode(), user.getUsername(), "http://umbba.site/");  // TODO Firebase 동적링크 연결 예정
    }

    private GetInvitationResponseDto withdrawUser() {
        return GetInvitationResponseDto.of(false);
    }

    private GetInvitationResponseDto firstTutorialQnA() {
        return GetInvitationResponseDto.ofFirst(false);
    }


    /**
     * 데모데이 테스트용 메서드
     */
    @Transactional
    public void updateDemoList(Long userId) {

        User myUser = getUserById(userId);
        Parentchild parentchild = getParentchildByUser(myUser);

        for (int i=0; i<4; i++) {
            updateDay(parentchild,
                    "우리 부모님은 어렸을 때부터 행복하고 좋은 기억을 많이 주셨고, 정말 행복하게 자랐어. 그 덕에 지금까지 행복하고 안정된 느낌을 받아.",
                    "오구 내 똥강아지~ 어렸을 때는 매일 볼 수 있었는데, 어른이 되고 나서 자주 못봐서 너무 아쉽다... 연락 잘하거라 요녀석~");
        }
        QnA fifthQnA = getTodayQnAByParentchild(parentchild);
        log.info("💖💖💖💖Day 5 QnA: {}", fifthQnA.getId());
        //TODO ⭐️SQS로 변경
//        fcmService.multipleSendByToken(FCMPushRequestDto.sendTodayQna(
//                fifthQnA.getQuestion().getSection().getValue(),
//                fifthQnA.getQuestion().getTopic()), parentchild.getId());

    }

    @Transactional
    public void todayUpdate(Long userId) {

        User myUser = getUserById(userId);
        Parentchild parentchild = getParentchildByUser(myUser);

        updateDay(parentchild,
                "우리 부모님은 어렸을 때부터 행복하고 좋은 기억을 많이 주셨고, 정말 행복하게 자랐어. 그 덕에 지금까지 행복하고 안정된 느낌을 받아.",
                "오구 내 똥강아지~ 어렸을 때는 매일 볼 수 있었는데, 어른이 되고 나서 자주 못봐서 너무 아쉽다... 연락 잘하거라 요녀석~");

        QnA todayQnA = getTodayQnAByParentchild(parentchild);
        //TODO ⭐️SQS로 변경
//        fcmService.multipleSendByToken(FCMPushRequestDto.sendTodayQna(
//                todayQnA.getQuestion().getSection().getValue(),
//                todayQnA.getQuestion().getTopic()), parentchild.getId());
    }

    private void updateDay(Parentchild parentchild, String childAnswer, String parentAnswer) {
        QnA currentQnA = getTodayQnAByParentchild(parentchild);
        log.info("💖💖💖💖Current QnA: {}", currentQnA.getId());
        currentQnA.saveChildAnswer(childAnswer);
        currentQnA.saveParentAnswer(parentAnswer);
        parentchild.addCount();
    }
}