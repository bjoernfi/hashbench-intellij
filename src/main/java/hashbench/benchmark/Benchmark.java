package hashbench.benchmark;

import java.util.List;
import java.util.Map;

public record Benchmark(long runsPerImpl,
                        long warmupRunsPerImpl,
                        long iterations,
                        long pauseBetweenRuns,
                        String input,
                        List<ImplementationResult> results,
                        Map<String, Object> sysInfo
) {}
