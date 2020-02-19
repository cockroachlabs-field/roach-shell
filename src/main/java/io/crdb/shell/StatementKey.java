package io.crdb.shell;

import com.fasterxml.jackson.annotation.JsonProperty;

public class StatementKey {

    @JsonProperty
    private StatementKeyData keyData;

    @JsonProperty
    private Integer nodeId;

    public StatementKeyData getKeyData() {
        return keyData;
    }

    public void setKeyData(StatementKeyData keyData) {
        this.keyData = keyData;
    }

    public Integer getNodeId() {
        return nodeId;
    }

    public void setNodeId(Integer nodeId) {
        this.nodeId = nodeId;
    }

    @Override
    public String toString() {
        return "StatementKey{" +
                "keyData=" + keyData +
                ", nodeId=" + nodeId +
                '}';
    }
}
