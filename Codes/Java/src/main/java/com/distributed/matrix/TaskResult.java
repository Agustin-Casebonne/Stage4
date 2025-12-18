package com.distributed.matrix;

import java.io.Serializable;

public class TaskResult implements Serializable {
    private static final long serialVersionUID = 1L;
    public final long computeTimeNs;

    public TaskResult(long computeTimeNs) {
        this.computeTimeNs = computeTimeNs;
    }
}
