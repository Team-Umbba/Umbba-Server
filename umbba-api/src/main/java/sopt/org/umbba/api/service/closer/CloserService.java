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
import sopt.org.umbba.domain.domain.closer.repository.CloserQnARepository;
import sopt.org.umbba.domain.domain.parentchild.Parentchild;
import sopt.org.umbba.domain.domain.user.User;
import sopt.org.umbba.domain.domain.user.repository.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CloserService {

    private final UserRepository userRepository;

    public TodayCloserQnAResponseDto getTodayCloserQnA(Long userId) {
        User user = getUserById(userId);
        Parentchild parentchild = user.getParentChild();
        if (parentchild == null) {
            throw new CustomException(ErrorType.USER_HAVE_NO_PARENTCHILD);
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

    private User getUserById(Long userId) {

        return userRepository.findById(userId).orElseThrow(
                () -> new CustomException(ErrorType.INVALID_USER)
        );
    }
}