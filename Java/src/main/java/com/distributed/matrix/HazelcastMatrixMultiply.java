package com.distributed.matrix;

import com.hazelcast.config.*;
import com.hazelcast.core.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import com.hazelcast.config.Config;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.config.NetworkConfig;
import com.hazelcast.config.TcpIpConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IExecutorService;
import com.hazelcast.map.IMap;

import java.util.Random;

import java.io.Serializable;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

public class HazelcastMatrixMultiply {

    public static Config createConfig() {
        Config config = new Config();
        config.getNetworkConfig().setPort(Integer.getInteger("hazelcast.port", 5701));
        return config;
    }

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        HazelcastInstance hz = Hazelcast.newHazelcastInstance(createConfig());

        System.out.println("=== HAZELCAST DISTRIBUTED MASTER ===");

        int N = 2000;
        int BLOCK_SIZE = 64;

        // Matrices distribuidas
        IMap<String, double[][]> map = hz.getMap("matrices");
        double[][] A = new double[N][N];
        double[][] B = new double[N][N];

        // Inicializar matrices
        for (int i = 0; i < N; i++)
            for (int j = 0; j < N; j++) {
                A[i][j] = i + j;
                B[i][j] = i - j;
            }

        map.put("A", A);
        map.put("B", B);
        map.put("C", new double[N][N]);

        IExecutorService executor = hz.getExecutorService("executor");

        long start = System.currentTimeMillis();

        // Distribuir tareas por bloques
        for (int i = 0; i < N; i += BLOCK_SIZE) {
            for (int j = 0; j < N; j += BLOCK_SIZE) {
                BlockMultiplyTask task = new BlockMultiplyTask(i, j, BLOCK_SIZE);
                executor.submit(task).get(); // Espera el resultado para simplificar
            }
        }

        long end = System.currentTimeMillis();

        // Comprobar resultado sumando algunos valores
        double[][] C = map.get("C");
        double sum = 0;
        for (int i = 0; i < N; i += N / 10)
            for (int j = 0; j < N; j += N / 10)
                sum += C[i][j];

        System.out.println("Checksum parcial de C: " + sum);
        System.out.println("Tiempo total: " + (end - start) / 1000.0 + " s");

        // Mantener cluster vivo
        Thread.sleep(Long.MAX_VALUE);
    }

    // Tarea que multiplica un bloque
    static class BlockMultiplyTask implements Callable<Void>, Serializable {
        int row, col, blockSize;

        BlockMultiplyTask(int row, int col, int blockSize) {
            this.row = row;
            this.col = col;
            this.blockSize = blockSize;
        }

        @Override
        public Void call() throws Exception {
            HazelcastInstance hz = Hazelcast.getHazelcastInstanceByName("hazelcast-instance");
            if (hz == null) hz = Hazelcast.newHazelcastInstance(createConfig());

            IMap<String, double[][]> map = hz.getMap("matrices");
            double[][] A = map.get("A");
            double[][] B = map.get("B");
            double[][] C = map.get("C");
            int N = A.length;

            for (int i = row; i < Math.min(row + blockSize, N); i++)
                for (int j = col; j < Math.min(col + blockSize, N); j++)
                    for (int k = 0; k < N; k++)
                        C[i][j] += A[i][k] * B[k][j];

            map.put("C", C);
            return null;
        }
    }
}
