package com.derby.nuke.wheatgrass.rpc.status;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * MemoryStatus
 *
 * @author Drizzt Yang
 */
public class MemoryStatus implements StatusAware {
    public static final String MEMORY = "memory";
    public static final Set<String> MEMORY_KEYS = new HashSet<String>();
    static {
        MEMORY_KEYS.add("max");
        MEMORY_KEYS.add("used");
        MEMORY_KEYS.add("heap.max");
        MEMORY_KEYS.add("heap.used");
        MEMORY_KEYS.add("nonHeap.max");
        MEMORY_KEYS.add("nonHeap.used");
    }

    private MemoryStatus() {
    }

    @Override
    public String getStatusPrefix() {
        return MEMORY;
    }

    @Override
    public Set<String> getStatusKeys() {
        return MEMORY_KEYS;
    }

    @Override
    public Map<String, String> status() {

        Map<String, String> properties = new LinkedHashMap<String, String> ();
        properties.put("max", String.valueOf(Runtime.getRuntime().maxMemory()));
        properties.put("used", String.valueOf(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()));

        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        properties.put("heap.max", String.valueOf(memoryMXBean.getHeapMemoryUsage().getMax()));
        properties.put("heap.used", String.valueOf(memoryMXBean.getHeapMemoryUsage().getUsed()));
        properties.put("nonHeap.max", String.valueOf(memoryMXBean.getNonHeapMemoryUsage().getMax()));
        properties.put("nonHeap.used", String.valueOf(memoryMXBean.getNonHeapMemoryUsage().getUsed()));
        return properties;
    }

    private static MemoryStatus memoryStatus = new MemoryStatus();

    public static MemoryStatus get() {
        return memoryStatus;
    }
}
