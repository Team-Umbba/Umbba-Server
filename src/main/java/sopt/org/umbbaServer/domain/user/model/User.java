package sopt.org.umbbaServer.domain.user.model;

import lombok.*;
import org.hibernate.annotations.Parent;
import sopt.org.umbbaServer.domain.parentchild.model.Parentchild;
import sopt.org.umbbaServer.domain.user.social.SocialPlatform;
import sopt.org.umbbaServer.global.util.AuditingTimeEntity;

import javax.persistence.*;

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

    @Column(nullable = false)
    private boolean hasAlarm;

    @ManyToOne
    @JoinColumn(name = "parentchild_id")
    private Parentchild parentChild;

    @Column(nullable = false)
    private Boolean isMeChild;

    public void updateParentchild(Parentchild parentchild) {
        this.parentChild = parentchild;
    }
    
    public void updateIsMeChild(boolean isMeChild) {
        this.isMeChild = isMeChild;
    }

    private String refreshToken;

    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
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

    public boolean isHasAlarm() {
        return hasAlarm;
    }

    public boolean isMeChild() {
        return isMeChild;
    }

    public User(SocialPlatform socialPlatform, String socialId) {
        this.socialPlatform = socialPlatform;
        this.socialId = socialId;
    }
}
