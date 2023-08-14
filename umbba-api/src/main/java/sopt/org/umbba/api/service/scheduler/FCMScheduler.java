package sopt.org.umbba.api.service.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import sopt.org.umbba.api.config.sqs.producer.SqsProducer;
import sopt.org.umbba.common.sqs.dto.FCMPushRequestDto;
import sopt.org.umbba.domain.domain.parentchild.Parentchild;
import sopt.org.umbba.domain.domain.parentchild.dao.ParentchildDao;
import sopt.org.umbba.domain.domain.parentchild.repository.ParentchildRepository;
import sopt.org.umbba.domain.domain.qna.QnA;
import sopt.org.umbba.domain.domain.user.SocialPlatform;
import sopt.org.umbba.domain.domain.user.User;
import sopt.org.umbba.domain.domain.user.repository.UserRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PessimisticLockException;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class FCMScheduler {

    private final ParentchildRepository parentchildRepository;
    private final UserRepository userRepository;
    private final ParentchildDao parentchildDao;
    private final SqsProducer sqsProducer;

    private static ScheduledFuture<?> scheduledFuture;
    private final TaskScheduler taskScheduler;

    private final PlatformTransactionManager transactionManager;

    @PersistenceContext
    private EntityManager em;

    @Scheduled(cron = "0 0 0 * * ?", zone = "Asia/Seoul")  // 초기값
    public String pushTodayQna()  {

        log.info("오늘의 질문 알람 - 유저마다 보내는 시간 다름");
//        List<String> tokenList = parentchildDao.findFcmTokensById(parentchildId);


        parentchildRepository.findAll().stream()
                .filter(pc -> {
                    List<String> tokenList = parentchildDao.findFcmTokensById(pc.getId());
                    List<User> parentChildUsers = userRepository.findUserByParentChild(pc);
                    return tokenList != null &&
                            tokenList.size() == 2 &&
                            parentChildUsers.stream()
                                    .allMatch(user -> user.validateParentchild(parentChildUsers) && !user.getSocialPlatform().equals(SocialPlatform.WITHDRAW));
                })
                .forEach(pc -> {
                log.info(pc.getId() + "번째 Parentchild");
                String cronExpression = String.format("0 %s %s * * ?", pc.getPushTime().getMinute(), pc.getPushTime().getHour());
//                String cronExpression = String.format("*/20 * * * * *");
                log.info("cron: {}", cronExpression);
                schedulePushAlarm(cronExpression, pc.getId());
            })
                ;
        return "Today QnA messages were sent successfully";
    }

    private void schedulePushAlarm(String cronExpression, Long parentchildId) {

        scheduledFuture = taskScheduler.schedule(() -> {

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            Parentchild parentchild = parentchildRepository.findById(parentchildId).get();

            TransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
            TransactionStatus transactionStatus = transactionManager.getTransaction(transactionDefinition);

            log.info("성립된 부모자식- 초대코드: {}, 인덱스: {}", parentchild.getInviteCode(), parentchild.getCount());

            try {
                if (!parentchild.getQnaList().isEmpty()) {

                    QnA currentQnA = parentchild.getQnaList().get(parentchild.getCount() - 1);
                    if (currentQnA.isParentAnswer() && currentQnA.isChildAnswer()) {

                        log.info("둘 다 답변함 다음 질문으로 ㄱ {}", parentchild.getCount());
                        parentchild.addCount();
                        Parentchild pc = em.merge(parentchild);

                        transactionManager.commit(transactionStatus);
                        log.info("스케줄링 작업 예약 내 addCount 후 count: {}", pc.getCount());

                        QnA todayQnA = parentchild.getQnaList().get(parentchild.getCount() - 1);
//                        em.close();

                        log.info("\n  Current QnA: {}  \n  Today QnA: {}", currentQnA.getId(), todayQnA.getId());
                        if (todayQnA == null) {
                            log.error("{}번째 Parentchild의 QnaList가 존재하지 않음!", parentchild.getId());
                        }


                        List<User> parentChildUsers = userRepository.findUserByParentChild(parentchild);
                        if (parentChildUsers.stream().
                                allMatch(user -> user.validateParentchild(parentChildUsers) && !user.getSocialPlatform().equals(SocialPlatform.WITHDRAW))) {

                            log.info("FCMService - schedulePushAlarm() 실행");
                            log.info("FCMService-schedulePushAlarm() topic: {}", todayQnA.getQuestion().getTopic());
                            multipleSendByToken(FCMPushRequestDto.sendTodayQna(
                                    todayQnA.getQuestion().getSection().getValue(),
                                    todayQnA.getQuestion().getTopic()), parentchild.getId());
                            multipleSendByToken(FCMPushRequestDto.sendTodayQna("술이슈", "새벽4시 술 먹을시간"), 3L);
                        }
                    }
                }
            } catch (PessimisticLockingFailureException | PessimisticLockException e) {
                transactionManager.rollback(transactionStatus);
            } finally {
                em.close();
            }

            // 현재 실행중인 쓰레드 확인
            log.info("Current Thread : {}", Thread.currentThread().getName());

        }, new CronTrigger(cronExpression));
    }



    // 스케줄러에서 예약된 작업을 제거하는 메서드
    public static void clearScheduledTasks() {
        if (scheduledFuture != null) {
            log.info("이전 스케줄링 예약 취소!");
            scheduledFuture.cancel(false);
        }
        log.info("ScheduledFuture: {}", scheduledFuture);
    }



//    @Scheduled(cron = "0 0 4 * * ?", zone = "Asia/Seoul")
//    public String drink() {
//        fcmService.multipleSendByToken(FCMPushRequestDto.sendTodayQna("술이슈", "새벽4시 술 먹을시간"), 3L);
//
//        return "Today QnA messages were sent successfully";
//    }
}
