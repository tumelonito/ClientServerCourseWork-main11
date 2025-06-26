package database;

import io.github.cdimascio.dotenv.Dotenv;
import lombok.Getter;

@Getter
public class DatabaseConfig {
    private final String host;
    private final String database;
    private final String username;
    private final String password;

    public DatabaseConfig() {
        Dotenv dotenv = loadEnv();
        host = dotenv.get("DB_HOST");
        database = dotenv.get("DB_NAME");
        username = dotenv.get("DB_USER");
        password = dotenv.get("DB_PASS");
    }

    private Dotenv loadEnv() {
        return Dotenv.configure()
                .directory("./")
                .ignoreIfMissing()
                .load();
    }
}
