package database;

public class MySqlDb {
    private static DataBase instance = null;
    private static DataBase testInstance = null;

    private MySqlDb(DatabaseConfig config) {}

    public static void setTestDatabase(DataBase testDb) {
        testInstance = testDb;
    }

    public static void clearTestDatabase() {
        testInstance = null;
    }

    public static DataBase getDataBase() {
        if (testInstance != null) {
            return testInstance;
        }

        if (instance == null) {
            DatabaseConfig config = new DatabaseConfig();
            instance = new DataBase(
                    "com.mysql.cj.jdbc.Driver",
                    "jdbc:mysql://" + config.getHost() + "/" + config.getDatabase() + "?createDatabaseIfNotExist=true",
                    config.getUsername(),
                    config.getPassword()
            );
        }
        return instance;
    }
}