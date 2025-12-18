# benchmark.py
import numpy as np
import time
from basic_matrix import multiply_basic
from parallel_matrix import multiply_parallel
from distributed_job import multiply_distributed
import argparse

def generate_matrix(n_rows, n_cols, seed=None):
    if seed is not None:
        np.random.seed(seed)
    return np.random.random((n_rows, n_cols))

def run_one(n, workers=None, block_size=64, seed=42, verify=True):
    print(f"\n=== BENCHMARK: {n}x{n} matrices ===")
    A = generate_matrix(n, n, seed=seed)
    B = generate_matrix(n, n, seed=seed+1)

    # BASIC
    print("Running BASIC (numpy.dot)...")
    C_basic, t_basic = multiply_basic(A, B)
    print(f"  BASIC time: {t_basic:.4f} s")

    # PARALLEL
    print("Running PARALLEL (multiprocessing rows)...")
    # multiprocessing on Windows requires spawning; ensure this is called from main block
    C_par, t_par = multiply_parallel(A, B, workers=workers)
    print(f"  PARALLEL time: {t_par:.4f} s")

    # DISTRIBUTED (block simulation)
    print(f"Running DISTRIBUTED (block_size={block_size})...")
    C_dist, t_dist = multiply_distributed(A, B, block_size=block_size)
    print(f"  DISTRIBUTED time: {t_dist:.4f} s")

    if verify:
        # compute norms of differences
        err_par = np.linalg.norm(C_basic - C_par)
        err_dist = np.linalg.norm(C_basic - C_dist)
        print(f"\nVerification (norm difference):")
        print(f"  BASIC vs PARALLEL: {err_par:.6e}")
        print(f"  BASIC vs DISTRIBUTED: {err_dist:.6e}")

    return {"n": n, "basic": t_basic, "parallel": t_par, "distributed": t_dist}

def run_sizes(sizes, workers=None, block_size=64):
    results = []
    for n in sizes:
        res = run_one(n, workers=workers, block_size=block_size)
        results.append(res)
    return results

if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="Benchmark matrix multiplication implementations")
    parser.add_argument("-s", "--sizes", nargs="+", type=int, default=[100,200,300], help="Matrix sizes to test")
    parser.add_argument("-w", "--workers", type=int, default=None, help="Number of worker processes for parallel version")
    parser.add_argument("-b", "--block", type=int, default=64, help="Block size for distributed simulation")
    args = parser.parse_args()

    # On Windows, multiprocessing Pool must be created inside __main__, which we satisfy here.
    results = run_sizes(args.sizes, workers=args.workers, block_size=args.block)

    print("\n=== SUMMARY ===")
    for r in results:
        print(f"{r['n']:4d} | basic: {r['basic']:.4f}s | parallel: {r['parallel']:.4f}s | distributed: {r['distributed']:.4f}s")
