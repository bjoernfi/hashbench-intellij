package hashbench.benchmark;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.diagnostic.Logger;
import hashbench.benchmark.impl.*;
import hashbench.sysinfo.SysInfoService;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

@Service
public final class BenchmarkService {
    private static final Logger LOG = Logger.getInstance(BenchmarkService.class);

    private final List<Implementation> IMPLEMENTATIONS = List.of(
            new JRESHA256(),
            new BouncyCastleSHA256()
    );

    private final long ITERATIONS = 2500000;
    private final String INPUT = "mmustermd8L_sA$9";

    private final long RUNS_PER_IMPL = 5;
    private final long WARMUP_RUNS_PER_IMPL = 1;
    private final long PAUSE_BETWEEN_RUNS = 1000 * 30;

    public static BenchmarkService getInstance() {
        return ApplicationManager.getApplication().getService(BenchmarkService.class);
    }

    public Benchmark benchmark(BenchmarkListener listener) {
        listener.reportProgress(0.01);

        var sysInfoService = SysInfoService.getInstance();
        var sysInfo = sysInfoService.snapshot();
        var runs = setupRuns();

        for (int i = 0; i < runs.size(); i++) {
            LOG.debug("Run %s/%s".formatted(i+1, runs.size()));
            var run = runs.get(i);
            var thread = new Thread(run);
            thread.start();
            try {
                thread.join();
                listener.reportProgress((i+1.0)/runs.size());

                if(i != runs.size() - 1) {
                    LOG.debug("Pausing");
                    Thread.sleep(PAUSE_BETWEEN_RUNS);
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        var runResults = runs.stream().skip(WARMUP_RUNS_PER_IMPL * IMPLEMENTATIONS.size())
                .map(BenchmarkRun::getResult)
                .toList();

        var implementationResults = averageResults(runResults);
        return new Benchmark(RUNS_PER_IMPL,
                WARMUP_RUNS_PER_IMPL,
                ITERATIONS,
                PAUSE_BETWEEN_RUNS,
                INPUT,
                implementationResults,
                sysInfo);
    }

    private List<BenchmarkRun> setupRuns() {
        var warmupRuns = new ArrayList<BenchmarkRun>();
        for(var impl : IMPLEMENTATIONS) {
            for(var i = 0; i < WARMUP_RUNS_PER_IMPL; i++) {
                var task = new BenchmarkRun(ITERATIONS, INPUT, impl);
                warmupRuns.add(task);
            }
        }
        Collections.shuffle(warmupRuns);

        var measureRuns = new ArrayList<BenchmarkRun>();
        for(var impl : IMPLEMENTATIONS) {
            for(var i = 0; i < RUNS_PER_IMPL; i++) {
                var task = new BenchmarkRun(ITERATIONS, INPUT, impl);
                measureRuns.add(task);
            }
        }
        Collections.shuffle(measureRuns);

        var allRuns = new ArrayList<BenchmarkRun>();
        allRuns.addAll(warmupRuns);
        allRuns.addAll(measureRuns);

        return allRuns;
    }

    private double std(DoubleStream stream, double average) {
        var variance = stream
                .map(i -> i - average)
                .map(i -> i*i).average().orElse(0);
        return Math.sqrt(variance);
    }

    private List<ImplementationResult> averageResults(List<BenchmarkRunResult> results) {
        return results.stream().collect(Collectors.groupingBy(BenchmarkRunResult::implementation)).entrySet()
                .stream().map(e -> {
                    var msAverage = e.getValue().stream()
                            .mapToDouble(BenchmarkRunResult::elapsedMilliseconds)
                            .average().orElse(0);
                    var msStd = std(e.getValue().stream()
                            .mapToDouble(BenchmarkRunResult::elapsedMilliseconds), msAverage);

                    var hashesPerSecondAverage = e.getValue().stream()
                            .mapToDouble(BenchmarkRunResult::hashesPerSecond)
                            .average().orElse(0);
                    var hashesPerSecondStd = std(e.getValue().stream()
                            .mapToDouble(BenchmarkRunResult::hashesPerSecond), hashesPerSecondAverage);

                    return new ImplementationResult(
                            e.getKey(),
                            e.getValue().get(0).hash(),
                            msAverage,
                            msStd,
                            hashesPerSecondAverage,
                            hashesPerSecondStd
                    );
                }).toList();
    }
}
