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

    @ShellMethod("Find range Hot Spots")
    public void hotspots(@ShellOption(value = {"--host", "-h"}, help = "hostname of crdb node") String host,
                         @ShellOption(value = {"--port", "-p"}, help = "", defaultValue = "26257") int port,
                         @ShellOption(value = {"--database", "-d"}, help = "", defaultValue = "system") String database,
                         @ShellOption(value = {"--username", "-u"}, help = "", defaultValue = "root") String username,
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
                         @ShellOption(value = {"--maxRanges", "-m"}, help = "", defaultValue = "10") int maxRanges) {

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

        if (sslEnabled) {

            hotSpotOptions.setSslMode(sslMode);

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

        shellHelper.print(service.getHotSpots(hotSpotOptions).render(120));
    }
}
