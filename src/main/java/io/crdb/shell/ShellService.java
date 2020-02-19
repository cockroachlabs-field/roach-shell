package io.crdb.shell;

import com.google.common.collect.Ordering;
import com.google.common.collect.TreeBasedTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.shell.table.ArrayTableModel;
import org.springframework.shell.table.BorderStyle;
import org.springframework.shell.table.Table;
import org.springframework.shell.table.TableBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Service
public class ShellService {

    private static final Logger log = LoggerFactory.getLogger(ShellApplication.class);

    private final RestTemplate restTemplate;
    private final ShellHelper shellHelper;

    public ShellService(RestTemplate restTemplate, ShellHelper shellHelper) {
        this.restTemplate = restTemplate;
        this.shellHelper = shellHelper;
    }

    public Table getHotSpots(HotSpotOptions options, ShellConnections connections) {

        final int maxHotRanges = options.getMaxHotRanges();
        final boolean verbose = options.isVerbose();

        final ConnectionOptions connectionOptions = connections.getConnectionOptions();

        final List<Node> nodes = getNodes(connectionOptions.getHttpPort(), connectionOptions.getHttpScheme(), connectionOptions.getHttpHost(), connections.getHeaders());

        final List<HotRangeVO> hotList = new ArrayList<>();

        for (Node node : nodes) {

            shellHelper.print("Found Node with id [" + node.getNodeId() + "], address [" + node.getAddress() + "] and build [" + node.getBuild() + "].");

            List<Store> stores = getHotRangesForNode(connectionOptions.getHttpPort(), connectionOptions.getHttpScheme(), connectionOptions.getHttpHost(), connections.getHeaders(), node, verbose);

            for (Store store : stores) {
                for (HotRange range : store.getHotRanges()) {
                    hotList.add(new HotRangeVO(node.getNodeId(), node.getAddress(), store.getStoreId(), range.getRangeId(), range.getStartKey(), range.getEndKey(), range.getQueriesPerSecond()));
                }
            }
        }

        List<HotRangeVO> hotRangeVOS = Ordering.natural().onResultOf(new CompareQPSFunction()).greatestOf(hotList, maxHotRanges);

        TreeBasedTable<Integer, Integer, String> treeBasedTable = TreeBasedTable.create();

        treeBasedTable.put(0, 0, "Rank");
        treeBasedTable.put(0, 1, "QPS");
        treeBasedTable.put(0, 2, "Node ID/Address");
        treeBasedTable.put(0, 3, "Store ID");
        treeBasedTable.put(0, 4, "Database");
        treeBasedTable.put(0, 5, "Table");
        treeBasedTable.put(0, 6, "Index");
        treeBasedTable.put(0, 7, "Range");

        int rowCount = 1;


        for (HotRangeVO vo : hotRangeVOS) {

            DataSource dataSource = connections.getDataSource();

            String sql = "select * from crdb_internal.ranges_no_leases where range_id = ?";

            try (Connection connection = dataSource.getConnection();
                 PreparedStatement statement = connection.prepareStatement(sql)
            ) {

                statement.setInt(1, vo.getRangeId());

                try (ResultSet resultSet = statement.executeQuery()) {

                    if (resultSet.next()) {

                        RangeVO rangeVO = new RangeVO(resultSet.getInt("range_id"),
                                resultSet.getString("start_pretty"),
                                resultSet.getString("end_pretty"),
                                resultSet.getString("database_name"),
                                resultSet.getString("table_name"),
                                resultSet.getString("index_name"));

                        Assert.notNull(rangeVO, "unable to find range for id " + vo.getRangeId());

                        treeBasedTable.put(rowCount, 0, Integer.toString(rowCount));
                        treeBasedTable.put(rowCount, 1, Float.toString(vo.getQueriesPerSecond()));
                        treeBasedTable.put(rowCount, 2, vo.getNodeId() + "/" + vo.getNodeAddress());
                        treeBasedTable.put(rowCount, 3, Integer.toString(vo.getStoreId()));
                        treeBasedTable.put(rowCount, 4, rangeVO.getDatabaseName());
                        treeBasedTable.put(rowCount, 5, rangeVO.getTableName());
                        treeBasedTable.put(rowCount, 6, rangeVO.getIndexName());
                        treeBasedTable.put(rowCount, 7, rangeVO.getStartKey() + " - " + rangeVO.getEndKey());

                        rowCount++;
                    }
                }

            } catch (SQLException e) {
                shellHelper.printError("Unable to load Ranges details from \"crdb_internal.ranges_no_leases\" for Range " + vo.getRangeId() + ".");

                printException(verbose, e);

                log.debug(e.getMessage(), e);
            }

        }

        return buildTable(treeBasedTable, 8);

    }

