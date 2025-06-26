package services;

import database.DataBase;
import database.MySqlDb;
import models.Account;
import lombok.SneakyThrows;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AccountService implements IDbExecutorService {
    private final DataBase db;
    private final UserService userService;

    public AccountService() {
        this.db = MySqlDb.getDataBase();
        this.userService = new UserService();
        createTable();
    }

    @SneakyThrows
    public long createAccount(Account account, String userLogin) {
        long userId = userService.getUserIdByLogin(userLogin)
                .orElseThrow(() -> new SQLException("User not found"));

        String sql = "INSERT INTO " + tableName() + " (name, description, currency, userId) VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = db.dbConnection().prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, account.getName());
            statement.setString(2, account.getDescription());
            statement.setString(3, account.getCurrency());
            statement.setLong(4, userId);
            return db.insert(statement);
        }
    }

    @SneakyThrows
    public Optional<Account> getAccountById(long id, String userLogin) {
        long userId = userService.getUserIdByLogin(userLogin)
                .orElseThrow(() -> new SQLException("User not found"));

        String sql = "SELECT * FROM " + tableName() + " WHERE id = ? AND userId = ?";
        try (PreparedStatement statement = db.dbConnection().prepareStatement(sql)) {
            statement.setLong(1, id);
            statement.setLong(2, userId);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToAccount(rs));
                }
            }
        }
        return Optional.empty();
    }

    @SneakyThrows
    public List<Account> getAllAccounts(String userLogin) {
        long userId = userService.getUserIdByLogin(userLogin)
                .orElseThrow(() -> new SQLException("User not found"));
        List<Account> accounts = new ArrayList<>();
        String sql = "SELECT * FROM " + tableName() + " WHERE userId = ?";
        try (PreparedStatement statement = db.dbConnection().prepareStatement(sql)) {
            statement.setLong(1, userId);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    accounts.add(mapRowToAccount(rs));
                }
            }
        }
        return accounts;
    }

    @SneakyThrows
    public int updateAccount(long id, Account account, String userLogin) {
        long userId = userService.getUserIdByLogin(userLogin)
                .orElseThrow(() -> new SQLException("User not found"));
        String sql = "UPDATE " + tableName() + " SET name = ?, description = ?, currency = ? WHERE id = ? AND userId = ?";
        try (PreparedStatement statement = db.dbConnection().prepareStatement(sql)) {
            statement.setString(1, account.getName());
            statement.setString(2, account.getDescription());
            statement.setString(3, account.getCurrency());
            statement.setLong(4, id);
            statement.setLong(5, userId);
            return db.update(statement);
        }
    }

    @SneakyThrows
    public boolean deleteAccount(long id, String userLogin) {
        long userId = userService.getUserIdByLogin(userLogin)
                .orElseThrow(() -> new SQLException("User not found"));
        String sql = "DELETE FROM " + tableName() + " WHERE id = ? AND userId = ?";
        try (PreparedStatement statement = db.dbConnection().prepareStatement(sql)) {
            statement.setLong(1, id);
            statement.setLong(2, userId);
            return db.delete(statement);
        }
    }


    private Account mapRowToAccount(ResultSet rs) throws SQLException {
        Account account = new Account();
        account.setId(rs.getLong("id"));
        account.setName(rs.getString("name"));
        account.setDescription(rs.getString("description"));
        account.setCurrency(rs.getString("currency"));
        account.setUserId(rs.getLong("userId"));
        return account;
    }

    @Override
    public void createTable() {
        String query = "id BIGINT PRIMARY KEY AUTO_INCREMENT, " +
                "name VARCHAR(255) UNIQUE NOT NULL, " +
                "description TEXT, " +
                "currency VARCHAR(10), " +
                "userId BIGINT, " +
                "FOREIGN KEY (userId) REFERENCES UserAccounts(id) ON DELETE CASCADE";
        db.createTable(tableName(), query);
    }

    @Override
    public String tableName() {
        return "Accounts";
    }
}