package sopt.org.umbbaServer;

import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Component;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;

@Slf4j
@SpringJUnitConfig(TestConfig.class)
@SpringBootTest
public class DBConnectionTest {

    private final String DB_URL = "jdbc:mysql://umbba-db.csqsqogfqnvj.ap-northeast-2.rds.amazonaws.com:3306/umbba_db?useSSL=true&useUnicode=true&serverTimezone=Asia/Seoul";
    private final String DB_USERNAME = "umbba_server";
    private final String DB_PASSWORD = "umbbaServer!";


    @Test
    void dataSourceDriverManager() throws SQLException, InterruptedException {
        //hikari pool 을 사용해서 커넥션 pooling
        //DriverManagerSource - 항상 새로운 커넥션을 획득
        DriverManagerDataSource dataSource = new DriverManagerDataSource(DB_URL, DB_USERNAME, DB_PASSWORD);
        useDataSource(dataSource);

        //커넥션에서 커넥션 풀이 생성되는 걸 보기위해
        Thread.sleep(1000);
    }

    @Test
    void dataSourceConnectionPool() throws SQLException, InterruptedException {
        //DriverManagerSource - 항상 새로운 커넥션을 획득

        //커넥션 풀링: HikariProxyConnection(Proxy) -> JdbcConnection(Target)
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(DB_URL);
        dataSource.setUsername(DB_USERNAME);
        dataSource.setPassword(DB_PASSWORD);

        dataSource.setMaximumPoolSize(10);
        dataSource.setPoolName("MyPool");

        useDataSource(dataSource);

        //커넥션에서 커넥션 풀이 생성되는 걸 보기위해
        Thread.sleep(1000);

    }


    private void useDataSource(DataSource dataSource) throws SQLException {
        Connection conn1 = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
        Connection conn2 = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
        log.info("connection={}, class={}", conn1, conn1.getClass());
        log.info("connection={}, class={}", conn2, conn2.getClass());
    }
}
