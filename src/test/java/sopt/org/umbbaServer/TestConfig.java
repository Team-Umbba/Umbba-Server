package sopt.org.umbbaServer;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@TestConfiguration
public class TestConfig {

    private final String DB_URL = "jdbc:mysql://umbba-db.csqsqogfqnvj.ap-northeast-2.rds.amazonaws.com:3306/umbba_db?useSSL=true&useUnicode=true&serverTimezone=Asia/Seoul";
    private final String DB_USERNAME = "umbba_server";
    private final String DB_PASSWORD = "umbbaServer!";

    @Bean
    public DataSource dataSource() {
        return new DriverManagerDataSource(DB_URL, DB_USERNAME, DB_PASSWORD);
    }
}
