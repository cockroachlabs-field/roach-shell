package io.crdb.tools.hsd;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.List;

@JsonDeserialize(using = StoreDeserializer.class)
public class Store {

    private int storeId;

    private List<HotRange> hotRanges;

    public Store(int storeId, List<HotRange> hotRanges) {
        this.storeId = storeId;
        this.hotRanges = hotRanges;
    }

    public int getStoreId() {
        return storeId;
    }

    public List<HotRange> getHotRanges() {
        return hotRanges;
    }
}
