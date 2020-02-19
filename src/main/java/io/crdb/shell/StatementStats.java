package io.crdb.shell;

import com.fasterxml.jackson.annotation.JsonProperty;

public class StatementStats {

    @JsonProperty
    private int count;

    @JsonProperty
    private int maxRetries;

    @JsonProperty
    private int rowsRead;

    @JsonProperty
    private StatementSensitiveInfo sensitiveInfo;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getMaxRetries() {
        return maxRetries;
    }

    public void setMaxRetries(int maxRetries) {
        this.maxRetries = maxRetries;
    }

    public int getRowsRead() {
        return rowsRead;
    }

    public void setRowsRead(int rowsRead) {
        this.rowsRead = rowsRead;
    }

    public StatementSensitiveInfo getSensitiveInfo() {
        return sensitiveInfo;
    }

    public void setSensitiveInfo(StatementSensitiveInfo sensitiveInfo) {
        this.sensitiveInfo = sensitiveInfo;
    }

    @Override
    public String toString() {
        return "StatementStats{" +
                "count=" + count +
                ", maxRetries=" + maxRetries +
                ", rowsRead=" + rowsRead +
                ", sensitiveInfo=" + sensitiveInfo +
                '}';
    }
}
