package com.derby.nuke.wheatgrass.rpc.status;

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException

import javax.sql.DataSource

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * MySQLStatus
 *
 * @author Drizzt Yang
 */
@Component
public class MySQLStatus implements StatusAware {
    private static final String MYSQL = "mysql";
    private static final Set<String> KEYS = new HashSet<String>();
    static {
        KEYS.add("status.");
        KEYS.add("process.size");
        KEYS.add("process.detail.");
    }

    private DataSource datasource;

	@Autowired
    public MySQLStatus(DataSource datasource) {
        this.datasource = datasource;
    }

    @Override
    public String getStatusPrefix() {
        return MYSQL;
    }

    @Override
    public Set<String> getStatusKeys() {
        return KEYS;
    }

    @Override
    public Map<String, String> status() {
        Map<String, String> status = new LinkedHashMap<String, String> ();
        List<List<String>> statusValues = executeSQL("show status;");
        for (List<String> statusEntry : statusValues) {
            status.put("status." + statusEntry.get(0), statusEntry.get(1));
        }

        List<List<String>> processStatus = executeSQL("show processlist;");
        for (List<String> processEntry : processStatus) {
            String id = processEntry.get(0);
            status.put("process.detail." + id + ".db", processEntry.get(3));
            status.put("process.detail." + id + ".command", processEntry.get(4));
            status.put("process.detail." + id + ".time", processEntry.get(5));
            status.put("process.detail." + id + ".state", processEntry.get(6));
            status.put("process.detail." + id + ".info", processEntry.get(7));
        }
        status.put("process.size", String.valueOf(processStatus.size()));
        return status;
    }

    public DataSource getDatasource() {
        return datasource;
    }

    protected List<List<String>> executeSQL(String sql) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = getDatasource().getConnection();
            preparedStatement = connection.prepareStatement(sql);

            ResultSet resultSet = preparedStatement.executeQuery();
            List<List<String>> results = new ArrayList<List<String>>();
            int size = resultSet.getMetaData().getColumnCount();
            while (resultSet.next()) {
                List<String> resultRow = new ArrayList<String>();
                for (int i = 1; i <= size; i++) {
                    resultRow.add(resultSet.getString(i));
                }
                results.add(resultRow);
            }
            return results;
        } catch (SQLException e) {
            throw new RuntimeException("execute sql for DatabaseConfiguration failed, sql [" + sql + "]", e);
        } finally {
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
            } catch (SQLException ignored) {
            }
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException ignored) {
            }
        }
    }
}
