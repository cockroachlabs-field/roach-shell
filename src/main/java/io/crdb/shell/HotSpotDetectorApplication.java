package io.crdb.shell;

import com.zaxxer.hikari.HikariDataSource;
import org.jline.terminal.Terminal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.env.Environment;
import org.springframework.web.client.RestTemplate;

import javax.sql.DataSource;

@SpringBootApplication
public class HotSpotDetectorApplication {

    private static final Logger log = LoggerFactory.getLogger(HotSpotDetectorApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(HotSpotDetectorApplication.class, args);
    }


    @Bean
    public ShellHelper shellHelper(@Lazy Terminal terminal) {
        return new ShellHelper(terminal);
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }

    @Bean
    public DataSource getDataSource(Environment environment) {
        final String url = String.format("jdbc:postgresql://%s:%s/%s?ApplicationName=HotSpotDetector&sslmode=%s",
                environment.getProperty("crdb.host"),
                environment.getProperty("crdb.port"),
                environment.getProperty("crdb.database"),
                environment.getProperty("crdb.ssl.mode"));

        log.info("CockroachDB URL = [{}]", url);

        return DataSourceBuilder.create()
                .type(HikariDataSource.class)
                .driverClassName("org.postgresql.Driver")
                .url(url)
                .username(environment.getProperty("crdb.username"))
                .password(environment.getProperty("crdb.password"))
                .build();
    }


}
