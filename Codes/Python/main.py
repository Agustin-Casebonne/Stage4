# main.py

import numpy as np

from basic_matrix import multiply_basic
from parallel_matrix import multiply_parallel
from distributed_job import multiply_distributed

def main():
    N = 300   # tamaño de prueba
    print(f"Generando matrices A({N}x{N}) y B({N}x{N})...")
    A = np.random.rand(N, N)
    B = np.random.rand(N, N)

    # --- BÁSICA ---
    print("\nEjecutando versión básica...")
    C_basic, t_basic = multiply_basic(A, B)
    print(f"Tiempo básica: {t_basic:.4f} s")

    # --- PARALELA ---
    print("\nEjecutando versión paralela...")
    C_parallel, t_parallel = multiply_parallel(A, B)
    print(f"Tiempo paralela: {t_parallel:.4f} s")

    # --- DISTRIBUIDA ---
    print("\nEjecutando versión distribuida...")
    C_dist, t_dist = multiply_distributed(A, B)
    print(f"Tiempo distribuida: {t_dist:.4f} s")

    # --- VALIDACIÓN ---
    diff_parallel = np.linalg.norm(C_basic - C_parallel)
    diff_dist = np.linalg.norm(C_basic - C_dist)

    print("\nDiferencia básica vs paralela:", diff_parallel)
    print("Diferencia básica vs distribuida:", diff_dist)

    print("\n--- RESUMEN ---")
    print(f"Básica:      {t_basic:.4f} s")
    print(f"Paralela:    {t_parallel:.4f} s")
    print(f"Distribuida: {t_dist:.4f} s")

if __name__ == "__main__":
    main()
