package com.derby.nuke.wheatgrass.rpc.status;

import javax.sql.DataSource

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * MySQLTableStatus
 *
 * @author Drizzt Yang
 */
@Component
public class MySQLTableStatus extends MySQLStatus implements StatusAware {
    private static final String NAME = "mysql-table";
    private static final Set<String> KEYS = new HashSet<String>();
    static {
        KEYS.add("table.number");
        KEYS.add("table.data-length");
        KEYS.add("table.");
    }

	@Autowired
    public MySQLTableStatus(DataSource datasource) {
        super(datasource);
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
        List<List<String>> statusValues = executeSQL("show table status;");

        BigDecimal dataLength = BigDecimal.ZERO;
        for (List<String> statusEntry : statusValues) {
            String tableName = statusEntry.get(0);
            status.put("table." + tableName + ".engine", statusEntry.get(1));
            status.put("table." + tableName + ".rows", statusEntry.get(4));
            status.put("table." + tableName + ".average-row-length", statusEntry.get(5));
            status.put("table." + tableName + ".data-length", statusEntry.get(6));
            dataLength = dataLength.add(new BigDecimal(statusEntry.get(6)));

            status.put("table." + tableName + ".auto-increment", statusEntry.get(10));
            status.put("table." + tableName + ".create-time", statusEntry.get(11));

            String createSQL = executeSQL("show create table " + tableName).get(0).get(1);
            status.put("table." + tableName + ".create-sql", createSQL);
        }
        status.put("table.number", String.valueOf(statusValues.size()));
        status.put("table.data-length", dataLength.toString());
        return status;
    }
}
