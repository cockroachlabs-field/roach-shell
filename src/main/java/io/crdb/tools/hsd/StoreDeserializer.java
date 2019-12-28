package io.crdb.tools.hsd;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.primitives.Floats;

import java.io.IOException;
import java.util.*;

public class StoreDeserializer extends JsonDeserializer<Store> {


    @Override
    public Store deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {

        JsonNode jsonNode = p.getCodec().readTree(p);

        Iterator<JsonNode> elements = jsonNode.get("hotRanges").elements();

        List<HotRange> hotRanges = new ArrayList<>();
        for (Iterator<JsonNode> it = elements; it.hasNext(); ) {
            JsonNode rangeNode = it.next();
            HotRange hotRange = rangeNode.traverse(p.getCodec()).readValueAs(HotRange.class);
            hotRanges.add(hotRange);
        }

        hotRanges.sort((o1, o2) -> Floats.compare(o1.getQueriesPerSecond(), o2.getQueriesPerSecond()));

        Collections.reverse(hotRanges);

        return new Store(jsonNode.get("storeId").intValue(), hotRanges);
    }
}
