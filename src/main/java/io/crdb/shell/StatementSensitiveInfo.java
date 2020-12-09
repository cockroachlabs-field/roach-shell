package io.crdb.shell;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

public class StatementSensitiveInfo {

    @JsonProperty
    private String lastErr;

    @JsonProperty
    private JsonNode mostRecentPlanDescription;

    @JsonProperty
    private String mostRecentPlanTimestamp;

    public JsonNode getMostRecentPlanDescription() {
        return mostRecentPlanDescription;
    }

    public void setMostRecentPlanDescription(JsonNode mostRecentPlanDescription) {
        this.mostRecentPlanDescription = mostRecentPlanDescription;
    }

    public String getMostRecentPlanTimestamp() {
        return mostRecentPlanTimestamp;
    }

    public void setMostRecentPlanTimestamp(String mostRecentPlanTimestamp) {
        this.mostRecentPlanTimestamp = mostRecentPlanTimestamp;
    }

    public String getLastErr() {
        return lastErr;
    }

    public void setLastErr(String lastErr) {
        this.lastErr = lastErr;
    }

    @Override
    public String toString() {
        return "StatementSensitiveInfo{" +
               "lastErr='" + lastErr + '\'' +
               ", mostRecentPlanDescription=" + mostRecentPlanDescription +
               ", mostRecentPlanTimestamp='" + mostRecentPlanTimestamp + '\'' +
               '}';
    }
}
