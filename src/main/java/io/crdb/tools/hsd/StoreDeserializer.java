package io.crdb.tools.hsd;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.primitives.Floats;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class StoreDeserializer extends JsonDeserializer<Store> {
    private static final Logger log = LoggerFactory.getLogger(HotSpotDetectorApplication.class);


    @Override
    public Store deserialize(JsonParser p, DeserializationContext context) throws IOException {

        JsonNode jsonNode = p.getCodec().readTree(p);

        Iterator<JsonNode> elements = jsonNode.get("hotRanges").elements();

        List<HotRange> hotRanges = new ArrayList<>();
        for (; elements.hasNext(); ) {
            JsonNode rangeNode = elements.next();
            HotRange hotRange = rangeNode.traverse(p.getCodec()).readValueAs(HotRange.class);

            // we don't need ranges with no activity
            if (hotRange.getQueriesPerSecond() > 0) {
                hotRanges.add(hotRange);
            }
        }

        hotRanges.sort((o1, o2) -> Floats.compare(o1.getQueriesPerSecond(), o2.getQueriesPerSecond()));

        Collections.reverse(hotRanges);

        return new Store(jsonNode.get("storeId").intValue(), hotRanges);
    }
}
