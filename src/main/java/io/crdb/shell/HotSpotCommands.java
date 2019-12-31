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
