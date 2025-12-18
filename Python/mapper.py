# mapper.py
"""
Mapper functions for distributed matrix multiplication using a MapReduce-like model.
"""

def mapper_A(element_A, N):
    """
    Element from matrix A: (i, k, value_A)
    Emit pairs for all j in [0, N).
    """
    i, k, val_A = element_A
    output = []
    for j in range(N):
        output.append(((i, j), ('A', k, val_A)))
    return output


def mapper_B(element_B, M):
    """
    Element from matrix B: (k, j, value_B)
    Emit pairs for all i in [0, M).
    """
    k, j, val_B = element_B
    output = []
    for i in range(M):
        output.append(((i, j), ('B', k, val_B)))
    return output
