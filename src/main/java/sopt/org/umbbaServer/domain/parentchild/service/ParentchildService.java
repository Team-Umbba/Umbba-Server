package sopt.org.umbbaServer.domain.parentchild.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sopt.org.umbbaServer.domain.parentchild.controller.dto.request.InviteCodeRequestDto;
import sopt.org.umbbaServer.domain.parentchild.controller.dto.request.OnboardingInviteRequestDto;
import sopt.org.umbbaServer.domain.parentchild.controller.dto.request.OnboardingReceiveRequestDto;
import sopt.org.umbbaServer.domain.parentchild.controller.dto.response.InviteResultResponseDto;
import sopt.org.umbbaServer.domain.parentchild.controller.dto.response.OnboardingInviteResponseDto;
import sopt.org.umbbaServer.domain.parentchild.controller.dto.response.OnboardingReceiveResponseDto;
import sopt.org.umbbaServer.domain.parentchild.dao.ParentchildDao;
import sopt.org.umbbaServer.domain.parentchild.model.Parentchild;
import sopt.org.umbbaServer.domain.parentchild.model.ParentchildRelation;
import sopt.org.umbbaServer.domain.parentchild.repository.ParentchildRepository;
import sopt.org.umbbaServer.domain.qna.model.OnboardingAnswer;
import sopt.org.umbbaServer.domain.user.model.User;
import sopt.org.umbbaServer.domain.user.repository.UserRepository;
import sopt.org.umbbaServer.global.config.ScheduleConfig;
import sopt.org.umbbaServer.global.exception.CustomException;
import sopt.org.umbbaServer.global.exception.ErrorType;
import sopt.org.umbbaServer.global.util.fcm.FCMScheduler;
import sopt.org.umbbaServer.global.util.fcm.FCMService;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ParentchildService {

    private final ParentchildRepository parentchildRepository;
    private final UserRepository userRepository;
    private final ParentchildDao parentchildDao;
    private final FCMScheduler fcmScheduler;
    private final FCMService fcmService;

    // [발신] 초대하는 측의 온보딩 정보 입력
    @Transactional
    public OnboardingInviteResponseDto onboardInvite(Long userId, OnboardingInviteRequestDto request) {

        User user = getUserById(userId);
        if (user.getParentChild() == null) {
            user.updateOnboardingInfo(
                    request.getUserInfo().getName(),
                    request.getUserInfo().getGender(),
                    request.getUserInfo().getBornYear()
            );
            log.info("isInvitorChild 요청값: {}", request.getIsInvitorChild());
            user.updateIsMeChild(request.getIsInvitorChild());
            log.info("업데이트 된 isMeChild 필드: {}", user.isMeChild());

            Parentchild parentchild = Parentchild.builder()
                    .inviteCode(generateInviteCode())
                    .isInvitorChild(request.getIsInvitorChild())
                    .relation(ParentchildRelation.relation(request.getUserInfo().getGender(), request.getRelationInfo(), request.getIsInvitorChild()))
                    .pushTime(request.getPushTime())   // TODO 케이스에 따라 없을 수도 있음
                    .count(1)
                    .build();
            parentchildRepository.save(parentchild);
            user.updateParentchild(parentchild);
            user.updateIsMatchFinish(true);
            log.info("userInfo: {}", request.getUserInfo().getBornYear());

            // String을 Enum으로 변경
            List<OnboardingAnswer> onboardingAnswerList = request.getOnboardingAnswerList().stream()
                    .map(OnboardingAnswer::of)
                    .collect(Collectors.toList());

            if (onboardingAnswerList.size() != 5) {
                throw new CustomException(ErrorType.INVALID_ONBOARDING_ANSWER_SIZE);
            }

            if (getUserById(userId).isMeChild()) {
                parentchild.changeChildOnboardingAnswerList(onboardingAnswerList);
            } else {
                parentchild.changeParentOnboardingAnswerList(onboardingAnswerList);
            }

            return OnboardingInviteResponseDto.of(parentchild, user);
        }

        throw new CustomException(ErrorType.ALREADY_EXISTS_PARENT_CHILD_USER);
    }


    // [수신] 초대받는 측의 온보딩 정보 입력
    @Transactional
    public OnboardingReceiveResponseDto onboardReceive(Long userId, OnboardingReceiveRequestDto request) throws InterruptedException {

        if (getUserById(userId).getParentChild() != null) {

            User user = getUserById(userId);
            user.updateOnboardingInfo(
                    request.getUserInfo().getName(),
                    request.getUserInfo().getGender(),
                    request.getUserInfo().getBornYear()
            );

            Parentchild parentchild = user.getParentChild();
//        parentchild.updateInfo();  TODO 온보딩 송수신 측의 관계 정보가 불일치한 경우에 대한 처리
            List<User> parentChildUsers = getParentChildUsers(parentchild);

            // String을 Enum으로 변경
            List<OnboardingAnswer> onboardingAnswerList = request.getOnboardingAnswerList().stream()
                    .map(OnboardingAnswer::of)
                    .collect(Collectors.toList());

            if (onboardingAnswerList.size() != 5) {
                throw new CustomException(ErrorType.INVALID_ONBOARDING_ANSWER_SIZE);
            }

            if (getUserById(userId).isMeChild()) {
                parentchild.changeChildOnboardingAnswerList(onboardingAnswerList);
            } else {
                parentchild.changeParentOnboardingAnswerList(onboardingAnswerList);
            }

            /*if (!ParentchildRelation.validate(parentChildUsers, parentchild.getRelation())) {
                throw new CustomException(ErrorType.INVALID_PARENT_CHILD_RELATION);
            }*/
            ScheduleConfig.resetScheduler();
            fcmScheduler.pushTodayQna();


            return OnboardingReceiveResponseDto.of(parentchild, user, parentChildUsers);
        }

        throw new CustomException(ErrorType.RECEIVE_AFTER_MATCH);
    }


    // 초대코드 생성 (형식예시: WUHZ-iGbPX9X)
    private String generateInviteCode() {
        return RandomStringUtils.randomAlphabetic(4).toUpperCase() +
                "-" + RandomStringUtils.randomAlphanumeric(6);
    }

    // 초대코드 확인 후 부모자식 관계 성립
    @Transactional
    public InviteResultResponseDto matchRelation(Long userId, InviteCodeRequestDto request) {

        log.info("ParentchlidService 실행 - 요청 초대코드: {}", request.getInviteCode());
        Parentchild newMatchRelation = parentchildRepository.findByInviteCode(request.getInviteCode()).orElseThrow(
                () -> new CustomException(ErrorType.INVALID_INVITE_CODE));
        User user = getUserById(userId);
        user.updateIsMeChild(!newMatchRelation.isInvitorChild());

        if (user.getParentChild() != null) {
            throw new CustomException(ErrorType.ALREADY_EXISTS_PARENT_CHILD_USER);
        }

        // TODO ParentChild에 연관된 User 수에 따른 예외처리
        // TODO 하나의 유저는 하나의 관계만 가지도록 예외처리
        user.updateParentchild(newMatchRelation);
        user.updateIsMatchFinish(true);
        log.info("로그인한 유저가 성립된 Parentchild Id: {}", user.getParentChild().getId());

        List<User> parentChildUsers = getParentChildUsers(newMatchRelation);
        if (!user.validateParentchild(parentChildUsers)) {
            throw new CustomException(ErrorType.INVALID_PARENT_CHILD_RELATION);
        }

        return InviteResultResponseDto.of(newMatchRelation, parentChildUsers);
    }

    public List<User> getParentChildUsers(Parentchild newMatchRelation) {
        return userRepository.findUserByParentChild(newMatchRelation);
    }


    private User getUserById(Long userId) {

        return userRepository.findById(userId).orElseThrow(
                () -> new CustomException(ErrorType.INVALID_USER)
        );
    }

}
