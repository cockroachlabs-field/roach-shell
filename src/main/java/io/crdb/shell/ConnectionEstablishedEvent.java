package io.crdb.shell;

import org.springframework.context.ApplicationEvent;

public class ConnectionEstablishedEvent extends ApplicationEvent {

    private final String connectionLabel;


    public ConnectionEstablishedEvent(Object source, String connectionLabel) {
        super(source);

        this.connectionLabel = connectionLabel;
    }

    public String getConnectionLabel() {
        return connectionLabel;
    }
}


