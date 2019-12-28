package io.crdb.tools.hsd;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;

public class HotRangeDeserializer extends JsonDeserializer<HotRange> {
    @Override
    public HotRange deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {

        JsonNode jsonNode = p.getCodec().readTree(p);

        JsonNode descNode = jsonNode.get("desc");

        float queriesPerSecond = jsonNode.get("queriesPerSecond").floatValue();

        return new HotRange(Integer.parseInt(descNode.get("rangeId").textValue()),
                descNode.get("startKey").textValue(),
                descNode.get("endKey").textValue(),
                queriesPerSecond);
    }
}
