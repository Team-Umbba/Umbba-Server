package sopt.org.umbbaServer.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * 특정 시간대에 알림을 보내주기 위해 Spring이 제공하는 TaskScheduler를 빈으로 등록
 */
@Configuration
public class ScheduleConfig implements SchedulingConfigurer {

    private final int POOL_SIZE = 10;

    @Bean
    public TaskScheduler scheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(POOL_SIZE);
        scheduler.setThreadNamePrefix("my-scheduled-task-pool-");
        scheduler.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
        return scheduler;
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(POOL_SIZE);
        scheduler.setThreadNamePrefix("my-scheduled-task-pool-");
        scheduler.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
        scheduler.initialize();

        taskRegistrar.setTaskScheduler(scheduler);
    }

    // 스케줄러 중지 후 재시작 (초기화)
    /*public void resetScheduler() {
        task
    }*/


    /*@Bean
    public TaskScheduler scheduler() {
        return new ConcurrentTaskScheduler();
    }*/
}
