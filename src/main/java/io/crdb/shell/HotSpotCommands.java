package io.crdb.shell;

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

@ShellComponent
public class HotSpotCommands {

    private final HotSpotService service;
    private final ShellHelper shellHelper;

    public HotSpotCommands(HotSpotService service, ShellHelper shellHelper) {
        this.service = service;
        this.shellHelper = shellHelper;
    }

    /*
    final String host = environment.getRequiredProperty("crdb.host");
        final int port = environment.getProperty("crdb.port", Integer.class, 26257);
        final String database = environment.getProperty("crdb.database", "system");
        final String username = environment.getProperty("crdb.username", "root");
        final String password = environment.getProperty("crdb.password");
        final String sslMode = environment.getProperty("crdb.ssl.mode", "disable");
        final String crtPath = environment.getProperty("crdb.ssl.crt");
        final String keyPath = environment.getProperty("crdb.ssl.key");
        final String httpScheme = environment.getProperty("crdb.http.scheme");
        final String httpUsername = environment.getProperty("crdb.http.username");
        final String httpPassword = environment.getProperty("crdb.http.password");
        final int httpPort = environment.getProperty("crdb.http.port", Integer.class, 8080);

        final boolean secure = environment.getProperty("crdb.secure.enabled", Boolean.class, Boolean.FALSE);
        final int maxHotRanges = environment.getProperty("crdb.hotranges.max", Integer.class, 10);


        String httpHost = environment.getProperty("crdb.http.host");
     */

    @ShellMethod("Find range Hot Spots")
    public void hotspots(@ShellOption(help = "hostname of crdb node") String host,
                         @ShellOption(help = "", defaultValue = "26257") int port,
                         @ShellOption(help = "", defaultValue = "system") String database,
                         @ShellOption(help = "", defaultValue = "root") String username,
                         @ShellOption(help = "", defaultValue = "") String password,
                         @ShellOption(help = "", defaultValue = "disable") String sslMode,
                         @ShellOption(help = "", defaultValue = "") String sslCrtPath,
                         @ShellOption(help = "", defaultValue = "") String sslKeyPath,
                         @ShellOption(help = "", defaultValue = "http") String httpScheme,
                         @ShellOption(help = "", defaultValue = "") String httpUsername,
                         @ShellOption(help = "", defaultValue = "") String httpPassword,
                         @ShellOption(help = "", defaultValue = "") String httpHost,
                         @ShellOption(help = "", defaultValue = "8080") int httpPort,
                         @ShellOption(help = "", defaultValue = "false") boolean sslEnabled,
                         @ShellOption(help = "", defaultValue = "10") int maxRanges) {

        HotSpotOptions hotSpotOptions = new HotSpotOptions();
        hotSpotOptions.setHost(host);
        hotSpotOptions.setPort(port);
        hotSpotOptions.setDatabase(database);
        hotSpotOptions.setUsername(username);

        if (!password.isBlank()) {
            hotSpotOptions.setPassword(password);
        }

        hotSpotOptions.setSslMode(sslMode);

        if (!sslCrtPath.isBlank()) {
            hotSpotOptions.setSslCrtPath(sslCrtPath);
        }

        if (!sslKeyPath.isBlank()) {
            hotSpotOptions.setSslKeyPath(sslKeyPath);
        }

        if (sslEnabled) {
            httpScheme = "https";
        }

        hotSpotOptions.setHttpScheme(httpScheme);

        if (!httpUsername.isBlank()) {
            hotSpotOptions.setHttpUsername(httpUsername);
        }

        if (!httpPassword.isBlank()) {
            hotSpotOptions.setHttpPassword(httpPassword);
        }

        if (httpHost.isBlank()) {
            httpHost = host;
        }


        hotSpotOptions.setHttpHost(httpHost);
        hotSpotOptions.setHttpPort(httpPort);
        hotSpotOptions.setSslEnabled(sslEnabled);
        hotSpotOptions.setMaxHotRanges(maxRanges);

        shellHelper.print(service.getHotSpots(hotSpotOptions).render(120));
    }
}
