package io.crdb.tools.hsd;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

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
    public CommandLineRunner run(RestTemplate restTemplate) throws Exception {
        // http://localhost:8080/_status/nodes
        // http://localhost:8080/_status/hotranges
        return args -> {
            NodeStatusWrapper wrapper = restTemplate.getForObject(
                    "http://localhost:8080/_status/nodes", NodeStatusWrapper.class);

            List<Node> nodes = wrapper.getNodes();


        };
    }

}
