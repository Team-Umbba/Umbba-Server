package sopt.org.umbbaServer.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/**
 * 특정 시간대에 알림을 보내주기 위해 Spring이 제공하는 TaskScheduler를 빈으로 등록
 */
@Configuration
public class ScheduleConfig {

    private final int POOL_SIZE = 10;

    public TaskScheduler scheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(POOL_SIZE);
        return scheduler;
    }
}