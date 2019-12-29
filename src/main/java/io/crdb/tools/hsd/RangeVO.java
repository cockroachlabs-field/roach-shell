package io.crdb.tools.hsd;

public class RangeVO {
    /*
    create table crdb_internal.ranges_no_leases
(
	range_id int8 not null,
	start_key bytea not null,
	start_pretty text not null,
	end_key bytea not null,
	end_pretty text not null,
	database_name text not null,
	table_name text not null,
	index_name text not null,
	replicas _int8(19) not null,
	replica_localities _text not null,
	learner_replicas _int8(19) not null,
	split_enforced_until timestamp(6)
);

comment on table crdb_internal.ranges_no_leases is 'range metadata without leaseholder details (KV join; expensive!)';


     */

    private int rangeId;
    private String startKey;
    private String endKey;
    private String databaseName;
    private String tableName;
    private String indexName;

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
