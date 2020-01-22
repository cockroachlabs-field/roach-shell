package io.crdb.shell;

public class HotSpotOptions {

    private String host;
    private int port;
    private String database;
    private String username;
    private String password;
    private boolean sslEnabled;
    private String sslMode;
    private String sslCrtPath;
    private String sslKeyPath;
    private String httpScheme;
    private String httpUsername;
    private String httpPassword;
    private String httpHost;
    private int httpPort;
    private boolean verbose;


    private int maxHotRanges;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSslMode() {
        return sslMode;
    }

    public void setSslMode(String sslMode) {
        this.sslMode = sslMode;
    }

    public String getSslCrtPath() {
        return sslCrtPath;
    }

    public void setSslCrtPath(String sslCrtPath) {
        this.sslCrtPath = sslCrtPath;
    }

    public String getSslKeyPath() {
        return sslKeyPath;
    }

    public void setSslKeyPath(String sslKeyPath) {
        this.sslKeyPath = sslKeyPath;
    }

    public String getHttpScheme() {
        return httpScheme;
    }

    public void setHttpScheme(String httpScheme) {
        this.httpScheme = httpScheme;
    }

    public String getHttpUsername() {
        return httpUsername;
    }

    public void setHttpUsername(String httpUsername) {
        this.httpUsername = httpUsername;
    }

    public String getHttpPassword() {
        return httpPassword;
    }

    public void setHttpPassword(String httpPassword) {
        this.httpPassword = httpPassword;
    }

    public int getHttpPort() {
        return httpPort;
    }

    public void setHttpPort(int httpPort) {
        this.httpPort = httpPort;
    }

    public boolean isSslEnabled() {
        return sslEnabled;
    }

    public void setSslEnabled(boolean sslEnabled) {
        this.sslEnabled = sslEnabled;
    }

    public int getMaxHotRanges() {
        return maxHotRanges;
    }

    public void setMaxHotRanges(int maxHotRanges) {
        this.maxHotRanges = maxHotRanges;
    }

    public String getHttpHost() {
        return httpHost;
    }

    public void setHttpHost(String httpHost) {
        this.httpHost = httpHost;
    }

    public boolean isVerbose() {
        return verbose;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
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

        shellHelper.print("\t" + "verbose" + ": " + verbose);
        shellHelper.printInfo("---------------------------------------------");
        shellHelper.print("");
    }
}
