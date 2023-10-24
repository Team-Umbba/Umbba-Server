package sopt.org.umbba.domain.config.jpa;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import sopt.org.umbba.domain.UmbbaDomainRoot;

import com.querydsl.jpa.impl.JPAQueryFactory;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Configuration
@EntityScan(basePackageClasses = {UmbbaDomainRoot.class})
@EnableJpaRepositories(basePackageClasses = {UmbbaDomainRoot.class})
@EnableJpaAuditing
public class JpaConfig {

    @PersistenceContext
    private EntityManager em;
    @Bean
    public JPAQueryFactory jpaQueryFactory() {
        return new JPAQueryFactory(em);
    }
}
