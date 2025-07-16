package hashbench.sysinfo;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellij.openapi.components.Service;
import oshi.SystemInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public final class OshiSysInfoService implements SysInfoService {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Map<String, Object> snapshot() {
        var sysInfo = getSysInfo();

        // we can't modify SysInfo objects, so we convert it to a map
        var sysInfoMap = objectMapper.convertValue(sysInfo, new TypeReference<Map<String, Object>>() {});

        removeKeys(sysInfoMap, List.of(
                // depending on the operating system and user permissions, there may be sensitive data
                // (e.g. serial number of various hardware components).
                "serialNumber", "hardwareUUID", "processorID",

                // we don't need this
                "osVersion", "computerSystem", "cpu.logicalProcessors",
                "cpu.processorIdentifier", "cpu.physicalProcessors", "gpu"));

        return sysInfoMap;
    }

    private SysInfo getSysInfo() {
        var si = new SystemInfo();
        var osFamily = si.getOperatingSystem().getFamily();
        var osVersion = si.getOperatingSystem().getVersionInfo();
        var osManufacturer = si.getOperatingSystem().getManufacturer();
        var cpu = si.getHardware().getProcessor();
        var computerSystem = si.getHardware().getComputerSystem();
        var memory = si.getHardware().getMemory();
        var gpu = si.getHardware().getGraphicsCards();

        var properties = new String[] {
                "java.version", "java.vendor",
                "java.vm.name", "java.vm.version", "java.vm.vendor",
                "java.vm.specification.name", "java.vm.specification.vendor",
                "java.vm.specification.version"
        };
        var java = new HashMap<String, String>();
        for(var prop : properties) {
            var val = System.getProperty(prop);
            java.put(prop, val);
        }

        return new SysInfo(osFamily, osManufacturer, osVersion, computerSystem, cpu, memory, gpu, java);
    }

    private void removeKeys(Map<String, Object> map, List<String> keys) {
        removeKeys(map, keys, "");
    }

    /**
     * recursively walks all key-value pairs in the given map and removes the pair from
     * the map if the key is present in the given keys.
     */
    private void removeKeys(Map<String, Object> map, List<String> keys, String path) {
        var iterator = map.entrySet().iterator();

        while (iterator.hasNext()) {
            var entry = iterator.next();
            var key = entry.getKey();
            var value = entry.getValue();
            var keyPath = Stream.of(path, key)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.joining("."));

            if (value instanceof Map<?, ?> nestedMap) {
                removeKeys((Map<String, Object>) nestedMap, keys, keyPath);
            }

            if (keys.contains(key) || keys.contains(keyPath)) {
                iterator.remove();
            }
        }
    }
}
