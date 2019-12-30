package io.crdb.shell;

import com.google.common.collect.Ordering;
import com.google.common.collect.TreeBasedTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.shell.table.ArrayTableModel;
import org.springframework.shell.table.BorderStyle;
import org.springframework.shell.table.Table;
import org.springframework.shell.table.TableBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.SortedSet;

@Service
public class HotSpotService {

    private static final Logger log = LoggerFactory.getLogger(HotSpotDetectorApplication.class);

    private final RestTemplate restTemplate;
    private final Environment environment;
    private final JdbcTemplate jdbcTemplate;

    public HotSpotService(RestTemplate restTemplate, Environment environment, JdbcTemplate jdbcTemplate) {
        this.restTemplate = restTemplate;
        this.environment = environment;
        this.jdbcTemplate = jdbcTemplate;
    }

    public Table getHotSpots() {


        final String host = environment.getRequiredProperty("crdb.host");
        final String httpPort = environment.getProperty("crdb.http.port");
        final int maxHotRanges = environment.getProperty("crdb.hotranges.max", Integer.class, 10);

        String httpHost = environment.getProperty("crdb.http.host");

        if (StringUtils.isEmpty(httpHost)) {
            httpHost = host;
        }


        final URI nodesUri = UriComponentsBuilder.fromUriString(String.format("http://%s:%s/_status/nodes", httpHost, httpPort))
                .build()
                .toUri();

        final NodeStatusWrapper wrapper = restTemplate.getForObject(nodesUri, NodeStatusWrapper.class);

        Assert.notNull(wrapper, "problem fetching nodes");

        final List<HotRangeVO> hotList = new ArrayList<>();

        for (Node node : wrapper.getNodes()) {
            final URI hotRangeUri = UriComponentsBuilder.fromUriString(String.format("http://%s:%s/_status/hotranges", httpHost, httpPort))
                    .queryParam("node_id", node.getNodeId())
                    .build()
                    .toUri();

            final HotRanges hotRanges = restTemplate.getForObject(hotRangeUri, HotRanges.class);

            Assert.notNull(hotRanges, "problem fetching hot ranges for node " + node.getNodeId());

            for (Store store : hotRanges.getStores()) {
                for (HotRange range : store.getHotRanges()) {
                    hotList.add(new HotRangeVO(node.getNodeId(), store.getStoreId(), range.getRangeId(), range.getStartKey(), range.getEndKey(), range.getQueriesPerSecond()));
                }
            }
        }

        List<HotRangeVO> hotRangeVOS = Ordering.natural().onResultOf(new CompareQPSFunction()).greatestOf(hotList, maxHotRanges);

        TreeBasedTable<Integer, Integer, String> treeBasedTable = TreeBasedTable.create();

        treeBasedTable.put(0, 0, "Rank");
        treeBasedTable.put(0, 1, "QPS");
        treeBasedTable.put(0, 2, "Node ID");
        treeBasedTable.put(0, 3, "Store ID");
        treeBasedTable.put(0, 4, "Database");
        treeBasedTable.put(0, 5, "Table");
        treeBasedTable.put(0, 6, "Index");
        treeBasedTable.put(0, 7, "Range");

        int rowCount = 1;

        for (HotRangeVO vo : hotRangeVOS) {

            RangeVO rangeVO = jdbcTemplate.queryForObject("select * from crdb_internal.ranges_no_leases where range_id = ?",
                    (resultSet, i) -> new RangeVO(resultSet.getInt("range_id"),
                            resultSet.getString("start_pretty"),
                            resultSet.getString("end_pretty"),
                            resultSet.getString("database_name"),
                            resultSet.getString("table_name"),
                            resultSet.getString("index_name")
                    ), vo.getRangeId());

            Assert.notNull(rangeVO, "unable to find range for id " + vo.getRangeId());

            treeBasedTable.put(rowCount, 0, Integer.toString(rowCount));
            treeBasedTable.put(rowCount, 1, Float.toString(vo.getQueriesPerSecond()));
            treeBasedTable.put(rowCount, 2, Integer.toString(vo.getNodeId()));
            treeBasedTable.put(rowCount, 3, Integer.toString(vo.getStoreId()));
            treeBasedTable.put(rowCount, 4, rangeVO.getDatabaseName());
            treeBasedTable.put(rowCount, 5, rangeVO.getTableName());
            treeBasedTable.put(rowCount, 6, rangeVO.getIndexName());
            treeBasedTable.put(rowCount, 7, rangeVO.getStartKey() + " - " + rangeVO.getEndKey());

            rowCount++;

        }

        SortedSet<Integer> rowKeys = treeBasedTable.rowKeySet();
        int rowKeySize = rowKeys.size();

        String[][] data = new String[rowKeySize][8];

        for (Integer rowKey : rowKeys) {
            SortedMap<Integer, String> row = treeBasedTable.row(rowKey);

            for (Integer columnKey : row.keySet()) {
                data[rowKey][columnKey] = row.get(columnKey);
            }
        }

        TableBuilder tableBuilder = new TableBuilder(new ArrayTableModel(data));
        return tableBuilder.addFullBorder(BorderStyle.fancy_heavy).build();

    }
}
