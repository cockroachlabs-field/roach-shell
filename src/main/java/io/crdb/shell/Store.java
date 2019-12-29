package io.crdb.shell;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.List;

@JsonDeserialize(using = StoreDeserializer.class)
public class Store {

    private int nodeId;

    private final int storeId;

    private final List<HotRange> hotRanges;

    public Store(int storeId, List<HotRange> hotRanges) {
        this.storeId = storeId;
        this.hotRanges = hotRanges;
    }

    public int getNodeId() {
        return nodeId;
    }

    public void setNodeId(int nodeId) {
        this.nodeId = nodeId;
    }

    public int getStoreId() {
        return storeId;
    }

    public List<HotRange> getHotRanges() {
        return hotRanges;
    }
}
