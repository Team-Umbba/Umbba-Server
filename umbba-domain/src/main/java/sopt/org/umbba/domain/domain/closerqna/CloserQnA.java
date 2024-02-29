package sopt.org.umbba.domain.domain.closerqna;

import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import sopt.org.umbba.domain.domain.common.AuditingTimeEntity;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@SQLDelete(sql = "UPDATE closer_qna SET deleted=true WHERE closer_qna_id=?")
@Where(clause = "deleted=false")
public class CloserQnA extends AuditingTimeEntity {

    @Id
    @Column(name = "closer_qna_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "closer_question_id", nullable = false)
    private CloserQuestion closerQuestion;

    private int parentAnswer;

    private int childAnswer;

    @Column(nullable = false)
    private boolean isParentAnswer;

    @Column(nullable = false)
    private boolean isChildAnswer;

    private boolean deleted = Boolean.FALSE;

    public boolean isParentAnswer() {
        return isParentAnswer;
    }

    public boolean isChildAnswer() {
        return isChildAnswer;
    }

    public void saveParentAnswer(int answer) {
        this.parentAnswer = answer;
        this.isParentAnswer = true;
    }

    public void saveChildAnswer(int answer) {
        this.childAnswer = answer;
        this.isChildAnswer = true;
    }
}
