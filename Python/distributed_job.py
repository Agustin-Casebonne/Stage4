# distributed_job.py
import numpy as np
import time
import math

def multiply_distributed(A, B, block_size=64):
    """
    Simulated distributed multiply by tiling A and B into square blocks of size block_size.
    Works with numpy arrays or lists. Returns (C, elapsed_seconds).
    """
    A = np.asarray(A)
    B = np.asarray(B)

    # Support rectangular matrices A (M x K) and B (K x N)
    M, K = A.shape
    K2, N = B.shape
    assert K == K2, "Inner dimensions must match"

    start = time.time()

    # Prepare result
    C = np.zeros((M, N), dtype=A.dtype)

    # Number of blocks in each dimension (ceil)
    br = math.ceil(M / block_size)
    bk = math.ceil(K / block_size)
    bc = math.ceil(N / block_size)

    for bi in range(br):
        i0 = bi * block_size
        i1 = min(i0 + block_size, M)
        for bj in range(bc):
            j0 = bj * block_size
            j1 = min(j0 + block_size, N)
            # accumulate C_block
            C_block = np.zeros((i1 - i0, j1 - j0), dtype=A.dtype)
            for bk_idx in range(bk):
                k0 = bk_idx * block_size
                k1 = min(k0 + block_size, K)
                A_block = A[i0:i1, k0:k1]
                B_block = B[k0:k1, j0:j1]
                if A_block.size == 0 or B_block.size == 0:
                    continue
                C_block += A_block.dot(B_block)
            C[i0:i1, j0:j1] = C_block

    end = time.time()
    return C, end - start
