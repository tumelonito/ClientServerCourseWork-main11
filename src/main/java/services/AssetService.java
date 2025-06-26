package services;

import database.DataBase;
import database.MySqlDb;
import lombok.SneakyThrows;
import models.Asset;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AssetService implements IDbExecutorService {

    private final DataBase db;

    public AssetService() {
        this.db = MySqlDb.getDataBase();
        createTable();
    }

    @SneakyThrows
    public long createAsset(Asset asset) {
        String sql = "INSERT INTO " + tableName() + " (name, ticker, description, sector, current_price) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement statement = db.dbConnection().prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, asset.getName());
            statement.setString(2, asset.getTicker());
            statement.setString(3, asset.getDescription());
            statement.setString(4, asset.getSector());
            statement.setBigDecimal(5, asset.getCurrentPrice());
            return db.insert(statement);
        }
    }

    @SneakyThrows
    public Optional<Asset> getAssetById(long id) {
        String sql = "SELECT * FROM " + tableName() + " WHERE id = ?";
        try (PreparedStatement statement = db.dbConnection().prepareStatement(sql)) {
            statement.setLong(1, id);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToAsset(rs));
                }
            }
        }
        return Optional.empty();
    }

    @SneakyThrows
    public List<Asset> getAllAssets() {
        List<Asset> assets = new ArrayList<>();
        String sql = "SELECT * FROM " + tableName();
        try (PreparedStatement statement = db.dbConnection().prepareStatement(sql)) {
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    assets.add(mapRowToAsset(rs));
                }
            }
        }
        return assets;
    }

    @SneakyThrows
    public int updateAsset(long id, Asset asset) {
        String sql = "UPDATE " + tableName() + " SET name = ?, description = ?, sector = ?, current_price = ? WHERE id = ?";
        try (PreparedStatement statement = db.dbConnection().prepareStatement(sql)) {
            statement.setString(1, asset.getName());
            statement.setString(2, asset.getDescription());
            statement.setString(3, asset.getSector());
            statement.setBigDecimal(4, asset.getCurrentPrice());
            statement.setLong(5, id);
            return db.update(statement);
        }
    }

    @SneakyThrows
    public boolean deleteAsset(long id) {
        String sql = "DELETE FROM " + tableName() + " WHERE id = ?";
        try (PreparedStatement statement = db.dbConnection().prepareStatement(sql)) {
            statement.setLong(1, id);
            return db.delete(statement);
        }
    }

    private Asset mapRowToAsset(ResultSet rs) throws SQLException {
        Asset asset = new Asset();
        asset.setId(rs.getLong("id"));
        asset.setName(rs.getString("name"));
        asset.setTicker(rs.getString("ticker"));
        asset.setDescription(rs.getString("description"));
        asset.setSector(rs.getString("sector"));
        asset.setCurrentPrice(rs.getBigDecimal("current_price"));
        return asset;
    }

    @Override
    public void createTable() {
        String query = "id BIGINT PRIMARY KEY AUTO_INCREMENT, " +
                "name VARCHAR(255) NOT NULL, " +
                "ticker VARCHAR(20) UNIQUE NOT NULL, " +
                "description TEXT, " +
                "sector VARCHAR(255), " +
                "current_price DECIMAL(18, 8)";
        db.createTable(tableName(), query);
    }

    @Override
    public String tableName() {
        return "Assets";
    }
}