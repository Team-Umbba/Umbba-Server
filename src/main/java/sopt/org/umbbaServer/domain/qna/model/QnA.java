package sopt.org.umbbaServer.domain.qna.model;

import lombok.*;
import org.hibernate.annotations.ColumnDefault;
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
    @ColumnDefault("false")
    private Boolean isParentAnswer;

    @Column(nullable = false)
    @ColumnDefault("false")
    private Boolean isChildAnswer;
}
