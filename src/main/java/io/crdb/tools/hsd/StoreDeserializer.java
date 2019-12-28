package io.crdb.tools.hsd;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Lists;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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

        return new Store(jsonNode.get("storeId").intValue(), hotRanges);
    }
}
