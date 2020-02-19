package io.crdb.shell;

import com.fasterxml.jackson.annotation.JsonProperty;

public class StatementKeyData {

    @JsonProperty
    private String query;

    @JsonProperty
    private String app;

    @JsonProperty
    private boolean distSQL;

    @JsonProperty
    private boolean failed;

    @JsonProperty
    private boolean opt;

    @JsonProperty
    private boolean implicitTxn;

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public boolean isDistSQL() {
        return distSQL;
    }

    public void setDistSQL(boolean distSQL) {
        this.distSQL = distSQL;
    }

    public boolean isFailed() {
        return failed;
    }

    public void setFailed(boolean failed) {
        this.failed = failed;
    }

    public boolean isOpt() {
        return opt;
    }

    public void setOpt(boolean opt) {
        this.opt = opt;
    }

    public boolean isImplicitTxn() {
        return implicitTxn;
    }

    public void setImplicitTxn(boolean implicitTxn) {
        this.implicitTxn = implicitTxn;
    }

    @Override
    public String toString() {
        return "StatementKeyData{" +
                "query='" + query + '\'' +
                ", app='" + app + '\'' +
                ", distSQL=" + distSQL +
                ", failed=" + failed +
                ", opt=" + opt +
                ", implicitTxn=" + implicitTxn +
                '}';
    }
}
