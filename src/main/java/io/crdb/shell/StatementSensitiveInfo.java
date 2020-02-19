package io.crdb.shell;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

public class StatementSensitiveInfo {

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

    @Override
    public String toString() {
        return "StatementSensitiveInfo{" +
                "mostRecentPlanDescription=" + mostRecentPlanDescription +
                ", mostRecentPlanTimestamp='" + mostRecentPlanTimestamp + '\'' +
                '}';
    }
}
