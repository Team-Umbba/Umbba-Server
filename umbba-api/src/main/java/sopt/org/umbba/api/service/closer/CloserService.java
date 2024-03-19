package sopt.org.umbba.api.service.closer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sopt.org.umbba.api.controller.closer.dto.request.TodayCloserAnswerRequestDto;
import sopt.org.umbba.api.controller.closer.dto.response.TodayCloserQnAResponseDto;
import sopt.org.umbba.common.exception.ErrorType;
import sopt.org.umbba.common.exception.model.CustomException;
import sopt.org.umbba.domain.domain.closer.CloserQnA;
import sopt.org.umbba.domain.domain.closer.CloserQuestion;
import sopt.org.umbba.domain.domain.closer.repository.CloserQnARepository;
import sopt.org.umbba.domain.domain.closer.repository.CloserQuestionRepository;
import sopt.org.umbba.domain.domain.parentchild.Parentchild;
import sopt.org.umbba.domain.domain.user.User;
import sopt.org.umbba.domain.domain.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CloserService {

    private final UserRepository userRepository;
    private final CloserQuestionRepository closerQuestionRepository;
    private final CloserQnARepository closerQnARepository;

    @Transactional
    public TodayCloserQnAResponseDto getTodayCloserQnA(Long userId) {
        User user = getUserById(userId);
        Parentchild parentchild = user.getParentChild();
        if (parentchild == null) {
            throw new CustomException(ErrorType.USER_HAVE_NO_PARENTCHILD);
        }

        if (parentchild.getCloserQnaList().isEmpty()) {
            addFirstCloserQnA(parentchild);
        }

        if (user.isMeChild()) {
            int closerCount = parentchild.getCloserChildCount();
            CloserQnA todayQnA = parentchild.getCloserQnaList().get(closerCount);

            if (!todayQnA.isChildAnswer()) { // Case 1 (내가 답변하지 않은 경우)
                return TodayCloserQnAResponseDto.of(todayQnA, 1, true);
            } else if (!todayQnA.isParentAnswer()) { // Case 2 (상대가 답변하지 않은 경우)
                return TodayCloserQnAResponseDto.of(todayQnA, 2, true);
            } else { // Case 3,4 (둘다 답변한 경우)
                return TodayCloserQnAResponseDto.of(todayQnA, 3, true);
            }

        } else {
            int closerCount = parentchild.getCloserParentCount();
            CloserQnA todayQnA = parentchild.getCloserQnaList().get(closerCount);

            if (!todayQnA.isParentAnswer()) { // Case 1 (내가 답변하지 않은 경우)
                return TodayCloserQnAResponseDto.of(todayQnA, 1, false);
            } else if (!todayQnA.isChildAnswer()) { // Case 2 (상대가 답변하지 않은 경우)
                return TodayCloserQnAResponseDto.of(todayQnA, 2, false);
            } else { // Case 3,4 (둘다 답변한 경우)
                return TodayCloserQnAResponseDto.of(todayQnA, 3, false);
            }
        }
    }

    @Transactional
    public void addFirstCloserQnA(Parentchild parentchild) {
        CloserQuestion firstCloserQuestion = closerQuestionRepository.findRandomExceptIds(new ArrayList<>())
                .orElseThrow(() -> new CustomException(ErrorType.NOT_FOUND_CLOSER_QUESTION));

        CloserQnA newCloserQnA = CloserQnA.builder()
                .closerQuestion(firstCloserQuestion)
                .isParentAnswer(false)
                .isChildAnswer(false)
                .build();
        closerQnARepository.save(newCloserQnA);
        parentchild.addCloserQna(newCloserQnA);
    }

    @Transactional
    public void answerTodayCloserQnA(Long userId, TodayCloserAnswerRequestDto request) {
        User user = getUserById(userId);
        Parentchild parentchild = user.getParentChild();
        if (parentchild == null) {
            throw new CustomException(ErrorType.USER_HAVE_NO_PARENTCHILD);
        }

        if (user.isMeChild()) {
            int closerCount = parentchild.getCloserChildCount();
            CloserQnA todayQnA = parentchild.getCloserQnaList().get(closerCount);

            todayQnA.saveChildAnswer(request.getAnswer());
        } else {
            int closerCount = parentchild.getCloserParentCount();
            CloserQnA todayQnA = parentchild.getCloserQnaList().get(closerCount);

            todayQnA.saveParentAnswer(request.getAnswer());
        }
    }

    @Transactional
    public void passToNextCloserQnA(Long userId) {
        User user = getUserById(userId);
        Parentchild parentchild = user.getParentChild();
        if (parentchild == null) {
            throw new CustomException(ErrorType.USER_HAVE_NO_PARENTCHILD);
        }

        if (user.isMeChild()) {
            if (parentchild.getCloserChildCount() < parentchild.getCloserParentCount()) {
                parentchild.addCloserChildCount();
            } else if (parentchild.getCloserChildCount() == parentchild.getCloserParentCount()) {
                parentchild.addCloserChildCount();
                CloserQuestion newCloserQuestion = closerQuestionRepository.findRandomExceptIds(getCloserQuestionIds(parentchild))
                        .orElseThrow(() -> new CustomException(ErrorType.NOT_FOUND_CLOSER_QUESTION));
                CloserQnA newCloserQnA = CloserQnA.builder()
                        .closerQuestion(newCloserQuestion)
                        .isParentAnswer(false)
                        .isChildAnswer(false)
                        .build();
                closerQnARepository.save(newCloserQnA);
                parentchild.addCloserQna(newCloserQnA);
            } else {
                throw new CustomException(ErrorType.INVALID_COUNT_STATUS);
            }
        } else {
            if (parentchild.getCloserParentCount() < parentchild.getCloserChildCount()) {
                parentchild.addCloserParentCount();
            } else if (parentchild.getCloserParentCount() == parentchild.getCloserChildCount()) {
                parentchild.addCloserParentCount();
                CloserQuestion newCloserQuestion = closerQuestionRepository.findRandomExceptIds(getCloserQuestionIds(parentchild))
                        .orElseThrow(() -> new CustomException(ErrorType.NOT_FOUND_CLOSER_QUESTION));
                CloserQnA newCloserQnA = CloserQnA.builder()
                        .closerQuestion(newCloserQuestion)
                        .isParentAnswer(false)
                        .isChildAnswer(false)
                        .build();
                closerQnARepository.save(newCloserQnA);
                parentchild.addCloserQna(newCloserQnA);
            } else {
                throw new CustomException(ErrorType.INVALID_COUNT_STATUS);
            }
        }
    }

    private static List<Long> getCloserQuestionIds(Parentchild parentchild) {
        return parentchild.getCloserQnaList().stream()
                .map(closerQnA -> closerQnA.getCloserQuestion().getId())
                .collect(Collectors.toList());
    }

    private User getUserById(Long userId) {

        return userRepository.findById(userId).orElseThrow(
                () -> new CustomException(ErrorType.INVALID_USER)
        );
    }
}