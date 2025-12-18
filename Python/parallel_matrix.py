# parallel_matrix.py
import numpy as np
import time
from multiprocessing import Pool, cpu_count

def _compute_row(args):
    row, B = args
    # row: 1d numpy array, B: 2d numpy array
    return row.dot(B)

def multiply_parallel(A, B, workers=None):
    """
    Multiply using multiprocessing by distributing rows.
    Returns (C, elapsed_seconds).
    """
    A_np = np.asarray(A)
    B_np = np.asarray(B)
    if workers is None:
        workers = cpu_count()

    start = time.time()

    # Prepare arguments: for each row send a view/copy of the row and the whole B
    rows_args = [(A_np[i, :], B_np) for i in range(A_np.shape[0])]

    # On Windows it's important to guard pool creation by __main__ - caller must do that.
    with Pool(processes=workers) as p:
        result_rows = p.map(_compute_row, rows_args)

    C = np.vstack(result_rows)
    end = time.time()
    return C, end - start
