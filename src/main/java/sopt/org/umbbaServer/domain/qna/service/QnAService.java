package sopt.org.umbbaServer.domain.qna.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sopt.org.umbbaServer.domain.parentchild.dao.ParentchildDao;
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
import sopt.org.umbbaServer.domain.user.social.SocialPlatform;
import sopt.org.umbbaServer.global.exception.CustomException;
import sopt.org.umbbaServer.global.exception.ErrorType;

import javax.persistence.NoResultException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QnAService {

    private final QnARepository qnARepository;
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final ParentchildRepository parentchildRepository;
    private final ParentchildDao parentchildDao;
    private final QnADao qnADao;

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
    public void createQnA() {
        QnA newQnA = QnA.builder()
                .question(questionRepository.findById(1L).get()) // 필터 로직 추가되어야함
                .isParentAnswer(false)
                .isChildAnswer(false)
                .build();
        qnARepository.save(newQnA);

        Parentchild parentchild = parentchildRepository.findById(1L).get();
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
