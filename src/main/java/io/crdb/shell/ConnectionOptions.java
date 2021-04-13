package io.crdb.shell;

import org.springframework.util.StringUtils;

public class ConnectionOptions extends AbstractOptions {

    private final String host;
    private final int port;
    private final String database;
    private final String username;
    private final String password;
    private final boolean sslEnabled;
    private final String sslMode;
    private final String sslRootCrtPath;
    private final String sslClientCrtPath;
    private final String sslClientKeyPath;
    private final String httpScheme;
    private final String httpUsername;
    private final String httpPassword;
    private final String httpHost;
    private final int httpPort;

    public ConnectionOptions(String host, int port, String database, String username, String password, boolean sslEnabled, String sslMode, String sslRootCrtPath, String sslClientCrtPath, String sslClientKeyPath, String httpUsername, String httpPassword, String httpHost, int httpPort) {
        this.host = host;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;
        this.sslEnabled = sslEnabled;
        this.sslMode = sslMode;
        this.sslRootCrtPath = sslRootCrtPath;
        this.sslClientCrtPath = sslClientCrtPath;
        this.sslClientKeyPath = sslClientKeyPath;

        this.httpPort = httpPort;

        if (!StringUtils.hasText(httpHost)) {
            this.httpHost = host;
        } else {
            this.httpHost = httpHost;
        }

        if (sslEnabled) {
            this.httpScheme = "https";
        } else {
            this.httpScheme = "http";
        }

        if (!StringUtils.hasText(httpUsername)) {
            this.httpUsername = username;
        } else {
            this.httpUsername = httpUsername;
        }

        if (!StringUtils.hasText(httpPassword)) {
            this.httpPassword = password;
        } else {
            this.httpPassword = httpPassword;
        }

    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getDatabase() {
        return database;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public boolean isSslEnabled() {
        return sslEnabled;
    }

    public String getSslMode() {
        return sslMode;
    }

    public String getSslClientCrtPath() {
        return sslClientCrtPath;
    }

    public String getSslClientKeyPath() {
        return sslClientKeyPath;
    }

    public String getSslRootCrtPath() {
        return sslRootCrtPath;
    }

    public String getHttpScheme() {
        return httpScheme;
    }

    public String getHttpUsername() {
        return httpUsername;
    }

    public String getHttpPassword() {
        return httpPassword;
    }

    public String getHttpHost() {
        return httpHost;
    }

    public int getHttpPort() {
        return httpPort;
    }

    public void print(ShellHelper shellHelper) {
        shellHelper.print("");
        shellHelper.print("---------------------------------------------");
        shellHelper.print("The following configuration parameters will be used:");
        shellHelper.print("\t" + "host" + ": " + host);
        shellHelper.print("\t" + "port" + ": " + port);
        shellHelper.print("\t" + "database" + ": " + database);
        shellHelper.print("\t" + "username" + ": " + username);

        if (password != null && !password.isBlank()) {
            shellHelper.print("\t" + "password" + ": (password provided but not shown)");
        } else {
            shellHelper.print("\t" + "password" + ": (password is null or blank)");
        }

        shellHelper.print("\t" + "ssl-enabled" + ": " + sslEnabled);
        shellHelper.print("\t" + "ssl-mode" + ": " + sslMode);
        shellHelper.print("\t" + "ssl-root-crt-path" + ": " + sslRootCrtPath);
        shellHelper.print("\t" + "ssl-client-crt-path" + ": " + sslClientCrtPath);
        shellHelper.print("\t" + "ssl-client-key-path" + ": " + sslClientKeyPath);
        shellHelper.print("\t" + "http-scheme" + ": " + httpScheme);
        shellHelper.print("\t" + "http-host" + ": " + httpHost);
        shellHelper.print("\t" + "http-port" + ": " + httpPort);
        shellHelper.print("\t" + "http-username" + ": " + httpUsername);

        if (httpPassword != null && !httpPassword.isBlank()) {
            shellHelper.print("\t" + "http-password" + ": (password provided but not shown)");
        } else {
            shellHelper.print("\t" + "http-password" + ": (password is null or blank)");
        }

        shellHelper.print("---------------------------------------------");
        shellHelper.print("");
    }

    @Override
    boolean validate(ShellHelper shellHelper) {

        if (sslEnabled) {

            if (sslMode == null || sslMode.equals("disable")) {
                shellHelper.printError("SSL is enabled but \"ssl-mode\" is set to \"disable\".  Please provide a valid \"ssl-mode\" or set \"ssl-enabled\" to \"false\".");
                return false;
            }

        }

        return true;

    }
}
