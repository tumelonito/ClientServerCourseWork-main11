package services;

import database.DataBase;
import database.MySqlDb;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.SQLException;
import java.sql.Statement;

@Testcontainers
public abstract class BaseIntegrationTest {

    @Container
    private static final MySQLContainer<?> mySQLContainer = new MySQLContainer<>("mysql:8.3")
            .withDatabaseName("investfolio_test_db")
            .withUsername("test")
            .withPassword("secret");


    @BeforeAll
    static void beforeAll() {
        System.out.println("==================================================");
        System.out.println("Starting MySQL container for integration tests...");
        mySQLContainer.start();

        DataBase testDb = new DataBase(
                mySQLContainer.getDriverClassName(),
                mySQLContainer.getJdbcUrl(),
                mySQLContainer.getUsername(),
                mySQLContainer.getPassword()
        );

        MySqlDb.setTestDatabase(testDb);

        System.out.println("MySQL container started. Test DB is set globally.");
        System.out.println("Creating database schema...");
        createSchema(testDb);
        System.out.println("Database schema created successfully.");
        System.out.println("==================================================");
    }

    @AfterAll
    static void afterAll() {
        System.out.println("==================================================");
        System.out.println("Stopping MySQL container and clearing test DB...");

        MySqlDb.clearTestDatabase();
        mySQLContainer.stop();

        System.out.println("MySQL container stopped.");
        System.out.println("==================================================");
    }

    private static void createSchema(DataBase db) {
        new LoginService().createTable();
        new AccountService().createTable();
        new AssetService().createTable();
        new TransactionService().createTable();
    }

    protected void cleanDatabase() throws SQLException {
        DataBase db = MySqlDb.getDataBase();
        try (Statement statement = db.dbConnection().createStatement()) {
            statement.execute("SET FOREIGN_KEY_CHECKS = 0;");
            statement.execute("TRUNCATE TABLE Transactions;");
            statement.execute("TRUNCATE TABLE Accounts;");
            statement.execute("TRUNCATE TABLE Assets;");
            statement.execute("TRUNCATE TABLE UserAccounts;");
            statement.execute("SET FOREIGN_KEY_CHECKS = 1;");
        }
    }
}