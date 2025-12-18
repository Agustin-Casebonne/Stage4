package com.distributed.matrix;
public class BlockMatrixMultiplicationWithTrace {

    // Block size (tile size)
    private static final int BLOCK_SIZE = 2;

    public static void main(String[] args) {
        int N = 4; // Matrix size (N x N)

        // Initialize matrices A, B, and C
        double[][] A = {
            {1, 2, 3, 4},
            {1, 2, 3, 4},
            {1, 2, 3, 4},
            {1, 2, 3, 4}
        };

        double[][] B = {
            {1, 2, 1, 2},
            {1, 2, 1, 2},
            {1, 2, 1, 2},
            {1, 2, 1, 2}
        };

        double[][] C = new double[N][N]; // Result matrix

        // Perform matrix multiplication with blocking
        System.out.println("Performing matrix multiplication with blocking...");
        blockMatrixMultiplication(A, B, C, N);

        // Final result
        System.out.println("\nFinal result matrix C:");
        printMatrix(C);
    }

    // Matrix multiplication using the blocking approach
    private static void blockMatrixMultiplication(double[][] A, double[][] B, double[][] C, int N) {
        for (int i = 0; i < N; i += BLOCK_SIZE) {
            for (int j = 0; j < N; j += BLOCK_SIZE) {
                for (int k = 0; k < N; k += BLOCK_SIZE) {
                    System.out.println("\nMultiplying blocks:");
                    System.out.println("A block (" + i + ", " + k + "):");
                    printBlock(A, i, k, N);

                    System.out.println("B block (" + k + ", " + j + "):");
                    printBlock(B, k, j, N);

                    multiplyBlock(A, B, C, i, j, k, N);

                    System.out.println("Intermediate result C block (" + i + ", " + j + "):");
                    printBlock(C, i, j, N);
                }
            }
        }
    }

    // Multiply individual blocks of the matrices
    private static void multiplyBlock(double[][] A, double[][] B, double[][] C, int rowBlock, int colBlock, int kBlock, int N) {
        for (int i = rowBlock; i < Math.min(rowBlock + BLOCK_SIZE, N); i++) {
            for (int j = colBlock; j < Math.min(colBlock + BLOCK_SIZE, N); j++) {
                double sum = 0;
                for (int k = kBlock; k < Math.min(kBlock + BLOCK_SIZE, N); k++) {
                    sum += A[i][k] * B[k][j];
                }
                C[i][j] += sum; // Update the result matrix
            }
        }
    }

    // Print a specific block from the matrix
    private static void printBlock(double[][] matrix, int rowStart, int colStart, int N) {
        for (int i = rowStart; i < Math.min(rowStart + BLOCK_SIZE, N); i++) {
            for (int j = colStart; j < Math.min(colStart + BLOCK_SIZE, N); j++) {
                System.out.printf("%.2f ", matrix[i][j]);
            }
            System.out.println();
        }
    }

    // Print the entire matrix
    private static void printMatrix(double[][] matrix) {
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                System.out.printf("%.2f ", matrix[i][j]);
            }
            System.out.println();
        }
    }
}
