package com.distributed.matrix;

import java.io.Serializable;

public class MatrixBlock implements Serializable {
    private static final long serialVersionUID = 1L;

    public final int blockRow;
    public final int blockCol;
    public final int rows;
    public final int cols;
    public final double[][] data;

    public MatrixBlock(int blockRow, int blockCol, double[][] data) {
        this.blockRow = blockRow;
        this.blockCol = blockCol;
        this.data = data;
        this.rows = data.length;
        this.cols = data[0].length;
    }

    public void addInPlace(MatrixBlock other) {
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++)
                this.data[i][j] += other.data[i][j];
    }
}
