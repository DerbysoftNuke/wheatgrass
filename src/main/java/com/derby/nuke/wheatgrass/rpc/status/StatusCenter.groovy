package com.derby.nuke.wheatgrass.rpc.status;

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * StatusCenter
 *
 * @author Drizzt Yang
 */
@Configuration
public class StatusCenter implements StatusService {
	
    private static final Logger logger = LoggerFactory.getLogger(StatusCenter.class);

    public Map<String, StatusAware> statusServiceMap = new HashMap<String, StatusAware>();
    
    public Set<String> keys() {
        LinkedHashSet<String> statusKeys = new LinkedHashSet<String>();
        for (Map.Entry<String, StatusAware> statusServiceEntry : statusServiceMap.entrySet()) {
            Set<String> innerStatusKeys = statusServiceEntry.getValue().getStatusKeys();
            for (String innerStatusKey : innerStatusKeys) {
                statusKeys.add(statusServiceEntry.getKey() + "." + innerStatusKey);    
            }
        }
        return statusKeys;
    }
    
    public Map<String, String> status() {
        return loadStatus(statusServiceMap.keySet());    
    }

    public Map<String, String> statusWith(Set<String> keys) {
        Set<String> prefixSet = new HashSet<String>();
        for (String key : keys) {
            String prefix = key.substring(0, key.indexOf("."));
            prefixSet.add(prefix);   
        }
        Map<String, String> status = loadStatus(prefixSet);
        Map<String, String> filterStatus = new LinkedHashMap<String, String>();
        
        for (Map.Entry<String, String> entry : status.entrySet()) {
            if(accept(entry.getKey(), keys)) {
                filterStatus.put(entry.getKey(), entry.getValue());
            }
        }
        return filterStatus;
    }

    public Map<String, String> statusWithout(Set<String> keys) {
        Set<String> excludePrefixSet = new HashSet<String>();
        for (String key : keys) {
            if (key.endsWith(".") && !key.substring(0, key.length() - 1).contains(".")) {
                excludePrefixSet.add(key.substring(0, key.length() - 1));
            }
        }

        Set<String> prefixSet = new HashSet<String>();
        for (String prefix : statusServiceMap.keySet()) {
            if (!excludePrefixSet.contains(prefix)) {
                prefixSet.add(prefix);
            }
        }

        Map<String, String> status = loadStatus(prefixSet);
        Map<String, String> filterStatus = new LinkedHashMap<String, String>();

        for (Map.Entry<String, String> entry : status.entrySet()) {
            if (!accept(entry.getKey(), keys)) {
                filterStatus.put(entry.getKey(), entry.getValue());
            }
        }
        return filterStatus;
    }

    private boolean accept(String statusKey, Set<String> keys) {
        for (String key : keys) {
            if (key.equals(statusKey)) {
                return true;
            } else if (key.endsWith(".") && statusKey.startsWith(key)) {
                return true;
            }
        }
        return false;
    }

    private Map<String, String> loadStatus(Set<String> prefixSet) {
        Map<String, String> filterStatus = new LinkedHashMap<String, String>();
        for (String prefix : prefixSet) {
            StatusAware statusAware = statusServiceMap.get(prefix);
            if(statusAware == null) {
                continue;
            }
            try {
                Map<String, String> status = statusAware.status();
                for (Map.Entry<String, String> statusEntry : status.entrySet()) {
                    filterStatus.put(statusAware.getStatusPrefix() + "." + statusEntry.getKey(), statusEntry.getValue());
                }
            } catch (Throwable t) {
                logger.warn("get status of " + prefix + " failed", t);
            }
        }
        return filterStatus;
    }
    
    public void register(StatusAware statusAware) {
        statusServiceMap.put(statusAware.getStatusPrefix(), statusAware);
    }

    public StatusAware getStatusAware(String prefix) {
        return statusServiceMap.get(prefix);
    }
    
    static StatusCenter statusCenter = new StatusCenter();

    static {
        statusCenter.register(MemoryStatus.get());
        statusCenter.register(JVMStatus.get());
        statusCenter.register(ApacheStatus.get());
    }

	@Bean
    public static StatusCenter get() {
        return statusCenter;
    }

	@Autowired
	public void setMySQLStatus(@Qualifier("mySQLStatus") MySQLStatus status){
		statusCenter.register(status);
	}
	
	@Autowired
	public void setMySQLTableStatus(MySQLTableStatus status){
		statusCenter.register(status);
	}

}
