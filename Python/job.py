# job.py
"""
Simulated MapReduce pipeline for distributed matrix multiplication.
"""
from mapper import mapper_A, mapper_B
from reducer import reducer


class MatrixMultiplicationJob:

    def __init__(self, A, B):
        self.A = A
        self.B = B

        self.M = len(A)
        self.K = len(A[0])
        self.N = len(B[0])

    def run(self):
        # ------- MAP -------
        intermediate = []

        # Flatten A and B to triplets
        elements_A = [(i, k, self.A[i][k]) for i in range(self.M) for k in range(self.K)]
        elements_B = [(k, j, self.B[k][j]) for k in range(self.K) for j in range(self.N)]

        for elem in elements_A:
            intermediate.extend(mapper_A(elem, self.N))

        for elem in elements_B:
            intermediate.extend(mapper_B(elem, self.M))

        # ------- SHUFFLE -------
        grouped = {}
        for key, value in intermediate:
            if key not in grouped:
                grouped[key] = []
            grouped[key].append(value)

        # ------- REDUCE -------
        results = []
        for key, values in grouped.items():
            results.append(reducer(key, values))

        # Convert (i, j, val) into final matrix
        C = [[0.0] * self.N for _ in range(self.M)]
        for i, j, val in results:
            C[i][j] = val

        return C
