package com.distributed.matrix;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;

public class HazelcastWorker {

    public static void main(String[] args) {

        Config config = HazelcastMatrixMultiply.createConfig();
        Hazelcast.newHazelcastInstance(config);

        System.out.println("WORKER NODE READY");

        // No se apaga: espera tareas
        try {
            Thread.sleep(Long.MAX_VALUE);
        } catch (InterruptedException ignored) {}
    }
}
