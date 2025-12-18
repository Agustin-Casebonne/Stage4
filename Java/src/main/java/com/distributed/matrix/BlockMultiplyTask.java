package com.distributed.matrix;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import java.io.Serializable;
import java.util.concurrent.Callable;

public class BlockMultiplyTask implements Callable<TaskResult>, Serializable {

    private static final long serialVersionUID = 1L;

    private final String aKey, bKey, cKey;
    private final String aMapName, bMapName, cMapName;

    public BlockMultiplyTask(
            String aMapName, String bMapName, String cMapName,
            String aKey, String bKey, String cKey) {
        this.aMapName = aMapName;
        this.bMapName = bMapName;
        this.cMapName = cMapName;
        this.aKey = aKey;
        this.bKey = bKey;
        this.cKey = cKey;
    }

    @Override
    public TaskResult call() {
        long computeStart = System.nanoTime();

        HazelcastInstance hz =
                Hazelcast.getAllHazelcastInstances().iterator().next();

        IMap<String, MatrixBlock> A = hz.getMap(aMapName);
        IMap<String, MatrixBlock> B = hz.getMap(bMapName);
        IMap<String, MatrixBlock> C = hz.getMap(cMapName);

        MatrixBlock a = A.get(aKey);
        MatrixBlock b = B.get(bKey);

        int r = a.rows;
        int c = b.cols;
        int kMax = a.cols;

        double[][] result = new double[r][c];

        for (int i = 0; i < r; i++)
            for (int k = 0; k < kMax; k++) {
                double av = a.data[i][k];
                for (int j = 0; j < c; j++)
                    result[i][j] += av * b.data[k][j];
            }

        MatrixBlock partial = new MatrixBlock(a.blockRow, b.blockCol, result);

        C.lock(cKey);
        try {
            MatrixBlock existing = C.get(cKey);
            if (existing == null) {
                C.put(cKey, partial);
            } else {
                existing.addInPlace(partial);
                C.put(cKey, existing);
            }
        } finally {
            C.unlock(cKey);
        }

        long computeEnd = System.nanoTime();
        return new TaskResult(computeEnd - computeStart);
    }
}
