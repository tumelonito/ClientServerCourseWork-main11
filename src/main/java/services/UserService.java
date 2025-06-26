package services;

import database.DataBase;
import database.MySqlDb;
import lombok.SneakyThrows;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Optional;

public class UserService {
    private final DataBase db;

    public UserService() {
        this.db = MySqlDb.getDataBase();
    }

    @SneakyThrows
    public Optional<Long> getUserIdByLogin(String login) {
        String sql = "SELECT id FROM UserAccounts WHERE login = ?";
        try (PreparedStatement statement = db.dbConnection().prepareStatement(sql)) {
            statement.setString(1, login);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(rs.getLong("id"));
                }
            }
        }
        return Optional.empty();
    }
}