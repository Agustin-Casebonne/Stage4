package com.distributed.matrix;
public class RowColumnMajor {

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

        double[][] C1 = new double[N][N]; // For row-major multiplication
        double[][] C2 = new double[N][N]; // For column-major multiplication

        // Row-major matrix multiplication
        System.out.println("Row-major multiplication result:");
        rowMajorMatrixMultiplication(A, B, C1, N);
        printMatrix(C1);

        // Column-major matrix multiplication (simulated)
        System.out.println("Column-major multiplication result (simulated):");
        columnMajorMatrixMultiplication(A, B, C2, N);
        printMatrix(C2);

        // Check if both matrices are equal
        System.out.println("Are the results the same? " + matricesAreEqual(C1, C2));
    }

    // Standard row-major matrix multiplication
    private static void rowMajorMatrixMultiplication(double[][] A, double[][] B, double[][] C, int N) {
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                C[i][j] = 0;
                for (int k = 0; k < N; k++) {
                    C[i][j] += A[i][k] * B[k][j];  // Correct row-major access
                }
            }
        }
    }

    // Simulated column-major matrix multiplication (same logic but different loop order)
    private static void columnMajorMatrixMultiplication(double[][] A, double[][] B, double[][] C, int N) {
        for (int j = 0; j < N; j++) { // Loop over columns first
            for (int i = 0; i < N; i++) {
                C[i][j] = 0;
                for (int k = 0; k < N; k++) {
                    C[i][j] += A[i][k] * B[k][j];  // Keep the correct multiplication logic
                }
            }
        }
    }

    // Check if two matrices are equal
    private static boolean matricesAreEqual(double[][] C1, double[][] C2) {
        for (int i = 0; i < C1.length; i++) {
            for (int j = 0; j < C1[i].length; j++) {
                if (Math.abs(C1[i][j] - C2[i][j]) > 1e-9) {
                    return false;
                }
            }
        }
        return true;
    }

    // Print the matrix
    private static void printMatrix(double[][] matrix) {
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                System.out.printf("%.2f ", matrix[i][j]);
            }
            System.out.println();
        }
    }
}

/*
 Mathematically: both methods yield the same result of A × B.

Computationally: the order of the loops affects performance in
low-level languages (C/C++, Fortran) because memory can be organized
in row-major or column-major order.

In row-major (as in C/Java): traversing rows consecutively makes better
use of cache locality.

In column-major (as in Fortran/Matlab): traversing columns is more efficient.

In pure Java this is not very noticeable, because arrays are stored by rows,
but the example is didactic to illustrate the effect of loop ordering. 
*/