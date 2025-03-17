package hashbench.benchmark;

public record ImplementationResult(String implementation, String hash, double elapsedMillisecondsMean, double elapsedMillisecondsStd, double hashesPerSecondMean, double hashesPerSecondStd) {
}
