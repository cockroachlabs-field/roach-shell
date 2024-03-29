package io.crdb.shell;

import org.postgresql.ds.PGSimpleDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;
import org.springframework.shell.standard.ShellOption;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.annotation.PreDestroy;
import javax.sql.DataSource;
import java.net.URI;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

@ShellComponent
public class ShellCommands {

    private static final Logger log = LoggerFactory.getLogger(ShellCommands.class);

    private final ShellService service;
    private final ShellHelper shellHelper;
    private final RestTemplate restTemplate;


    private ShellConnections connections = null;


    public ShellCommands(ShellService service, ShellHelper shellHelper, RestTemplate restTemplate) {
        this.service = service;
        this.shellHelper = shellHelper;
        this.restTemplate = restTemplate;
    }

    @ShellMethod("Connect to a CockroachDB cluster.")
    public void connect(@ShellOption(value = {"--host", "-h"}, help = "hostname of a CRDB node") String host,
                        @ShellOption(value = {"--port", "-p"}, help = "port of a CRDB node", defaultValue = "26257") int port,
                        @ShellOption(value = {"--database", "-d"}, help = "CRDB database name", defaultValue = "system") String database,
                        @ShellOption(value = {"--username", "-u"}, help = "username used to connect to database", defaultValue = "root") String username,
                        @ShellOption(help = "password used to connect to database", defaultValue = ShellOption.NULL) String password,
                        @ShellOption(help = "SSL mode for database connection.  disable, allow, prefer, require, verify-ca or verify-full.", defaultValue = "disable") String sslMode,
                        @ShellOption(help = "is SSL enabled? true or false.", defaultValue = "false") boolean sslEnabled,
                        @ShellOption(help = "path to Root Cert file when SSL is enabled", defaultValue = ShellOption.NULL) String sslRootCrtPath,
                        @ShellOption(help = "path to SSL Client Cert file when SSL is enabled", defaultValue = ShellOption.NULL) String sslClientCrtPath,
                        @ShellOption(help = "path to SSL Client Key file when SSL is enabled", defaultValue = ShellOption.NULL) String sslClientKeyPath,
                        @ShellOption(help = "username used for Admin UI REST calls.  will use database username if no value provided.", defaultValue = ShellOption.NULL) String httpUsername,
                        @ShellOption(help = "password used for Admin UI REST calls.  will use database password if no value provided.", defaultValue = ShellOption.NULL) String httpPassword,
                        @ShellOption(help = "host used for Admin UI REST calls", defaultValue = ShellOption.NULL) String httpHost,
                        @ShellOption(help = "port used for Admin UI REST calls", defaultValue = "8080") int httpPort) {


        ConnectionOptions connectionOptions = new ConnectionOptions(host, port, database, username, password, sslEnabled, sslMode, sslRootCrtPath, sslClientCrtPath, sslClientKeyPath, httpUsername, httpPassword, httpHost, httpPort);

        if (!connectionOptions.validate(shellHelper)) {
            return;
        }

        if (connections != null) {
            shellHelper.printWarning("Connections already exist and will be disconnected.");
            disconnect();
        }

        connectionOptions.print(shellHelper);

        try {
            connections = new ShellConnections(connectionOptions, getDataSource(connectionOptions), loginRest(connectionOptions));
        } catch (SQLException e) {
            log.error(e.getMessage(), e);

            shellHelper.printError("Unable to establish connection to CockroachDB.");
        }

    }

    @ShellMethod("Disconnect from the CockroachDB cluster.")
    public void disconnect() {
        closeConnections();
    }

    @ShellMethod("Find range hot spots in the CockroachDB cluster.")
    public void hotspots(
            @ShellOption(value = {"--max-ranges", "-m"}, help = "max number of hot ranges returned", defaultValue = "10") int maxRanges,
            @ShellOption(value = {"--verbose", "-v"}, help = "include verbose output.  true or false.", defaultValue = "false") boolean verbose) {

        HotSpotOptions options = new HotSpotOptions(verbose, maxRanges);
        options.print(shellHelper);

        shellHelper.print(service.getHotSpots(options, connections).render(200));
    }

    @ShellMethod("List active client connections to the CockroachDB cluster.")
    public void clients(
            @ShellOption(value = {"--verbose", "-v"}, help = "include verbose output.  true or false.", defaultValue = "false") boolean verbose) {

        ClientsOptions options = new ClientsOptions(verbose);
        options.print(shellHelper);

        shellHelper.print(service.getClients(options, connections).render(200));
    }

