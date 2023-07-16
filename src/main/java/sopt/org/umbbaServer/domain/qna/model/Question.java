package sopt.org.umbbaServer.domain.qna.model;

import lombok.*;
import sopt.org.umbbaServer.global.util.AuditingTimeEntity;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class Question extends AuditingTimeEntity {

    @Id
    @Column(name = "question_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String parentQuestion;

    @Column(nullable = false)
    private String childQuestion;

    @Column(nullable = false)
    private String topic;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private QuestionSection section;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private QuestionType type;
}
