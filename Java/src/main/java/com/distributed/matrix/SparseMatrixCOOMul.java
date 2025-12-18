package com.distributed.matrix;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SparseMatrixCOOMul {

    // Sparse matrix in COO format
    static class COOMatrix {
        List<Integer> rowIndices;   // Row indices of non-zero elements
        List<Integer> colIndices;   // Column indices of non-zero elements
        List<Double> values;        // Non-zero values
        int rows, cols;             // Number of rows and columns in the matrix

        COOMatrix(int rows, int cols) {
            this.rows = rows;
            this.cols = cols;
            this.rowIndices = new ArrayList<>();
            this.colIndices = new ArrayList<>();
            this.values = new ArrayList<>();
        }

        // Method to add a non-zero element to the matrix
        public void addValue(int row, int col, double value) {
            if (value != 0) {
                rowIndices.add(row);
                colIndices.add(col);
                values.add(value);
            }
        }

        // Method to print the COO matrix details
        public void printCOODetails() {
            System.out.println("COO Representation:");
            System.out.println("Row Indices: " + rowIndices);
            System.out.println("Column Indices: " + colIndices);
            System.out.println("Values: " + values);
        }

        // Method to print the matrix in its full (dense) form
        public void printDenseMatrix() {
            double[][] denseMatrix = new double[rows][cols];

            for (int i = 0; i < values.size(); i++) {
                denseMatrix[rowIndices.get(i)][colIndices.get(i)] = values.get(i);
            }

            System.out.println("Dense Matrix:");
            for (double[] row : denseMatrix) {
                System.out.println(Arrays.toString(row));
            }
        }

        // Method to multiply two COO matrices
        public COOMatrix multiply(COOMatrix B) {
            if (this.cols != B.rows) {
                throw new IllegalArgumentException("Matrix dimensions do not match for multiplication.");
            }

            COOMatrix result = new COOMatrix(this.rows, B.cols);

            // Temporary array to store multiplication results for each entry
            double[][] tempResult = new double[this.rows][B.cols];

            // Multiply non-zero elements from both matrices
            for (int i = 0; i < this.values.size(); i++) {
                int rowA = this.rowIndices.get(i);
                int colA = this.colIndices.get(i);
                double valA = this.values.get(i);

                // Find matching column in B
                for (int j = 0; j < B.values.size(); j++) {
                    if (B.rowIndices.get(j) == colA) {
                        int colB = B.colIndices.get(j);
                        double valB = B.values.get(j);
                        tempResult[rowA][colB] += valA * valB;
                    }
                }
            }

            // Convert the resulting dense matrix back into COO format
            for (int i = 0; i < this.rows; i++) {
                for (int j = 0; j < B.cols; j++) {
                    if (tempResult[i][j] != 0) {
                        result.addValue(i, j, tempResult[i][j]);
                    }
                }
            }

            return result;
        }
    }

    // Method to create a COO matrix from a 2D array
    public static COOMatrix convertToCOO(double[][] matrix) {
        int rows = matrix.length;
        int cols = matrix[0].length;
        COOMatrix cooMatrix = new COOMatrix(rows, cols);

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (matrix[i][j] != 0) {
                    cooMatrix.addValue(i, j, matrix[i][j]);
                }
            }
        }

        return cooMatrix;
    }

    public static void main(String[] args) {
        // Example sparse matrix A
        double[][] matrixA = {
            {1, 0, 0, 0},
            {0, 2, 0, 0},
            {0, 0, 0, 3},
            {4, 0, 5, 0}
        };

        // Example sparse matrix B
        double[][] matrixB = {
            {0, 0, 1, 0},
            {2, 0, 0, 0},
            {0, 0, 0, 3},
            {0, 4, 0, 0}
        };

        // Convert matrices A and B to COO format
        COOMatrix cooA = convertToCOO(matrixA);
        COOMatrix cooB = convertToCOO(matrixB);

        // Print COO representation of both matrices
        System.out.println("Matrix A in COO format:");
        cooA.printCOODetails();
        System.out.println("Matrix A in dense format:");
        cooA.printDenseMatrix();

        System.out.println("\nMatrix B in COO format:");
        cooB.printCOODetails();
        System.out.println("Matrix B in dense format:");
        cooB.printDenseMatrix();

        // Perform matrix multiplication (A * B)
        System.out.println("\nMultiplying Matrix A and Matrix B:");
        COOMatrix resultMatrix = cooA.multiply(cooB);

        // Print the COO representation and dense form of the result
        System.out.println("\nResulting Matrix in COO format:");
        resultMatrix.printCOODetails();
        System.out.println("Resulting Matrix in dense format:");
        resultMatrix.printDenseMatrix();
    }
}
