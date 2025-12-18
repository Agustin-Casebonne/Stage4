    package com.distributed.matrix;
    import java.io.*;
    import java.util.*;

    public class DistributedMatrixMultiplication {

        private static final int M = 4; // Filas de A / Filas de C
        private static final int K = 4; // Columnas de A / Filas de B (Índice interno)
        private static final int N = 4; // Columnas de B / Columnas de C

        static class Context {
            public void write(String key, String value) {
            
                System.out.println("Intermediate Output: Key=" + key + ", Value=" + value);
            }
            public void writeResult(String key, double value) {
                // Salida final del Reducer
                System.out.println("Result C: Key=" + key + ", Value=" + String.format("%.2f", value));
            }
        }


        public static class MatrixMapper {
        
            public void map(String line, Context context) {
                String[] parts = line.split(" ");
                String matrixId = parts[0];
                int i = Integer.parseInt(parts[1]); 
                int j = Integer.parseInt(parts[2]); 
                double value = Double.parseDouble(parts[3]);

                if (matrixId.equals("A")) {
                    int rowA = i;
                    int k = j;
                    // A[i][k] contribuye a C[i][j] para todos los j (0 a N-1)
                    for (int colB = 0; colB < N; colB++) { 
                        // Clave: "i,j" (posición en C), Valor: "A,k,A_ik"
                        context.write(rowA + "," + colB, "A," + k + "," + value);
                    }
                } else if (matrixId.equals("B")) {
                    int k = i;
                    int colB = j;
                    // B[k][j] contribuye a C[i][j] para todos los i (0 a M-1)
                    for (int rowA = 0; rowA < M; rowA++) { 
                        // Clave: "i,j" (posición en C), Valor: "B,k,B_kj"
                        context.write(rowA + "," + colB, "B," + k + "," + value);
                    }
                }
            }
        }

        
        public static class MatrixReducer {
        
            public void reduce(String key, List<String> values, Context context) {
                
                // mapA: k -> A_ik | mapB: k -> B_kj
                Map<Integer, Double> mapA = new HashMap<>(); 
                Map<Integer, Double> mapB = new HashMap<>(); 

                for (String val : values) {
                    String[] parts = val.split(",");
                    String type = parts[0];
                    int k = Integer.parseInt(parts[1]);
                    double value = Double.parseDouble(parts[2]);

                    if (type.equals("A")) {
                        mapA.put(k, value);
                    } else {
                        mapB.put(k, value);
                    }
                }

                double result = 0.0;
                // Iterar sobre el índice interno común k para calcular la sumatoria
                for (Integer k : mapA.keySet()) {
                    if (mapB.containsKey(k)) {
                        // C_ij += A_ik * B_kj
                        result += mapA.get(k) * mapB.get(k);
                    }
                }

                // Salida: Clave: "i,j", Valor: C_ij
                context.writeResult(key, result); 
            }
        }

        public static void main(String[] args) {
            // --- SIMULACIÓN DE DATOS DE ENTRADA (Matrices 4x4) ---
            // A (1, 2, 3, 4) por fila: 1.0, 2.0, 3.0, 4.0
            // B (1, 2, 1, 2) por fila: 1.0, 2.0, 1.0, 2.0
            
            List<String> inputLines = new ArrayList<>();
            // Matrix A (filas i = 0 a 3, columnas k = 0 a 3)
            // Usamos los valores del archivo BlockMatrixMultiplicationWithTrace.java
            for (int i = 0; i < M; i++) {
                for (int k = 0; k < K; k++) {
                    inputLines.add(String.format("A %d %d %.1f", i, k, (double)k + 1.0)); // A[i][k] = k+1
                }
            }
            // Matrix B (filas k = 0 a 3, columnas j = 0 a 3)
            // Usamos los valores del archivo BlockMatrixMultiplicationWithTrace.java
            double[] bRow = {1.0, 2.0, 1.0, 2.0};
            for (int k = 0; k < K; k++) {
                for (int j = 0; j < N; j++) {
                    inputLines.add(String.format("B %d %d %.1f", k, j, bRow[j])); // B[k][j]
                }
            }

            System.out.println("--- FASE MAP (Generación de Pares Intermedios) ---");
            MatrixMapper mapper = new MatrixMapper();
            Context context = new Context();
            
            // Simulación de la ejecución de los mappers
            List<Map.Entry<String, String>> intermediatePairs = new ArrayList<>();
            for (String line : inputLines) {
                // En lugar de imprimir, capturamos los pares para simular el Shuffle
                
                // Simulación de la función map
                mapper.map(line, new Context() {
                    @Override
                    public void write(String key, String value) {
                        intermediatePairs.add(new AbstractMap.SimpleEntry<>(key, value));
                        System.out.println("Mapper Emitted: Key=" + key + ", Value=" + value);
                    }
                });
            }

            System.out.println("\n--- FASE SHUFFLE & SORT (Agrupación por Clave) ---");
            // Agrupación de todos los valores por la clave (i, j)
            Map<String, List<String>> groupedData = new HashMap<>();
            for (Map.Entry<String, String> entry : intermediatePairs) {
                groupedData.computeIfAbsent(entry.getKey(), k -> new ArrayList<>()).add(entry.getValue());
            }
            System.out.println("Total de claves únicas (celdas C): " + groupedData.size()); // Debería ser M*N

            System.out.println("\n--- FASE REDUCE (Cálculo de C[i][j]) ---");
            MatrixReducer reducer = new MatrixReducer();
            
            // Simulación de la ejecución de los reducers
            for (Map.Entry<String, List<String>> entry : groupedData.entrySet()) {
                reducer.reduce(entry.getKey(), entry.getValue(), context);
            }
        }
    }