package com.distributed.matrix;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ParallelMatrixMultiplication {
    private static void parallelMatrixMultiplication(
        double[][] A, double[][] B, double[][] C, int N) throws InterruptedException {

    int numThreads = Runtime.getRuntime().availableProcessors();
    ExecutorService executor = Executors.newFixedThreadPool(numThreads);

    for (int i = 0; i < N; i++) {
        final int row = i;
        executor.submit(() -> {
            for (int j = 0; j < N; j++) {
                double sum = 0;
                for (int k = 0; k < N; k++) {
                    sum += A[row][k] * B[k][j];
                }
                C[row][j] = sum;
            }
        });
    }

    executor.shutdown();
    executor.awaitTermination(1, TimeUnit.HOURS);
    }

    
}
