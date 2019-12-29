package io.crdb.tools.hsd;

import com.google.common.collect.Ordering;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.sql.DataSource;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class HotSpotDetectorApplication {

    private static final Logger log = LoggerFactory.getLogger(HotSpotDetectorApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(HotSpotDetectorApplication.class, args);
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }


    @Bean
    public CommandLineRunner run(RestTemplate restTemplate, Environment environment, JdbcTemplate jdbcTemplate) {
        return args -> {


            final String host = environment.getRequiredProperty("crdb.host");
            final String httpPort = environment.getProperty("crdb.http.port");
            final int maxHotRanges = environment.getProperty("crdb.hotranges.max", Integer.class, 10);

            String httpHost = environment.getProperty("crdb.http.host");

            if (StringUtils.isEmpty(httpHost)) {
                httpHost = host;
            }


            final URI nodesUri = UriComponentsBuilder.fromUriString(String.format("http://%s:%s/_status/nodes", httpHost, httpPort))
                    .build()
                    .toUri();

            final NodeStatusWrapper wrapper = restTemplate.getForObject(nodesUri, NodeStatusWrapper.class);

            Assert.notNull(wrapper, "problem fetching nodes");

            final List<HotRangeVO> hotList = new ArrayList<>();

            for (Node node : wrapper.getNodes()) {
                final URI hotRangeUri = UriComponentsBuilder.fromUriString(String.format("http://%s:%s/_status/hotranges", httpHost, httpPort))
                        .queryParam("node_id", node.getNodeId())
                        .build()
                        .toUri();

                final HotRanges hotRanges = restTemplate.getForObject(hotRangeUri, HotRanges.class);

                Assert.notNull(hotRanges, "problem fetching hot ranges for node " + node.getNodeId());

                for (Store store : hotRanges.getStores()) {
                    for (HotRange range : store.getHotRanges()) {
                        hotList.add(new HotRangeVO(node.getNodeId(), store.getStoreId(), range.getRangeId(), range.getStartKey(), range.getEndKey(), range.getQueriesPerSecond()));
                    }
                }
            }

            List<HotRangeVO> hotRangeVOS = Ordering.natural().onResultOf(new CompareQPSFunction()).greatestOf(hotList, maxHotRanges);

            for (HotRangeVO vo : hotRangeVOS) {

                RangeVO rangeVO = jdbcTemplate.queryForObject("select * from crdb_internal.ranges_no_leases where range_id = ?",
                        (resultSet, i) -> new RangeVO(resultSet.getInt("range_id"),
                                resultSet.getString("start_pretty"),
                                resultSet.getString("end_pretty"),
                                resultSet.getString("database_name"),
                                resultSet.getString("table_name"),
                                resultSet.getString("index_name")
                        ), vo.getRangeId());

                Assert.notNull(rangeVO, "unable to find range for id " + vo.getRangeId());

                log.info("{}, rangeVo {}", vo.toString(), rangeVO.toString());
            }


        };
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
