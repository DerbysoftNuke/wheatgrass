package com.derby.nuke.wheatgrass.rpc.status;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.derby.nuke.wheatgrass.rpc.log.CommandExecutor;

/**
 * ApacheStatus
 *
 * @author Drizzt Yang
 */
public class ApacheStatus implements StatusAware {
    private static final Set<String> KEYS = new HashSet<String>();

    static {
        KEYS.add("process");
        KEYS.add("http.total");
        KEYS.add("http.state.");

        KEYS.add("https.total");
        KEYS.add("https.state.");

        KEYS.add("ajp.total");
        KEYS.add("ajp.state.");
    }

    CommandExecutor commandExecutor = new CommandExecutor();

    @Override
    public String getStatusPrefix() {
        return "apache";
    }

    @Override
    public Set<String> getStatusKeys() {
        return KEYS;
    }

    @Override
    public Map<String, String> status() {
        Map<String, String> status = new LinkedHashMap<String, String>();
        List<String> processArray = Arrays.asList(commandExecutor.execute("ps -ef | grep httpd").split("\n"));
        int processCount = 0;
        for (String process : processArray) {
            if (!process.contains("grep")) {
                processCount = processCount + 1;
            }
        }
        status.put("process", String.valueOf(processCount));

        status.putAll(findConnections("80", "http."));
        status.putAll(findConnections("443", "https."));
        status.putAll(findConnections("8009", "ajp."));

        return status;
    }

    private Map<String, String> findConnections(String filter, String prefix) {
        String result = commandExecutor.execute("netstat -ant | grep ':" + filter + " '");
        Map<String, String> status = new HashMap<String, String>();
        Map<String, Integer> connectionStatusMap = new HashMap<String, Integer>();

        List<List<String>> cols = commandExecutor.result2Cols(result, null, 6);
        int total = 0;
        for (List<String> connectionAttributes : cols) {
            String connectionDestination = connectionAttributes.get(3);
            if (!connectionDestination.endsWith(":" + filter)) {
                continue;
            }

            String connectionStatus = connectionAttributes.get(5);
            if (connectionStatusMap.get(connectionStatus) == null) {
                connectionStatusMap.put(connectionStatus, 0);
            }
            connectionStatusMap.put(connectionStatus, connectionStatusMap.get(connectionStatus) + 1);
            total = total + 1;
        }
        status.put(prefix + "total", String.valueOf(total));
        for (Map.Entry<String, Integer> entry : connectionStatusMap.entrySet()) {
            status.put(prefix + "state." + entry.getKey(), entry.getValue().toString());
        }
        return status;
    }

    private static ApacheStatus apacheStatus = new ApacheStatus();

    public static ApacheStatus get() {
        return apacheStatus;
    }
}
