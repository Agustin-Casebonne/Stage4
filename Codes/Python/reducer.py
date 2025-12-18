# reducer.py
"""
Reducer function for distributed matrix multiplication.
"""

def reducer(key, values):
    """
    key = (i, j)
    values = [('A' or 'B', k, val), ...]
    Computes C[i][j] = Σ_k A[i][k] * B[k][j]
    """
    map_a = {}
    map_b = {}

    for matrix_id, k, val in values:
        if matrix_id == 'A':
            map_a[k] = val
        else:
            map_b[k] = val

    result = 0.0
    for k in map_a:
        if k in map_b:
            result += map_a[k] * map_b[k]

    return (key[0], key[1], result)
