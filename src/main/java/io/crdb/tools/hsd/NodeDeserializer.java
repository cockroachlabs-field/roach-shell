package io.crdb.tools.hsd;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.time.Instant;

public class NodeDeserializer extends JsonDeserializer<Node> {


    @Override
    public Node deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        JsonNode jsonNode = p.getCodec().readTree(p);

        JsonNode descNode = jsonNode.get("desc");

        return new Node(descNode.get("nodeId").intValue(),
                descNode.get("buildTag").asText(),
                Instant.ofEpochMilli(Long.parseLong(jsonNode.get("startedAt").textValue())),
                Long.parseLong(jsonNode.get("totalSystemMemory").textValue()),
                jsonNode.get("numCpus").intValue()
        );
    }
}
