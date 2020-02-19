package io.crdb.shell;

public class StatementOptions extends AbstractOptions {

    private final boolean verbose;
    private final String applicationName;
    private final boolean excludeDDL;
    private final boolean hasSpanAll;


    public StatementOptions(boolean verbose, String applicationName, boolean excludeDDL, boolean hasSpanAll) {
        this.verbose = verbose;
        this.applicationName = applicationName;
        this.excludeDDL = excludeDDL;
        this.hasSpanAll = hasSpanAll;
    }

    public boolean isVerbose() {
        return verbose;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public boolean isExcludeDDL() {
        return excludeDDL;
    }

    public boolean isHasSpanAll() {
        return hasSpanAll;
    }

    @Override
    void print(ShellHelper shellHelper) {

    }

    @Override
    boolean validate(ShellHelper shellHelper) {
        return false;
    }
}
