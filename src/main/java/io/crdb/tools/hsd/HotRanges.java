package io.crdb.tools.hsd;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.List;

@JsonDeserialize(using = HotRangesDeserializer.class)
public class HotRanges {

    private List<Store> stores;

    public HotRanges(List<Store> stores) {
        this.stores = stores;
    }

    public List<Store> getStores() {
        return stores;
    }
}
