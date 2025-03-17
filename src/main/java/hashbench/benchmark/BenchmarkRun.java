package hashbench.benchmark;

import hashbench.benchmark.impl.Implementation;

import java.nio.charset.StandardCharsets;

public class BenchmarkRun implements Runnable {

    private final long iterations;
    private final String input;
    private final Implementation implementation;
    private BenchmarkRunResult result;

    public BenchmarkRun(long iterations, String input, Implementation implementation) {
        this.iterations = iterations;
        this.input = input;
        this.implementation = implementation;
    }

    public BenchmarkRunResult getResult() {
        return result;
    }

    @Override
    public void run() {
        var inputBytes = input.getBytes(StandardCharsets.UTF_8);
        var startTime = System.nanoTime();

        var hash = inputBytes;
        for (var k = 0; k < iterations; k++) {
            hash = implementation.hash(hash);
        }

        var elapsedMilliseconds = (System.nanoTime() - startTime) / 1000000.0;
        var elapsedSeconds = (elapsedMilliseconds / 1000);
        var hashesPerSecond = iterations / elapsedSeconds;
        this.result = new BenchmarkRunResult(implementation.getClass().getSimpleName(), bytesToHex(hash),
                elapsedMilliseconds, hashesPerSecond
        );
    }

    private String bytesToHex(byte[] bytes) {
        var hexString = new StringBuilder(2 * bytes.length);
        for (byte b : bytes) {
            hexString.append(String.format("%02x", b));
        }
        return hexString.toString();
    }
}
