package io.crdb.shell;

public class HotSpotOptions extends AbstractOptions {

    private final boolean verbose;
    private final int maxHotRanges;

    public HotSpotOptions(boolean verbose, int maxHotRanges) {
        this.verbose = verbose;
        this.maxHotRanges = maxHotRanges;
    }

    public boolean isVerbose() {
        return verbose;
    }

    public int getMaxHotRanges() {
        return maxHotRanges;
    }

    public void print(ShellHelper shellHelper) {
        shellHelper.print("");
        shellHelper.printInfo("---------------------------------------------");
        shellHelper.printInfo("The following configuration parameters will be used:");
        shellHelper.print("\t" + "max-ranges" + ": " + maxHotRanges);
        shellHelper.print("\t" + "verbose" + ": " + verbose);
        shellHelper.printInfo("---------------------------------------------");
        shellHelper.print("");
    }

    @Override
    boolean validate(ShellHelper shellHelper) {
        return true;
    }
}
