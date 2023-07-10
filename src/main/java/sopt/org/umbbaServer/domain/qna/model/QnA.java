package sopt.org.umbbaServer.domain.qna.model;

import lombok.*;
import sopt.org.umbbaServer.global.util.AuditingTimeEntity;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class QnA extends AuditingTimeEntity {

    @Id
    @Column(name = "qna_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "question_id")
    private Question question;

    private String parentAnswer;

    private String childAnswer;

    @Column(nullable = false)
    private boolean isParentAnswer;

    @Column(nullable = false)
    private boolean isChildAnswer;


    public boolean isParentAnswer() {
        return isParentAnswer;
    }

    public boolean isChildAnswer() {
        return isChildAnswer;
    }

    public void saveParentAnswer(String answer) {
        this.parentAnswer = answer;
        this.isParentAnswer = true;
    }

    public void saveChildAnswer(String answer) {
        this.childAnswer = answer;
        this.isChildAnswer = true;
    }
}
