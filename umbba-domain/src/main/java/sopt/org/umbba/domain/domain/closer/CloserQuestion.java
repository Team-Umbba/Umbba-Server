package sopt.org.umbba.domain.domain.closer;

import lombok.*;
import sopt.org.umbba.domain.domain.common.AuditingTimeEntity;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class CloserQuestion extends AuditingTimeEntity {

    @Id
    @Column(name = "closer_question_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String balanceQuestion;

    @Column(nullable = false)
    private String choiceAnswer1;

    @Column(nullable = false)
    private String choiceAnswer2;

    @Column(columnDefinition = "TEXT")
    private String imgUrl;
}
