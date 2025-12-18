# basic_matrix.py
import numpy as np
import time

def multiply_basic(A, B):
    """
    Single-core classical matrix multiplication.
    A and B can be lists or numpy arrays. Returns (C, elapsed_seconds)
    """
    A_np = np.asarray(A)
    B_np = np.asarray(B)
    start = time.time()
    C = A_np.dot(B_np)
    end = time.time()
    return C, end - start
