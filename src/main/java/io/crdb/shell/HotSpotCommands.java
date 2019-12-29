package io.crdb.shell;

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

@ShellComponent
public class HotSpotCommands {

    private final HotSpotService service;
    private final ShellHelper shellHelper;

    public HotSpotCommands(HotSpotService service, ShellHelper shellHelper) {
        this.service = service;
        this.shellHelper = shellHelper;
    }

    @ShellMethod("Find cluster Hot Spots")
    public void hotspots() {
        shellHelper.print(service.getHotSpots().render(80));
    }
}
