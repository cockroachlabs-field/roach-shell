package io.crdb.shell;

public abstract class AbstractOptions {

    abstract void print(ShellHelper shellHelper);
    abstract boolean validate(ShellHelper shellHelper);


}
