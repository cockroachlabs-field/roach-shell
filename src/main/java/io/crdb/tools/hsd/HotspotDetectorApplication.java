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
import org.springframework.jdbc.core.RowMapper;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.sql.DataSource;
import java.net.URI;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class HotspotDetectorApplication {

    private static final Logger log = LoggerFactory.getLogger(HotspotDetectorApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(HotspotDetectorApplication.class, args);
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }


    @Bean
    public CommandLineRunner run(RestTemplate restTemplate, Environment environment, JdbcTemplate jdbcTemplate) throws Exception {
        return args -> {
            URI nodesUri = UriComponentsBuilder.fromUriString("http://localhost:8080/_status/nodes").build().toUri();

            NodeStatusWrapper wrapper = restTemplate.getForObject(nodesUri, NodeStatusWrapper.class);

            List<Node> nodes = wrapper.getNodes();

            if (nodes == null || nodes.isEmpty()) {
                log.warn("Unable to find nodes");
                return;
            } else {
                log.debug("found {} nodes", nodes.size());
            }

            List<HotRangeVO> hotList = new ArrayList<>();

            for (Node node : nodes) {
                URI hotRangeUri = UriComponentsBuilder.fromUriString("http://localhost:8080/_status/hotranges")
                        .queryParam("node_id", node.getNodeId())
                        .build()
                        .toUri();

                HotRanges hotRanges = restTemplate.getForObject(hotRangeUri, HotRanges.class);

                for (Store store : hotRanges.getStores()) {

                    for (HotRange range : store.getHotRanges()) {
                        hotList.add(new HotRangeVO(node.getNodeId(), store.getStoreId(), range.getRangeId(), range.getStartKey(), range.getEndKey(), range.getQueriesPerSecond()));
                    }
                }

                log.debug("stop");
            }

            List<HotRangeVO> hotRangeVOS = Ordering.natural().onResultOf(new CompareQPSFunction()).greatestOf(hotList, 10);


            int i = 1;
            for (HotRangeVO vo : hotRangeVOS) {

                RangeVO rangeVO = jdbcTemplate.queryForObject("select * from crdb_internal.ranges_no_leases where range_id=?", new RowMapper<RangeVO>() {
                    @Override
                    public RangeVO mapRow(ResultSet resultSet, int i) throws SQLException {

                        return new RangeVO(resultSet.getInt("range_id"),
                                resultSet.getString("start_pretty"),
                                resultSet.getString("end_pretty"),
                                resultSet.getString("database_name"),
                                resultSet.getString("table_name"),
                                resultSet.getString("index_name")
                        );
                    }
                }, vo.getRangeId());


                log.info("#{} - {}, rangeVo {}", i, vo.toString(), rangeVO.toString());
                i++;
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
