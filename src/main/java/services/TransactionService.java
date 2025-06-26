package services;

import database.DataBase;
import database.MySqlDb;
import lombok.SneakyThrows;
import models.Transaction;

import java.sql.PreparedStatement;
import java.sql.Timestamp;

public class TransactionService implements IDbExecutorService {

    private final DataBase db;

    public TransactionService() {
        this.db = MySqlDb.getDataBase();
        createTable();
    }

    @SneakyThrows
    public long createTransaction(Transaction transaction, String userLogin) {
        String sql = "INSERT INTO " + tableName() +
                " (account_id, asset_id, type, quantity, price_per_unit, transaction_date, commission) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement statement = db.dbConnection().prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.setLong(1, transaction.getAccountId());
            statement.setLong(2, transaction.getAssetId());
            statement.setString(3, transaction.getType().name());
            statement.setBigDecimal(4, transaction.getQuantity());
            statement.setBigDecimal(5, transaction.getPricePerUnit());
            statement.setTimestamp(6, transaction.getTransactionDate() == null ?
                    new Timestamp(System.currentTimeMillis()) :
                    Timestamp.valueOf(transaction.getTransactionDate()));
            statement.setBigDecimal(7, transaction.getCommission());

            return db.insert(statement);
        }
    }

    @Override
    public void createTable() {
        String query = "id BIGINT PRIMARY KEY AUTO_INCREMENT, " +
                "account_id BIGINT NOT NULL, " +
                "asset_id BIGINT NOT NULL, " +
                "type VARCHAR(10) NOT NULL, " +
                "quantity DECIMAL(18, 8) NOT NULL, " +
                "price_per_unit DECIMAL(18, 8) NOT NULL, " +
                "transaction_date DATETIME NOT NULL, " +
                "commission DECIMAL(18, 8), " +
                "FOREIGN KEY (account_id) REFERENCES Accounts(id) ON DELETE CASCADE, " +
                "FOREIGN KEY (asset_id) REFERENCES Assets(id)";
        db.createTable(tableName(), query);
    }

    @Override
    public String tableName() {
        return "Transactions";
    }
}