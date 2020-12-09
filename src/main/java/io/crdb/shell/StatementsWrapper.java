package io.crdb.shell;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class StatementsWrapper {

    @JsonProperty
    private List<Statement> statements;

    public List<Statement> getStatements() {
        return statements;
    }

    public void setStatements(List<Statement> statements) {
        this.statements = statements;
    }

    @Override
    public String toString() {
        return "StatementsWrapper{" +
               "statements=" + statements +
               '}';
    }
}
