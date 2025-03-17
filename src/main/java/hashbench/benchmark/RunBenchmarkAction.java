package hashbench.benchmark;

import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.ui.Messages;
import hashbench.util.JsonService;
import hashbench.util.LogMessage;
import org.jetbrains.annotations.NotNull;
import com.intellij.openapi.diagnostic.Logger;

public class RunBenchmarkAction extends AnAction implements DumbAware {
    private static final Logger LOG = Logger.getInstance(RunBenchmarkAction.class);

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        try {
            var result = Messages.showOkCancelDialog(
                    "Please close all other applications (except IntelliJ) before proceeding.",
                    "Preparation", "Start", "Cancel", Messages.getWarningIcon()
            );

            if (result == Messages.CANCEL) {
                return;
            }

            ProgressManager.getInstance()
                    .run(new Task.Backgroundable(
                            e.getProject(), "Benchmarking", false) {
                        public void run(ProgressIndicator progressIndicator) {
                            try {
                                progressIndicator.setIndeterminate(false);
                                var benchmarkService = BenchmarkService.getInstance();
                                var benchmark = benchmarkService.benchmark(progressIndicator::setFraction);

                                var jsonService = JsonService.getInstance();
                                var jsonResult = jsonService.write(benchmark);

                                ApplicationManager.getApplication()
                                        .invokeLater(() -> new ResultDialog(jsonResult).show());
                            } catch (Exception ex) {
                                LOG.error(LogMessage.from("Unhandled exception in background task"), ex);
                            }
                        }
                    });
        } catch(Exception ex) {
            LOG.error(LogMessage.from("Unhandled exception"), ex);
        }
    }
}
