package io.crdb.shell;

public class HotRangeVO {

    private final int nodeId;
    private final int storeId;
    private final int rangeId;

    private final String startKey;
    private final String endKey;
    private final float queriesPerSecond;

    public HotRangeVO(int nodeId, int storeId, int rangeId, String startKey, String endKey, float queriesPerSecond) {
        this.nodeId = nodeId;
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

    @Override
    public String toString() {
        return "HotRangeVO{" +
                "nodeId=" + nodeId +
                ", storeId=" + storeId +
                ", rangeId=" + rangeId +
                ", startKey='" + startKey + '\'' +
                ", endKey='" + endKey + '\'' +
                ", queriesPerSecond=" + queriesPerSecond +
                '}';
    }
}
