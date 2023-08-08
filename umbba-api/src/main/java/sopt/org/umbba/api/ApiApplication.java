package sopt.org.umbba.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import sopt.org.umbba.common.UmbbaCommonRoot;
import sopt.org.umbba.domain.UmbbaDomainRoot;
import sopt.org.umbba.external.UmbbaExternalRoot;

@SpringBootApplication(scanBasePackageClasses = {
        UmbbaCommonRoot.class,
        UmbbaDomainRoot.class,
        UmbbaExternalRoot.class,
        ApiApplication.class
})
public class ApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiApplication.class, args);
    }

}
