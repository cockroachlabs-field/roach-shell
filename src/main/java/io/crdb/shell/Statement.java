package io.crdb.shell;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Statement {

    @JsonProperty
    private StatementKey key;

    @JsonProperty
    private StatementStats stats;

    public StatementKey getKey() {
        return key;
    }

    public void setKey(StatementKey key) {
        this.key = key;
    }

    public StatementStats getStats() {
        return stats;
    }

    public void setStats(StatementStats stats) {
        this.stats = stats;
    }

    @Override
    public String toString() {
        return "Statement{" +
                "key=" + key +
                ", stats=" + stats +
                '}';
    }
}
