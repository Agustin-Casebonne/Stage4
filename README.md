# Stage 4 – Dense Matrix Multiplication Performance Analysis

This project evaluates different dense matrix multiplication strategies implemented in **Java** and **Python**, including sequential, cache-blocked, parallel, and distributed executions.

## Requirements

### Java
- Java JDK 8 or higher
- Maven (optional, but recommended)
- Hazelcast 4.2.8 (already included in `lib/`)

### Python
- Python 3.8 or higher
- NumPy

---

## Java Execution

All Java source files are located under:
src/main/java/com/distributed/matrix

java --% -cp "target/classes;lib/hazelcast-4.2.8.jar" -Dhazelcast.port=5701 com.distributed.matrix.HazelcastWorker
java --% -cp "target/classes;lib/hazelcast-4.2.8.jar" com.distributed.matrix.HazelcastMatrixMultiply

## Run Python benchmarks
python basic_matrix_multiplication.py
python parallel_matrix_multiplication.py
python distributed_matrix_multiplication.py
