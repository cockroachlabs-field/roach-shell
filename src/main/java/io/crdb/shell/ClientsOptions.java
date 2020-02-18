package io.crdb.shell;

public class ClientsOptions extends AbstractOptions {

    private final boolean verbose;


    public ClientsOptions(boolean verbose) {
        this.verbose = verbose;
    }

    public boolean isVerbose() {
        return verbose;
    }


    public void print(ShellHelper shellHelper) {
        shellHelper.print("");
        shellHelper.print("---------------------------------------------");
        shellHelper.print("The following configuration parameters will be used:");
        shellHelper.print("\t" + "verbose" + ": " + verbose);
        shellHelper.print("---------------------------------------------");
        shellHelper.print("");
    }

    @Override
    boolean validate(ShellHelper shellHelper) {
        return true;
    }
}
