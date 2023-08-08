package sopt.org.umbba.domain.domain.parentchild;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import sopt.org.umbba.domain.domain.common.AuditingTimeEntity;
import sopt.org.umbba.domain.domain.qna.OnboardingAnswer;
import sopt.org.umbba.domain.domain.qna.QnA;

import javax.persistence.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class Parentchild extends AuditingTimeEntity {

    @Id
    @Column(name = "parentchild_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "parentchild_id")
    private List<QnA> qnaList;

    @Column(nullable = false)
    private int count;

    public void addCount() {
        if (this.count < 7) {
            this.count += 1;
            log.info("Parentchild - addCount() 호출: {}", this.count);
        }
    }

    @Column(nullable = false)
    private String inviteCode;

    @Column(nullable = false)
    private boolean isInvitorChild;

    @Enumerated(EnumType.STRING)
    @Column(name = "answer", nullable = false)
    @ElementCollection
    private List<OnboardingAnswer> childOnboardingAnswerList = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "answer", nullable = false)
    @ElementCollection
    private List<OnboardingAnswer> parentOnboardingAnswerList = new ArrayList<>();

    public void changeChildOnboardingAnswerList(List<OnboardingAnswer> onboardingAnswerList) {
        this.childOnboardingAnswerList = onboardingAnswerList;
    }
    public void changeParentOnboardingAnswerList(List<OnboardingAnswer> onboardingAnswerList) {
        this.parentOnboardingAnswerList = onboardingAnswerList;
    }

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ParentchildRelation relation;

    @Column(nullable = false)
    private LocalTime pushTime;  // default: 오후 11시(클라이언트)

    public void initQnA() {
        qnaList = new ArrayList<>();
    }

    public void addQnA(QnA qnA) {
        qnaList.add(qnA);
    }

}
