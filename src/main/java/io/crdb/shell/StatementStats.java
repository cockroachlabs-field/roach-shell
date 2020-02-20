package io.crdb.shell;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class StatementStats {

    @JsonProperty
    private int count;

    @JsonProperty
    private int maxRetries;

    @JsonProperty
    private int rowsRead;

    @JsonProperty
    private StatementSensitiveInfo sensitiveInfo;

    private double meanOverallLatency;

    private int meanNumRows;

    @JsonProperty("numRows")
    private void unpackNumRows(Map<String,Object> numRows) {
        this.meanNumRows = (Integer)numRows.get("mean");
    }

    @JsonProperty("serviceLat")
    private void unpackServiceLat(Map<String,Object> serviceLat) {
        this.meanOverallLatency = (Double)serviceLat.get("mean");
    }

    public double getMeanOverallLatency() {
        return meanOverallLatency;
    }

    public int getMeanNumRows() {
        return meanNumRows;
    }

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
