package com.distributed.matrix;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MatrixMultiplicationComparison {

    private static final int BLOCK_SIZE = 64;

    public static void main(String[] args) throws Exception {

        int N = 300; // Igual que Python

        double[][] A = new double[N][N];
        double[][] B = new double[N][N];

        double[][] C_basic = new double[N][N];
        double[][] C_parallel = new double[N][N];
        double[][] C_block = new double[N][N];

        init(A);
        init(B);

        System.out.println("=== JAVA BENCHMARK " + N + "x" + N + " ===");

        // ================= BASIC =================
        long t0 = System.nanoTime();
        multiplyBasic(A, B, C_basic, N);
        long t1 = System.nanoTime();
        double basicTime = (t1 - t0) / 1e9;
        System.out.printf("BASIC time: %.4f s%n", basicTime);

        // ================= PARALLEL =================
        t0 = System.nanoTime();
        multiplyParallel(A, B, C_parallel, N);
        t1 = System.nanoTime();
        double parallelTime = (t1 - t0) / 1e9;
        System.out.printf("PARALLEL time: %.4f s%n", parallelTime);

        // ================= BLOCKED =================
        t0 = System.nanoTime();
        multiplyBlocked(A, B, C_block, N);
        t1 = System.nanoTime();
        double blockTime = (t1 - t0) / 1e9;
        System.out.printf("BLOCKED time: %.4f s%n", blockTime);

        // ================= VERIFICATION =================
        double diffBlock = diffNorm(C_basic, C_block);
        double diffParallel = diffNorm(C_basic, C_parallel);

        System.out.printf("Verification ||C_basic - C_block||    = %.6e%n", diffBlock);
        System.out.printf("Verification ||C_basic - C_parallel|| = %.6e%n", diffParallel);
    }

    // =========================================================
    // INITIALIZATION
    // =========================================================
    private static void init(double[][] M) {
        Random r = new Random(42);
        for (int i = 0; i < M.length; i++)
            for (int j = 0; j < M[i].length; j++)
                M[i][j] = r.nextDouble();
    }

    // =========================================================
    // BASIC O(N^3)
    // =========================================================
    private static void multiplyBasic(double[][] A, double[][] B, double[][] C, int N) {
        for (int i = 0; i < N; i++)
            for (int j = 0; j < N; j++) {
                double sum = 0;
                for (int k = 0; k < N; k++)
                    sum += A[i][k] * B[k][j];
                C[i][j] = sum;
            }
    }

    // =========================================================
    // PARALLEL (row-based threading)
    // =========================================================
    private static void multiplyParallel(
            double[][] A, double[][] B, double[][] C, int N) throws InterruptedException {

        int threads = Runtime.getRuntime().availableProcessors();
        ExecutorService pool = Executors.newFixedThreadPool(threads);

        for (int i = 0; i < N; i++) {
            final int row = i;
            pool.submit(() -> {
                for (int j = 0; j < N; j++) {
                    double sum = 0;
                    for (int k = 0; k < N; k++)
                        sum += A[row][k] * B[k][j];
                    C[row][j] = sum;
                }
            });
        }

        pool.shutdown();
        pool.awaitTermination(1, TimeUnit.HOURS);
    }

    // =========================================================
    // BLOCKED (cache-friendly)
    // =========================================================
    private static void multiplyBlocked(double[][] A, double[][] B, double[][] C, int N) {
        for (int ii = 0; ii < N; ii += BLOCK_SIZE)
            for (int jj = 0; jj < N; jj += BLOCK_SIZE)
                for (int kk = 0; kk < N; kk += BLOCK_SIZE)
                    for (int i = ii; i < Math.min(ii + BLOCK_SIZE, N); i++)
                        for (int j = jj; j < Math.min(jj + BLOCK_SIZE, N); j++) {
                            double sum = C[i][j];
                            for (int k = kk; k < Math.min(kk + BLOCK_SIZE, N); k++)
                                sum += A[i][k] * B[k][j];
                            C[i][j] = sum;
                        }
    }

    // =========================================================
    // DIFFERENCE NORM
    // =========================================================
    private static double diffNorm(double[][] A, double[][] B) {
        double sum = 0;
        for (int i = 0; i < A.length; i++)
            for (int j = 0; j < A[i].length; j++) {
                double d = A[i][j] - B[i][j];
                sum += d * d;
            }
        return Math.sqrt(sum);
    }
}
