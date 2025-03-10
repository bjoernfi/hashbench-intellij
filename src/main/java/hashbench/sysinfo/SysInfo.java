package hashbench.sysinfo;

import oshi.hardware.*;
import oshi.software.os.OperatingSystem;

import java.util.List;
import java.util.Map;

public record SysInfo(String osFamily,
                      String osManufacturer,
                      OperatingSystem.OSVersionInfo osVersion,
                      ComputerSystem computerSystem,
                      CentralProcessor cpu,
                      GlobalMemory memory,
                      List<GraphicsCard> gpu,
                      Map<String, String> java) {

}
