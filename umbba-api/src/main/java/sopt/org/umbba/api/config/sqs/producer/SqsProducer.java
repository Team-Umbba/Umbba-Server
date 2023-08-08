package sopt.org.umbba.api.config.sqs.producer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SqsProducer {

    @Value("${cloud.aws.sqs.notification.url}")
    private String notificationUrl;

    private static final String GROUP_ID = "sqs";
}
