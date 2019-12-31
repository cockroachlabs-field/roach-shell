package io.crdb.shell;

import com.google.common.collect.Ordering;
import com.google.common.collect.TreeBasedTable;
import org.postgresql.ds.PGSimpleDataSource;
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
import java.net.URI;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.SortedSet;

@Service
public class HotSpotService {

    private static final Logger log = LoggerFactory.getLogger(HotSpotDetectorApplication.class);

    private final RestTemplate restTemplate;
    private final ShellHelper shellHelper;

    public HotSpotService(RestTemplate restTemplate, ShellHelper shellHelper) {
        this.restTemplate = restTemplate;
        this.shellHelper = shellHelper;
    }

    public Table getHotSpots(HotSpotOptions options) {

        final String host = options.getHost();
        final int port = options.getPort();
        final String database = options.getDatabase();
        final String username = options.getUsername();
        final String password = options.getPassword();
        final boolean sslEnabled = options.isSslEnabled();
        final String sslMode = options.getSslMode();
        final String sslCrtPath = options.getSslCrtPath();
        final String sslKeyPath = options.getSslKeyPath();
        final String httpScheme = options.getHttpScheme();
        final String httpHost = options.getHttpHost();
        final int httpPort = options.getHttpPort();
        final String httpUsername = options.getHttpUsername();
        final String httpPassword = options.getHttpPassword();
        final int maxHotRanges = options.getMaxHotRanges();

        final HttpHeaders headers = new HttpHeaders();

        if (sslEnabled) {
            String loginCookie = login(httpPort, httpScheme, httpHost, httpUsername, httpPassword);

            headers.add("Cookie", loginCookie);
        }

        final List<Node> nodes = getNodes(httpPort, httpScheme, httpHost, headers);

        final List<HotRangeVO> hotList = new ArrayList<>();

        for (Node node : nodes) {

            shellHelper.print("Found Node with id [" + node.getNodeId() + "], address [" + node.getAddress() + "] and build [" + node.getBuild() + "].");

            List<Store> stores = getHotRangesForNode(httpPort, httpScheme, httpHost, headers, node);

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

        DataSource dataSource = getDataSource(host, port, database, username, password, sslEnabled, sslMode, sslCrtPath, sslKeyPath);

        for (HotRangeVO vo : hotRangeVOS) {

            try (Connection connection = dataSource.getConnection();
                 PreparedStatement statement = connection.prepareStatement("select * from crdb_internal.ranges_no_leases where range_id = ?")
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
                log.warn(e.getMessage(), e);
            }

        }


        if (sslEnabled) {
            logout(httpPort, httpScheme, httpHost, headers);
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

    private DataSource getDataSource(String host,
                                     int port,
                                     String database,
                                     String user,
                                     String password,
                                     boolean sslEnabled,
                                     String sslMode,
                                     String crtPath,
                                     String keyPath) {
        PGSimpleDataSource ds = new PGSimpleDataSource();
        ds.setServerName(host);
        ds.setPortNumber(port);
        ds.setDatabaseName(database);
        ds.setUser(user);
        if (password != null && !password.isBlank()) {
            ds.setPassword(password);
        }
        ds.setSslMode(sslMode);
        ds.setSsl(sslEnabled);

        if (sslEnabled) {
            ds.setSslCert(crtPath);
            ds.setSslKey(keyPath);
        }

        ds.setReWriteBatchedInserts(true);
        ds.setApplicationName("HotSpotDetector");

        return ds;
    }

    private List<Store> getHotRangesForNode(int httpPort, String httpScheme, String httpHost, HttpHeaders headers, Node node) {

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

    private String login(int httpPort, String httpScheme, String httpHost, String username, String password) {
        final URI uri = UriComponentsBuilder
                .fromUriString(String.format("%s://%s:%s/login", httpScheme, httpHost, httpPort))
                .build()
                .toUri();

        final String body = String.format("{\"username\":\"%s\", \"password\":\"%s\"}", username, password);

        final RequestEntity<String> requestEntity = RequestEntity.post(uri).contentType(MediaType.APPLICATION_JSON).body(body);

        final ResponseEntity<String> responseEntity = restTemplate.exchange(requestEntity, String.class);

        return responseEntity.getHeaders().getFirst(HttpHeaders.SET_COOKIE);
    }

    private void logout(int httpPort, String httpScheme, String httpHost, HttpHeaders headers) {
        final URI uri = UriComponentsBuilder
                .fromUriString(String.format("%s://%s:%s/logout", httpScheme, httpHost, httpPort))
                .build()
                .toUri();

        final RequestEntity<Void> requestEntity = RequestEntity.get(uri).headers(headers).build();

        restTemplate.exchange(requestEntity, String.class);
    }
}
