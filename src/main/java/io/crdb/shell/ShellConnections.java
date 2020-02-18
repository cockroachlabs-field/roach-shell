package io.crdb.shell;

import org.springframework.http.HttpHeaders;

import javax.sql.DataSource;

public class ShellConnections {

    private static final String COOKIE_KEY = "Cookie";

    private final ConnectionOptions connectionOptions;
    private final DataSource dataSource;

    private final HttpHeaders headers = new HttpHeaders();

    public ShellConnections(ConnectionOptions connectionOptions, DataSource dataSource, String cookie) {
        this.connectionOptions = connectionOptions;
        this.dataSource = dataSource;

        if (cookie != null && !headers.containsKey(COOKIE_KEY)) {
            headers.add(COOKIE_KEY, cookie);
        }
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    public ConnectionOptions getConnectionOptions() {
        return connectionOptions;
    }

}
