package sopt.org.umbba.domain.domain.user;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.ConstraintMode;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sopt.org.umbba.domain.domain.common.AuditingTimeEntity;
import sopt.org.umbba.domain.domain.parentchild.Parentchild;

@Slf4j
@Entity
@Table(name = "`User`")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@SQLDelete(sql = "UPDATE user SET deleted=true WHERE user_id=?")
@Where(clause = "deleted=false")
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

    private boolean deleted = Boolean.FALSE;

    private boolean isFirstEntry = Boolean.TRUE;

    private boolean isEndingDone = Boolean.FALSE;

    private LocalDateTime lastRerollChange = LocalDateTime.of(2024, 3, 1, 12, 0);

    public void updateLastRerollChange() {
        this.lastRerollChange = LocalDateTime.now();
    }

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

    public void updateIsFirstEntry() {
        this.isFirstEntry = false;
    }

    public void updateIsEndingDone() { this.isEndingDone = true; }

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
