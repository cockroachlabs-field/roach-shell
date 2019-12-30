package io.crdb.shell;

import com.zaxxer.hikari.HikariDataSource;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
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
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import javax.sql.DataSource;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.function.Supplier;

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
    public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder) throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {

        Supplier<ClientHttpRequestFactory> supplier = () -> {
            try {
                SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustSelfSignedStrategy()).build();
                SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(sslContext);

                CloseableHttpClient httpClient = HttpClients.custom()
                        .setSSLHostnameVerifier(new NoopHostnameVerifier())
                        .setSSLSocketFactory(socketFactory)
                        .build();

                HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
                requestFactory.setHttpClient(httpClient);

                return requestFactory;
            } catch (NoSuchAlgorithmException | KeyStoreException | KeyManagementException e) {
                throw new RuntimeException(e);
            }
        };

        return restTemplateBuilder
                .requestFactory(supplier)
                .build();
    }

    @Bean
    public DataSource getDataSource(Environment environment) {

        boolean secure = environment.getProperty("crdb.secure.enabled", Boolean.class, Boolean.FALSE);

        String url = String.format("jdbc:postgresql://%s:%s/%s?ApplicationName=HotSpotDetector&sslmode=%s",
                environment.getProperty("crdb.host"),
                environment.getProperty("crdb.port"),
                environment.getProperty("crdb.database"),
                environment.getProperty("crdb.ssl.mode"));

        if (secure) {
            url += String.format("&sslcert=%s&sslkey=%s", "/Users/tv/dev/projects/docker-examples/example-secure/client.root.crt", "/Users/tv/dev/projects/docker-examples/example-secure/client.root.key.pk8");
        }

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
