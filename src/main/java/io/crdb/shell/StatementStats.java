package io.crdb.shell;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class StatementStats {

    @JsonProperty
    private int count;

    @JsonProperty
    private int firstCountAttempt;

    @JsonProperty
    private int maxRetries;

    @JsonProperty
    private String legacyLastErr;

    @JsonProperty
    private String legacyLastErrRedacted;

    @JsonProperty
    private StatementSensitiveInfo sensitiveInfo;

    private double meanNumRows;
    private double meanParseLat;
    private double meanPlanLat;
    private double meanRunLat;
    private double meanServiceLat;
    private double meanOverheadLat;
    private double meanBytesRead;
    private double meanRowsRead;


    @JsonProperty("numRows")
    private void unpackNumRows(Map<String,Double> numRows) {
        this.meanNumRows = numRows.get("mean");
    }

    @JsonProperty("parseLat")
    private void unpackParseLat(Map<String,Double> numRows) {
        this.meanParseLat = numRows.get("mean");
    }

    @JsonProperty("planLat")
    private void unpackPlanLat(Map<String,Double> numRows) {
        this.meanPlanLat = numRows.get("mean");
    }

    @JsonProperty("runLat")
    private void unpackRunLat(Map<String,Double> numRows) {
        this.meanRunLat = numRows.get("mean");
    }

    @JsonProperty("serviceLat")
    private void unpackServiceLat(Map<String,Double> numRows) {
        this.meanServiceLat = numRows.get("mean");
    }

    @JsonProperty("overheadLat")
   private void unpackOverheadLat(Map<String,Double> numRows) {
        this.meanOverheadLat = numRows.get("mean");
    }

    @JsonProperty("bytesRead")
    private void unpackBytesRead(Map<String,Double> numRows) {
        this.meanBytesRead = numRows.get("mean");
    }

    @JsonProperty("rowsRead")
    private void unpackRowsRead(Map<String,Double> numRows) {
        this.meanRowsRead = numRows.get("mean");
    }


    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getFirstCountAttempt() {
        return firstCountAttempt;
    }

    public void setFirstCountAttempt(int firstCountAttempt) {
        this.firstCountAttempt = firstCountAttempt;
    }

    public int getMaxRetries() {
        return maxRetries;
    }

    public void setMaxRetries(int maxRetries) {
        this.maxRetries = maxRetries;
    }

    public String getLegacyLastErr() {
        return legacyLastErr;
    }

    public void setLegacyLastErr(String legacyLastErr) {
        this.legacyLastErr = legacyLastErr;
    }

    public String getLegacyLastErrRedacted() {
        return legacyLastErrRedacted;
    }

    public void setLegacyLastErrRedacted(String legacyLastErrRedacted) {
        this.legacyLastErrRedacted = legacyLastErrRedacted;
    }

    public StatementSensitiveInfo getSensitiveInfo() {
        return sensitiveInfo;
    }

    public void setSensitiveInfo(StatementSensitiveInfo sensitiveInfo) {
        this.sensitiveInfo = sensitiveInfo;
    }

    public double getMeanNumRows() {
        return meanNumRows;
    }

    public double getMeanParseLat() {
        return meanParseLat;
    }

    public double getMeanPlanLat() {
        return meanPlanLat;
    }

    public double getMeanRunLat() {
        return meanRunLat;
    }

    public double getMeanServiceLat() {
        return meanServiceLat;
    }

    public double getMeanOverheadLat() {
        return meanOverheadLat;
    }

    public double getMeanBytesRead() {
        return meanBytesRead;
    }

    public double getMeanRowsRead() {
        return meanRowsRead;
    }

    @Override
    public String toString() {
        return "StatementStats{" +
               "count=" + count +
               ", firstCountAttempt=" + firstCountAttempt +
               ", maxRetries=" + maxRetries +
               ", legacyLastErr='" + legacyLastErr + '\'' +
               ", legacyLastErrRedacted='" + legacyLastErrRedacted + '\'' +
               ", sensitiveInfo=" + sensitiveInfo +
               ", meanNumRows=" + meanNumRows +
               ", meanParseLat=" + meanParseLat +
               ", meanPlanLat=" + meanPlanLat +
               ", meanRunLat=" + meanRunLat +
               ", meanServiceLat=" + meanServiceLat +
               ", meanOverheadLat=" + meanOverheadLat +
               ", meanBytesRead=" + meanBytesRead +
               ", meanRowsRead=" + meanRowsRead +
               '}';
    }
}
