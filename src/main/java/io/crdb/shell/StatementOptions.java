package io.crdb.shell;

public class StatementOptions extends AbstractOptions {

    private final boolean verbose;
    private final String applicationName;
    private final Boolean excludeDDL;
    private final Boolean excludeInternal;
    private final Boolean hasSpanAll;
    private final Boolean distOnly;


    public StatementOptions(boolean verbose, String applicationName, Boolean excludeDDL, Boolean excludeInternal, Boolean hasSpanAll, Boolean distOnly) {
        this.verbose = verbose;
        this.applicationName = applicationName;
        this.excludeDDL = excludeDDL;
        this.excludeInternal = excludeInternal;
        this.hasSpanAll = hasSpanAll;
        this.distOnly = distOnly;
    }

    public boolean isVerbose() {
        return verbose;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public Boolean getExcludeDDL() {
        return excludeDDL;
    }

    public Boolean getExcludeInternal() {
        return excludeInternal;
    }

    public Boolean getHasSpanAll() {
        return hasSpanAll;
    }

    public Boolean getDistOnly() {
        return distOnly;
    }

    @Override
    void print(ShellHelper shellHelper) {
        shellHelper.print("");
        shellHelper.print("---------------------------------------------");
        shellHelper.print("The following configuration parameters will be used:");
        shellHelper.print("\t" + "application-name" + ": " + applicationName);
        shellHelper.print("\t" + "exclude-ddl" + ": " + excludeDDL);
        shellHelper.print("\t" + "exclude-internal" + ": " + excludeInternal);
        shellHelper.print("\t" + "dist-only" + ": " + distOnly);
        shellHelper.print("\t" + "has-span-all" + ": " + hasSpanAll);
        shellHelper.print("\t" + "verbose" + ": " + verbose);
        shellHelper.print("---------------------------------------------");
        shellHelper.print("");
    }

    @Override
    boolean validate(ShellHelper shellHelper) {
        return true;
    }


}
