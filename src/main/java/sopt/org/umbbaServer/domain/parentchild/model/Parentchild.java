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

    @Column(nullable = false)
    private Boolean isInvitorChild;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ParentchildRelation relation;

    @Column(nullable = false)
    private LocalTime pushTime;

    public void addQnA(QnA qnA) {
        qnaList.add(qnA);
    }
}
