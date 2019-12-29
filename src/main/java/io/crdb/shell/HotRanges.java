package io.crdb.shell;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.List;

@JsonDeserialize(using = HotRangesDeserializer.class)
public class HotRanges {

    private final List<Store> stores;

    public HotRanges(List<Store> stores) {
        this.stores = stores;
    }

    public List<Store> getStores() {
        return stores;
    }
}
