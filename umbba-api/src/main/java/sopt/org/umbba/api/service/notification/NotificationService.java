package sopt.org.umbba.api.service.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * SQS 대기열로 알림 메시지를 추가
 */
@RequiredArgsConstructor
@Service
@Transactional
public class NotificationService {
}
