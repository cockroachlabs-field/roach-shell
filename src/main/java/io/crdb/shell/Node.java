package io.crdb.shell;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.time.Instant;

@JsonDeserialize(using = NodeDeserializer.class)
public class Node {

    private final int nodeId;

    private final String build;

    private final Instant startedAt;

    private final long totalSystemMemory;

    private final int cpus;

    private final String address;

    public Node(int nodeId, String build, Instant startedAt, long totalSystemMemory, int cpus, String address) {
        this.nodeId = nodeId;
        this.build = build;
        this.startedAt = startedAt;
        this.totalSystemMemory = totalSystemMemory;
        this.cpus = cpus;
        this.address = address;
    }

    public int getNodeId() {
        return nodeId;
    }

    public String getBuild() {
        return build;
    }

    public Instant getStartedAt() {
        return startedAt;
    }

    public long getTotalSystemMemory() {
        return totalSystemMemory;
    }

    public int getCpus() {
        return cpus;
    }

    public String getAddress() {
        return address;
    }
}
