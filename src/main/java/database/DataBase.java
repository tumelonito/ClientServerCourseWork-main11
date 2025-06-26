package database;

import java.sql.*;

public class DataBase implements AutoCloseable {
    protected final Connection dbConnection;

    public DataBase(String driverClass, String connectionString, String username, String password) {
        try {
            Class.forName(driverClass);
            dbConnection = DriverManager.getConnection(connectionString, username, password);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException("Failed to initialize database connection", e);
        }
    }

    public Connection dbConnection (){
        return dbConnection;
    }

    public long insert(PreparedStatement statement) {
        try {
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getLong(1);
                } else {
                    throw new RuntimeException("No ID returned from insert query");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Insert query failed", e);
        }
    }

    public int update(PreparedStatement statement) {
        try {
            return statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Update query failed", e);
        }
    }

    public boolean delete(PreparedStatement statement) {
        try {
            int affectedRows = statement.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Delete query failed", e);
        }
    }

    public ResultSet selectById(PreparedStatement statement) {
        try {
            return statement.executeQuery();
        } catch (SQLException e) {
            throw new RuntimeException("Select by ID query failed", e);
        }
    }

    public ResultSet execQuery(PreparedStatement statement) {
        try {
            return statement.executeQuery();
        } catch (SQLException e) {
            throw new RuntimeException("Error executing query", e);
        }
    }

    public void createTable(String tableName, String columnsQuery) {
        String sql = String.format(
                "CREATE TABLE IF NOT EXISTS %s (%s);", tableName, columnsQuery);

        try (PreparedStatement st = dbConnection.prepareStatement(sql)) {
            st.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void dropTable(String tableName) {
        String sql = String.format("DROP TABLE IF EXISTS %s", tableName);
        try (Statement statement = dbConnection.createStatement()) {
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to drop table", e);
        }
    }

    @Override
    public void close() {
        try {
            if (dbConnection != null && !dbConnection.isClosed()) {
                dbConnection.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to close database connection", e);
        }
    }
}