    private Table buildTable(TreeBasedTable<Integer, Integer, String> treeBasedTable, int columnCount) {
        SortedSet<Integer> rowKeys = treeBasedTable.rowKeySet();
        int rowKeySize = rowKeys.size();

        String[][] data = new String[rowKeySize][columnCount];

        for (Integer rowKey : rowKeys) {
            SortedMap<Integer, String> row = treeBasedTable.row(rowKey);

            for (Integer columnKey : row.keySet()) {
                data[rowKey][columnKey] = row.get(columnKey);
            }
        }

        TableBuilder tableBuilder = new TableBuilder(new ArrayTableModel(data));
        return tableBuilder.addFullBorder(BorderStyle.fancy_heavy).build();
    }

    private void printException(boolean verbose, Exception e) {
        if (verbose) {
            shellHelper.printError("the following error occurred: " + e.getMessage());

            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);

            shellHelper.printError("with stacktrace: " + sw.toString());
        }
    }



    private List<Store> getHotRangesForNode(int httpPort, String httpScheme, String httpHost, HttpHeaders headers, Node node, boolean verbose) {

        try {
            final URI hotRangeUri = UriComponentsBuilder
                    .fromUriString(String.format("%s://%s:%s/_status/hotranges", httpScheme, httpHost, httpPort))
                    .queryParam("node_id", node.getNodeId())
                    .build()
                    .toUri();

            final RequestEntity<Void> requestEntity = RequestEntity.get(hotRangeUri).headers(headers).accept(MediaType.APPLICATION_JSON).build();

            final ResponseEntity<HotRanges> responseEntity = restTemplate.exchange(requestEntity, HotRanges.class);

            Assert.notNull(responseEntity, "getHotRangesForNode() ResponseEntity is null");

            final HotRanges body = responseEntity.getBody();

            Assert.notNull(body, "getHotRangesForNode() body is null");

            return body.getStores();
        } catch (Exception e) {
            shellHelper.printError("Unable to load Hot Ranges for Node " + node.getNodeId() + ".  Node may be down.");

            printException(verbose, e);

            log.debug(e.getMessage(), e);
        }

        return new ArrayList<>();
    }

    private List<Node> getNodes(int httpPort, String httpScheme, String httpHost, HttpHeaders headers) {

        final URI nodesUri = UriComponentsBuilder
                .fromUriString(String.format("%s://%s:%s/_status/nodes", httpScheme, httpHost, httpPort))
                .build()
                .toUri();

        final RequestEntity<Void> requestEntity = RequestEntity.get(nodesUri).headers(headers).accept(MediaType.APPLICATION_JSON).build();

        final ResponseEntity<NodeStatusWrapper> responseEntity = restTemplate.exchange(requestEntity, NodeStatusWrapper.class);

        Assert.notNull(responseEntity, "getNodes() ResponseEntity is null");

        final NodeStatusWrapper body = responseEntity.getBody();

        Assert.notNull(body, "getNodes() body is null");

        return body.getNodes();

    }


    public Table getClients(ClientsOptions options, ShellConnections connections) {

        DataSource dataSource = connections.getDataSource();

        TreeBasedTable<Integer, Integer, String> treeBasedTable = TreeBasedTable.create();

        treeBasedTable.put(0, 0, "Username");
        treeBasedTable.put(0, 1, "Client Address");
        treeBasedTable.put(0, 2, "Application Name");
        treeBasedTable.put(0, 3, "Session Start");
        treeBasedTable.put(0, 4, "Oldest Query Start");
        treeBasedTable.put(0, 5, "Last Active Query");

        String sql = "select * from crdb_internal.cluster_sessions order by session_start desc";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery();
        ) {

            int rowCount = 1;
            while (resultSet.next()) {

                String userName = resultSet.getString("user_name");
                String clientAddress = resultSet.getString("client_address");
                String applicationName = resultSet.getString("application_name");
                String sessionStart = resultSet.getString("session_start");
                String oldestQueryStart = resultSet.getString("oldest_query_start");
                String lastActiveQuery = resultSet.getString("last_active_query");

                treeBasedTable.put(rowCount, 0, userName != null ? userName : "");
                treeBasedTable.put(rowCount, 1, clientAddress != null ? clientAddress : "");
                treeBasedTable.put(rowCount, 2, applicationName != null ? applicationName : "");
                treeBasedTable.put(rowCount, 3, sessionStart != null ? sessionStart : "");
                treeBasedTable.put(rowCount, 4, oldestQueryStart != null ? oldestQueryStart : "");
                treeBasedTable.put(rowCount, 5, lastActiveQuery != null ? lastActiveQuery : "");

                rowCount++;

            }
        } catch (SQLException e) {
            shellHelper.printError("Unable to get clients from \"crdb_internal.cluster_sessions\".");

            printException(options.isVerbose(), e);

            log.debug(e.getMessage(), e);
        }

        return buildTable(treeBasedTable, 6);
    }

    public Table getStatements(StatementOptions options, ShellConnections connections) {

        List<Statement> statements = getAllStatements(connections);

        Map<String, Statement> filtered = new HashMap<>();

        boolean filterByApp = options.getApplicationName() != null && !options.getApplicationName().isEmpty();

        for (Statement statement : statements) {

            StatementKeyData keyData = statement.getKey().getKeyData();

            String query = keyData.getQuery().toUpperCase();
            String appName = keyData.getApp().toUpperCase();
            String plan = statement.getStats().getSensitiveInfo().getMostRecentPlanDescription().toString().toUpperCase();

            if (filtered.containsKey(query)) {
                continue;
            }

            boolean include = true;

            if (options.getDistOnly() != null && options.getDistOnly()) {
                if (!keyData.isDistSQL())  {
                    include = false;
                }
            }

            if (include && filterByApp) {
                if (!appName.equals(options.getApplicationName().toUpperCase())) {
                    include = false;
                }
            }

            if (include && (options.getExcludeInternal() != null && options.getExcludeInternal())) {
                if (appName.contains("INTERNAL" )) {
                    include = false;
                }
            }

            if (include && (options.getExcludeDDL() != null && options.getExcludeDDL())) {
                if (query.startsWith("CREATE") || query.startsWith("ALTER") || query.startsWith("DROP") || query.startsWith("SET")) {
                    include = false;
                }
            }

            if (include && (options.getHasSpanAll() != null && options.getHasSpanAll())) {
                if (!plan.contains("\"KEY\":\"SPANS\",\"VALUE\":\"ALL\"")) {
                    include = false;
                }
            }

            if (include) {
                filtered.put(query, statement);
            } else {
                log.debug("this statement was excluded [{}]", statement.toString());
            }
        }

        shellHelper.printSuccess(String.format("Returned %d total statements.  Showing %d after applying filters.", statements.size(), filtered.size()));

        TreeBasedTable<Integer, Integer, String> treeBasedTable = TreeBasedTable.create();

        treeBasedTable.put(0, 0, "Node");
        treeBasedTable.put(0, 1, "Application Name");
        treeBasedTable.put(0, 2, "Count");
        treeBasedTable.put(0, 3, "Last Plan Timestamp");
        treeBasedTable.put(0, 4, "Statement");

        int rowCount = 1;
        for (Statement statement : filtered.values()) {
            String node = Integer.toString(statement.getKey().getNodeId());
            String appName = statement.getKey().getKeyData().getApp();
            String count = Integer.toString(statement.getStats().getCount());
            String timestamp = statement.getStats().getSensitiveInfo().getMostRecentPlanTimestamp();
            String query = statement.getKey().getKeyData().getQuery();

            treeBasedTable.put(rowCount, 0, node != null ? node : "");
            treeBasedTable.put(rowCount, 1, appName != null ? appName : "");
            treeBasedTable.put(rowCount, 2, count != null ? count : "");
            treeBasedTable.put(rowCount, 3, timestamp != null ? timestamp : "");
            treeBasedTable.put(rowCount, 4, query != null ? query : "");

            rowCount++;
        }


        return buildTable(treeBasedTable, 5);
    }

    private List<Statement> getAllStatements(ShellConnections connections) {
        ConnectionOptions connectionOptions = connections.getConnectionOptions();

        final URI uri = UriComponentsBuilder
                .fromUriString(String.format("%s://%s:%s/_status/statements", connectionOptions.getHttpScheme(), connectionOptions.getHttpHost(), connectionOptions.getHttpPort()))
                .build()
                .toUri();

        final RequestEntity<Void> requestEntity = RequestEntity.get(uri).headers(connections.getHeaders()).accept(MediaType.APPLICATION_JSON).build();

        final ResponseEntity<StatementsWrapper> responseEntity = restTemplate.exchange(requestEntity, StatementsWrapper.class);

        Assert.notNull(responseEntity, "getStatements() ResponseEntity is null");

        final StatementsWrapper body = responseEntity.getBody();

        Assert.notNull(body, "getStatements() body is null");

        return body.getStatements();
    }
}
