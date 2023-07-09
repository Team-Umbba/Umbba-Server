package sopt.org.umbbaServer.domain.parentchild.model;

import lombok.*;
import sopt.org.umbbaServer.domain.qna.model.QnA;
import sopt.org.umbbaServer.global.util.AuditingTimeEntity;

import javax.persistence.*;
import java.time.LocalTime;
import java.util.List;

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

    @OneToMany
    @JoinColumn(name = "parentchild_id")
    private List<QnA> qnaList;

    @Column(nullable = false)
    private String inviteCode;

//    @Column(nullable = false)
    private boolean isInvitorChild;

    // TODO 기획에 따라 변경사항 있음
    private boolean liveTogether;

//    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ParentchildRelation relation;

    @Column(nullable = false)  // TODO 푸시알림 시간 디폴트 값 없으면 nullable: true로 변경
    private LocalTime pushTime;  // default: 오후 11시

    //== 연관관계 메서드 ==//
    public void addQna(QnA qna) {
        this.qnaList.add(qna);
    }
}
