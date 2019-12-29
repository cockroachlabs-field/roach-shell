package io.crdb.shell;

public class RangeVO {
    private final int rangeId;
    private final String startKey;
    private final String endKey;
    private final String databaseName;
    private final String tableName;
    private final String indexName;

    public RangeVO(int rangeId, String startKey, String endKey, String databaseName, String tableName, String indexName) {
        this.rangeId = rangeId;
        this.startKey = startKey;
        this.endKey = endKey;
        this.databaseName = databaseName;
        this.tableName = tableName;
        this.indexName = indexName;
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

    public String getDatabaseName() {
        return databaseName;
    }

    public String getTableName() {
        return tableName;
    }

    public String getIndexName() {
        return indexName;
    }



    @Override
    public String toString() {
        return "RangeVO{" +
                "rangeId=" + rangeId +
                ", startKey='" + startKey + '\'' +
                ", endKey='" + endKey + '\'' +
                ", databaseName='" + databaseName + '\'' +
                ", tableName='" + tableName + '\'' +
                ", indexName='" + indexName + '\'' +
                '}';
    }
}
