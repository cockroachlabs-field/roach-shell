package io.crdb.shell;

public class HotRangeVO {

    private final int nodeId;
    private final String nodeAddress;
    private final int storeId;
    private final int rangeId;

    private final String startKey;
    private final String endKey;
    private final float queriesPerSecond;

    public HotRangeVO(int nodeId, String nodeAddress, int storeId, int rangeId, String startKey, String endKey, float queriesPerSecond) {
        this.nodeId = nodeId;
        this.nodeAddress = nodeAddress;
        this.storeId = storeId;
        this.rangeId = rangeId;
        this.startKey = startKey;
        this.endKey = endKey;
        this.queriesPerSecond = queriesPerSecond;
    }

    public int getNodeId() {
        return nodeId;
    }

    public int getStoreId() {
        return storeId;
    }

    public int getRangeId() {
        return rangeId;
    }

    public String getStartKey() {
        return startKey;
    }

    public String getEndKey() {
        return endKey;
    }

    public float getQueriesPerSecond() {
        return queriesPerSecond;
    }

    public String getNodeAddress() {
        return nodeAddress;
    }

    @Override
    public String toString() {
        return "HotRangeVO{" +
                "nodeId=" + nodeId +
                ", nodeAddress='" + nodeAddress + '\'' +
                ", storeId=" + storeId +
                ", rangeId=" + rangeId +
                ", startKey='" + startKey + '\'' +
                ", endKey='" + endKey + '\'' +
                ", queriesPerSecond=" + queriesPerSecond +
                '}';
    }
}
