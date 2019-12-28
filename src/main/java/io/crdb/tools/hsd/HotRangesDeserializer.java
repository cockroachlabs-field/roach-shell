package io.crdb.tools.hsd;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class HotRangesDeserializer extends JsonDeserializer<HotRanges> {

    @Override
    public HotRanges deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        JsonNode jsonNode = p.getCodec().readTree(p);

        String nodeId = jsonNode.get("nodeId").asText();

        Iterator<JsonNode> elements = jsonNode.get("hotRangesByNodeId").get(nodeId).get("stores").elements();

        List<Store> stores = new ArrayList<>();

        for (Iterator<JsonNode> it = elements; it.hasNext(); ) {
            JsonNode storeNode = it.next();
            Store store = storeNode.traverse(p.getCodec()).readValueAs(Store.class);
            store.setNodeId(Integer.parseInt(nodeId));
            stores.add(store);
        }

        return new HotRanges(stores);

    }

}
