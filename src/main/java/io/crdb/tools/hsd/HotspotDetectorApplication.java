package io.crdb.tools.hsd;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public CommandLineRunner run(RestTemplate restTemplate) throws Exception {
        return args -> {
            NodeStatusWrapper wrapper = restTemplate.getForObject("http://localhost:8080/_status/nodes", NodeStatusWrapper.class);

            List<Node> nodes = wrapper.getNodes();

            if (nodes == null || nodes.isEmpty()) {
                log.warn("Unable to find nodes");
                return;
            } else {
                log.debug("found {} nodes", nodes.size());
            }

            for (Node node : nodes) {
                UriComponentsBuilder builder = UriComponentsBuilder.fromUriString("http://localhost:8080/_status/hotranges")
                        .queryParam("node_id", node.getNodeId());

                HotRanges hotRanges = restTemplate.getForObject(builder.toUriString(), HotRanges.class);

                log.debug("stop");
            }


        };
    }

}
