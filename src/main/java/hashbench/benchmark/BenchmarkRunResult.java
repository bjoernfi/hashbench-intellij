package hashbench.benchmark;

public record BenchmarkRunResult(String implementation, String hash, double elapsedMilliseconds, double hashesPerSecond) {
}
