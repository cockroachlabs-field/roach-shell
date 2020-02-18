package io.crdb.shell;

public class ConnectionOptions extends AbstractOptions {

    private final String host;
    private final int port;
    private final String database;
    private final String username;
    private final String password;
    private final boolean sslEnabled;
    private final String sslMode;
    private final String sslCrtPath;
    private final String sslKeyPath;
    private final String httpScheme;
    private final String httpUsername;
    private final String httpPassword;
    private final String httpHost;
    private final int httpPort;

    public ConnectionOptions(String host, int port, String database, String username, String password, boolean sslEnabled, String sslMode, String sslCrtPath, String sslKeyPath, String httpScheme, String httpUsername, String httpPassword, String httpHost, int httpPort) {
        this.host = host;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;
        this.sslEnabled = sslEnabled;
        this.sslMode = sslMode;
        this.sslCrtPath = sslCrtPath;
        this.sslKeyPath = sslKeyPath;
        this.httpScheme = httpScheme;
        this.httpUsername = httpUsername;
        this.httpPassword = httpPassword;
        this.httpPort = httpPort;

        if (httpHost == null || httpHost.isBlank()) {
            this.httpHost = host;
        } else {
            this.httpHost = httpHost;
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

    public String getSslCrtPath() {
        return sslCrtPath;
    }

    public String getSslKeyPath() {
        return sslKeyPath;
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
        shellHelper.printInfo("---------------------------------------------");
        shellHelper.printInfo("The following configuration parameters will be used:");
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
        shellHelper.print("\t" + "ssl-crt-path" + ": " + sslCrtPath);
        shellHelper.print("\t" + "ssl-key-path" + ": " + sslKeyPath);
        shellHelper.print("\t" + "http-scheme" + ": " + httpScheme);
        shellHelper.print("\t" + "http-host" + ": " + httpHost);
        shellHelper.print("\t" + "http-port" + ": " + httpPort);
        shellHelper.print("\t" + "http-username" + ": " + httpUsername);

        if (httpPassword != null && !httpPassword.isBlank()) {
            shellHelper.print("\t" + "http-password" + ": (password provided but not shown)");
        } else {
            shellHelper.print("\t" + "http-password" + ": (password is null or blank)");
        }

        shellHelper.printInfo("---------------------------------------------");
        shellHelper.print("");
    }

    @Override
    boolean validate(ShellHelper shellHelper) {

        if (sslEnabled) {

            if (sslMode == null || sslMode.equals("disable")) {
                shellHelper.printError("SSL is enabled but \"ssl-mode\" is set to \"disable\".  Please provide a valid \"ssl-mode\" or set \"ssl-enabled\" to \"false\".");
                return false;
            }

            if (sslCrtPath == null || sslCrtPath.isBlank()) {
                shellHelper.printError("SSL is enabled but \"ssl-crt-path\" is empty.  Please provide a valid \"ssl-crt-path\".");
                return false;
            }

            if (sslKeyPath == null || sslKeyPath.isBlank()) {
                shellHelper.printError("SSL is enabled but \"ssl-key-path\" is empty.  Please provide a valid \"ssl-key-path\".");
                return false;
            }

        }

        return true;

    }
}
