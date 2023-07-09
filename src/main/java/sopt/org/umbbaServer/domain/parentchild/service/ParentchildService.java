package sopt.org.umbbaServer.domain.parentchild.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.utility.RandomString;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sopt.org.umbbaServer.domain.parentchild.controller.dto.request.OnboardingInviteRequestDto;
import sopt.org.umbbaServer.domain.parentchild.controller.dto.response.OnboardingInviteResponseDto;
import sopt.org.umbbaServer.domain.parentchild.model.Parentchild;
import sopt.org.umbbaServer.domain.parentchild.model.ParentchildRelation;
import sopt.org.umbbaServer.domain.parentchild.respository.ParentchildRepository;
import sopt.org.umbbaServer.domain.user.model.User;
import sopt.org.umbbaServer.domain.user.repository.UserRepository;
import sopt.org.umbbaServer.global.exception.CustomException;
import sopt.org.umbbaServer.global.exception.ErrorType;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ParentchildService {

    private final ParentchildRepository parentchildRepository;
    private final UserRepository userRepository;

    // [발신] 초대하는 측의 온보딩 정보 입력
    @Transactional
    public OnboardingInviteResponseDto onboardInvite(OnboardingInviteRequestDto request) {

        User user = userRepository.findById(request.getUserInfo().getUserId()).orElseThrow(
                () -> new CustomException(ErrorType.INVALID_USER)
        );
        user.updateOnboardingInfo(
                request.getUserInfo().getName(),
                request.getUserInfo().getGender(),
                request.getUserInfo().getBornYear()
        );

        Parentchild parentchild = Parentchild.builder()
                .inviteCode(generateInviteCode())
                .isInvitorChild(request.isInvitorChild())
                .relation(getRelation(request.getUserInfo().getGender(), request.getRelationInfo(), request.isInvitorChild()))
                .pushTime(request.getPushTime())  // TODO 케이스에 따라 없을 수도 있음
                .build();
        parentchildRepository.save(parentchild);

        log.info("userInfo: {}", request.getUserInfo().getBornYear());
        return OnboardingInviteResponseDto.of(parentchild, user);
    }

    // [수신] 초대받는 측의 온보딩 정보 입력
//    public OnboadringReceiveResponseDto

    // 부모자식 관계 케이스 분류하기
    private ParentchildRelation getRelation(String gender, String relationInfo, boolean isInvitorChild) {

        // 내가 부모다
        if (!isInvitorChild) {
            if (gender.equals("남자")) {    // 아빠
                if (relationInfo.equals("아들")) {
                    return ParentchildRelation.DAD_SON;
                } else if (relationInfo.equals("딸")) {
                    return ParentchildRelation.DAD_DAU;   // TODO 클라에서 둘 중 하나의 값만 받도록 처리하니까 else if 구문 빼도 무관
                }
            } else if(gender.equals("여자")) {   // 엄마
                if (relationInfo.equals("아들")) {
                    return ParentchildRelation.MOM_SON;
                } else if (relationInfo.equals("딸")) {
                    return ParentchildRelation.DAD_DAU;
                }
            }
        } else {   // 내가 자식이다
            if (gender.equals("남자")) {   // 아들
                if (relationInfo.equals("아빠")) {
                    return ParentchildRelation.DAD_SON;
                } else if (relationInfo.equals("엄마")) {
                    return ParentchildRelation.MOM_SON;
                }
            } else if(gender.equals("여자")) {   // 딸
                if (relationInfo.equals("아빠")) {
                    return ParentchildRelation.DAD_DAU;
                } else if (relationInfo.equals("엄마")) {
                    return ParentchildRelation.MOM_DAU;
                }
            }
        }

        throw new CustomException(ErrorType.INVALID_PARENT_CHILD_RELATION_INFO);
    }

    // 초대코드 생성 (형식예시: WUHZ-iGbPX9X)
    private String generateInviteCode() {
        return RandomStringUtils.randomAlphabetic(4).toUpperCase() +
                "-" + RandomStringUtils.randomAlphanumeric(6);
    }
}