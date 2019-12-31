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

    @ShellMethod("Find range hot spots in a CockroachDB (CRDB) cluster.")
    public void hotspots(@ShellOption(value = {"--host", "-h"}, help = "hostname of a CRDB node") String host,
                         @ShellOption(value = {"--port", "-p"}, help = "port of a CRDB node", defaultValue = "26257") int port,
                         @ShellOption(value = {"--database", "-d"}, help = "CRDB database name", defaultValue = "system") String database,
                         @ShellOption(value = {"--username", "-u"}, help = "username used to connect to database", defaultValue = "root") String username,
                         @ShellOption(help = "password used to connect to database", defaultValue = "") String password,
                         @ShellOption(help = "SSL mode for database connection", defaultValue = "disable") String sslMode,
                         @ShellOption(help = "is SSL enabled?", defaultValue = "false") boolean sslEnabled,
                         @ShellOption(help = "path to SSL Cert file when SSL is enabled", defaultValue = "") String sslCrtPath,
                         @ShellOption(help = "path to SSL Key file when SSL is enabled", defaultValue = "") String sslKeyPath,
                         @ShellOption(help = "HTTP scheme for Admin UI REST calls", defaultValue = "http") String httpScheme,
                         @ShellOption(help = "username used for Admin UI REST calls", defaultValue = "") String httpUsername,
                         @ShellOption(help = "password used for Admin UI REST calls", defaultValue = "") String httpPassword,
                         @ShellOption(help = "host used for Admin UI REST calls", defaultValue = "") String httpHost,
                         @ShellOption(help = "port used for Admin UI REST calls", defaultValue = "8080") int httpPort,
                         @ShellOption(value = {"--max-ranges", "-m"}, help = "max number of hot ranges returned", defaultValue = "10") int maxRanges) {

        // todo: print collected values
        // todo: print wanrings when combinations don't make sense

        HotSpotOptions hotSpotOptions = new HotSpotOptions();
        hotSpotOptions.setHost(host);
        hotSpotOptions.setPort(port);
        hotSpotOptions.setDatabase(database);
        hotSpotOptions.setUsername(username);

        if (!password.isBlank()) {
            hotSpotOptions.setPassword(password);
        }

        hotSpotOptions.setSslEnabled(sslEnabled);
        hotSpotOptions.setSslMode(sslMode);

        if (sslEnabled) {

            if (sslMode.equals("disable")) {
                shellHelper.printError("SSL is enabled but \"sslMode\" is set to \"disable\".  Please provide a valid \"sslMode\" or set \"sslEnabled\" to \"false\".");
            }

            if (sslCrtPath.isBlank()) {
                shellHelper.printError("SSL is enabled but \"sslCrtPath\" is empty.  Please provide a valid \"sslCrtPath\".");
            }

            hotSpotOptions.setSslCrtPath(sslCrtPath);

            if (sslKeyPath.isBlank()) {
                shellHelper.printError("SSL is enabled but \"sslKeyPath\" is empty.  Please provide a valid \"sslKeyPath\".");
            }

            hotSpotOptions.setSslKeyPath(sslKeyPath);

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
        hotSpotOptions.setMaxHotRanges(maxRanges);

        hotSpotOptions.print(shellHelper);

        shellHelper.print(service.getHotSpots(hotSpotOptions).render(200));
    }
}