    @ShellMethod("List recent statements against the CockroachDB cluster.")
    public void statements(
            @ShellOption(help = "include only dist sql statements.  true or false.", defaultValue = ShellOption.NULL) Boolean distOnly,
            @ShellOption(value = {"--exclude-ddl", "-xd"}, help = "exclude DDL statements.  true or false.", defaultValue = "true") Boolean excludeDDL,
            @ShellOption(value = {"--exclude-internal", "-xi"}, help = "exclude statements from CockroachDB internals.  true or false.", defaultValue = "true") Boolean excludeInternal,
            @ShellOption(help = "include statements with \"span = ALL\".  true or false.", defaultValue = ShellOption.NULL) Boolean hasSpanAll,
            @ShellOption(value = {"--verbose", "-v"}, help = "include verbose output.  true or false.", defaultValue = "false") boolean verbose,
            @ShellOption(value = {"--app", "-a"}, help = "only include statements from this application", defaultValue = ShellOption.NULL) String applicationName) {


        StatementOptions options = new StatementOptions(verbose, applicationName, excludeDDL, excludeInternal, hasSpanAll, distOnly);
        options.print(shellHelper);

        shellHelper.print(service.getStatements(options, connections).render(200));
    }

    @ShellMethodAvailability({"hotspots", "disconnect", "clients"})
    public Availability connectionAvailability() {
        return connections != null ? Availability.available() : Availability.unavailable("No connection has been established.  Please run 'connect' first.");
    }

    @PreDestroy
    public void cleanUp() {
        closeConnections();
    }

    private void closeConnections() {

        if (connections != null) {
            try {
                logoutRest(connections);
            } catch (Exception e) {
                log.error("unable to logout", e);
            }

            connections = null;

            log.info("Existing connections have been closed!");
        }

    }

    private DataSource getDataSource(ConnectionOptions connectionOptions) throws SQLException {
        PGSimpleDataSource ds = new PGSimpleDataSource();
        ds.setServerNames(new String[]{connectionOptions.getHost()});
        ds.setPortNumbers(new int[]{connectionOptions.getPort()});
        ds.setDatabaseName(connectionOptions.getDatabase());

        String username = connectionOptions.getUsername();

        if (username != null && !username.isBlank()) {
            ds.setUser(username);
        }

        String password = connectionOptions.getPassword();

        if (password != null && !password.isBlank()) {
            ds.setPassword(password);
        }

        ds.setSslMode(connectionOptions.getSslMode());
        ds.setSsl(connectionOptions.isSslEnabled());

        if (connectionOptions.isSslEnabled()) {
            if (StringUtils.hasText(connectionOptions.getSslRootCrtPath())) {
                ds.setSslRootCert(connectionOptions.getSslRootCrtPath());
            }

            if (StringUtils.hasText(connectionOptions.getSslClientCrtPath())) {
                ds.setSslCert(connectionOptions.getSslClientCrtPath());
            }

            if (StringUtils.hasText(connectionOptions.getSslClientKeyPath())) {
                ds.setSslKey(connectionOptions.getSslClientKeyPath());
            }
        }

        ds.setReWriteBatchedInserts(true);
        ds.setApplicationName("HotSpotDetector");

        try (Connection connection = ds.getConnection()) {

            DatabaseMetaData metaData = connection.getMetaData();

            shellHelper.printSuccess(String.format("Connection to CockroachDB successful.  URL is %s", metaData.getURL()));
        }

        return ds;
    }

    private String loginRest(ConnectionOptions connectionOptions) {

        if (connectionOptions.isSslEnabled()) {

            final URI uri = UriComponentsBuilder
                    .fromUriString(String.format("%s://%s:%s/login", connectionOptions.getHttpScheme(), connectionOptions.getHttpHost(), connectionOptions.getHttpPort()))
                    .build()
                    .toUri();

            final String body = String.format("{\"username\":\"%s\", \"password\":\"%s\"}", connectionOptions.getHttpUsername(), connectionOptions.getHttpPassword());

            final RequestEntity<String> requestEntity = RequestEntity.post(uri).contentType(MediaType.APPLICATION_JSON).body(body);

            final ResponseEntity<String> responseEntity = restTemplate.exchange(requestEntity, String.class);

            String cookie = responseEntity.getHeaders().getFirst(HttpHeaders.SET_COOKIE);

            Assert.hasText(cookie, "Secure Cookie is NULL.  This is likely a problem with the Login call.");

            shellHelper.printSuccess("Login successful for secure HTTP connection.");

            return cookie;
        }

        return null;
    }

    private void logoutRest(ShellConnections connections) {

        if (connections.getConnectionOptions().isSslEnabled()) {

            ConnectionOptions connectionOptions = connections.getConnectionOptions();

            final URI uri = UriComponentsBuilder
                    .fromUriString(String.format("%s://%s:%s/logout", connectionOptions.getHttpScheme(), connectionOptions.getHttpHost(), connectionOptions.getHttpPort()))
                    .build()
                    .toUri();

            final RequestEntity<Void> requestEntity = RequestEntity.get(uri).headers(connections.getHeaders()).build();

            restTemplate.exchange(requestEntity, String.class);

            shellHelper.printSuccess("Logout complete from secure HTTP connection.");
        }
    }

}
