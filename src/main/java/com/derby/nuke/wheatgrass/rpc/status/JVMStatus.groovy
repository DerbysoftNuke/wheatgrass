package com.derby.nuke.wheatgrass.rpc.status;

import java.lang.management.ManagementFactory

/**
 * JVMStatus
 *
 * @author Drizzt Yang
 */
public class JVMStatus implements StatusAware {
    public static final String NAME = "jvm";
    private static final Set<String> KEYS = new HashSet<String>();
    static {
        KEYS.add("os.");
        KEYS.add("starttime");
        KEYS.add("uptime");
        KEYS.add("currenttime");

        KEYS.add("timezone.id");
        KEYS.add("timezone.name");
    }
    @Override
    public String getStatusPrefix() {
        return NAME;
    }

    @Override
    public Set<String> getStatusKeys() {
        return KEYS;
    }

    @Override
    public Map<String, String> status() {
        Map<String, String> status = new LinkedHashMap<String, String>();
        status.put("os.arch", ManagementFactory.getOperatingSystemMXBean().getArch());
        status.put("os.name", ManagementFactory.getOperatingSystemMXBean().getName());
        status.put("os.version", ManagementFactory.getOperatingSystemMXBean().getVersion());
        status.put("os.processors", String.valueOf(ManagementFactory.getOperatingSystemMXBean().getAvailableProcessors()));
        status.put("os.load", String.valueOf(ManagementFactory.getOperatingSystemMXBean().getSystemLoadAverage()));

        status.put("starttime", String.valueOf(ManagementFactory.getRuntimeMXBean().getStartTime()));
        status.put("currenttime", String.valueOf(System.currentTimeMillis()));
        status.put("uptime", String.valueOf(ManagementFactory.getRuntimeMXBean().getUptime()));

        status.put("timezone.id", TimeZone.getDefault().getID());
        status.put("timezone.name", TimeZone.getDefault().getDisplayName(Locale.ENGLISH));

        return status;
    }

    private static JVMStatus status = new JVMStatus();

    public static JVMStatus get() {
        return status;
    }
}
