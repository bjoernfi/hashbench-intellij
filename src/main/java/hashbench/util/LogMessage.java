package hashbench.util;

/**
 * Preprocess log messages before forwarding them to IntelliJ's logging infrastructure.
 */
public class LogMessage {

    /**
     * Returns the message without new lines.
     * This is required as using the filter of the
     * <a href="https://www.jetbrains.com/help/idea/setting-log-options.html">Log Viewer</a>
     * will lead to truncated log messages otherwise.
     */
    public static String from(String m) {
        return m.replace("\n", "");
    }
}
