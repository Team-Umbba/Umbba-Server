package sopt.org.umbba.domain.domain.user;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import sopt.org.umbba.domain.domain.common.AuditingTimeEntity;
import sopt.org.umbba.domain.domain.parentchild.Parentchild;

import javax.persistence.*;
import java.util.List;

@Slf4j
@Entity
@Table(name = "`User`")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class User extends AuditingTimeEntity {

    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //    @Column(nullable = false) // 사실 온보딩 단계에서 입력되기 때문에 nullable = true로 가져가야함
    private String username;

    //    @Column(nullable = false)
    private String gender;

    //    @Column(nullable = false)
    private Integer bornYear;

    @ManyToOne
    @JoinColumn(name = "parentchild_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))  // 외래 키 제약조건 제거
    private Parentchild parentChild;

    public void updateParentchild(Parentchild parentchild) {
        this.parentChild = parentchild;
    }

    @Column(nullable = false)
    private boolean isMeChild;

    public void updateIsMeChild(boolean isMeChild) {
        this.isMeChild = isMeChild;
    }

    @Column(nullable = false)
    private boolean isMatchFinish;

    public void updateIsMatchFinish(boolean isMatchFinish) {
        this.isMatchFinish = isMatchFinish;
    }

    private String refreshToken;

    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    // ** FCM 푸시 알림 관련 **
//    @Column(nullable = false)
    private String fcmToken;  // registration+token

    public void updateFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    // ** 소셜 로그인 관련 **
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SocialPlatform socialPlatform;

    @Column(nullable = false) // 이걸 PK로 가져갈지 고민
    private String socialId;

    private String socialNickname;

    private String socialProfileImage;

    private String socialAccessToken;

//    private String socialRefreshToken;
    //

    // 로그인 새롭게 할 때마다 해당 필드들 업데이트
    public void updateSocialInfo(String socialNickname, String socialProfileImage, String socialAccessToken/*, String socialRefreshToken*/) {
        this.socialNickname = socialNickname;
        this.socialProfileImage = socialProfileImage;
        this.socialAccessToken = socialAccessToken;
//        this.socialRefreshToken = socialRefreshToken;
    }

    public void updateOnboardingInfo(String name, String gender, Integer bornYear) {
        this.username = name;
        this.gender = gender;
        this.bornYear = bornYear;
    }

    public void deleteSocialInfo() {
        this.socialPlatform = SocialPlatform.WITHDRAW;
        this.socialNickname = null;
        this.socialProfileImage = null;
        this.socialAccessToken = null;
    }

    public User(SocialPlatform socialPlatform, String socialId) {
        this.socialPlatform = socialPlatform;
        this.socialId = socialId;
    }

    public boolean validateParentchild(List<User> parentChildUsers) {

        // 부모자식 관계에 대한 예외처리
        if (parentChildUsers.isEmpty()) {
            return false;
//            throw new CustomException(ErrorType.NOT_EXIST_PARENT_CHILD_USER);
        }

        if (parentChildUsers.size() == 1) {
            return false;
//            throw new CustomException(ErrorType.NOT_MATCH_PARENT_CHILD_RELATION);
        } else if (parentChildUsers.size() != 2) {
            return false;
//            throw new CustomException(ErrorType.INVALID_PARENT_CHILD_RELATION);
        }

        log.info("성립된 부모자식: {} X {}, 관계: {}", parentChildUsers.get(0).getUsername(), parentChildUsers.get(1).getUsername(), this.parentChild.getRelation());

        return true;
    }
}
