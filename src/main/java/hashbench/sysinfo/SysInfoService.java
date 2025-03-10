package hashbench.sysinfo;

import com.intellij.openapi.application.ApplicationManager;

import java.util.Map;

public interface SysInfoService {
    static SysInfoService getInstance() {
        return ApplicationManager.getApplication().getService(SysInfoService.class);
    }

    Map<String, Object> snapshot();
}
