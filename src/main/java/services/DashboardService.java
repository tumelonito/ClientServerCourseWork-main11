package services;

import database.DataBase;
import dto.AssetPosition;
import dto.PortfolioSummary;
import lombok.SneakyThrows;
import models.Account;
import database.MySqlDb;
import models.Asset;
import models.Transaction;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DashboardService {
    private static final int MONETARY_SCALE = 2;
    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;

    private final DataBase db;
    private final UserService userService;
    private final AccountService accountService;
    private final AssetService assetService;

    public DashboardService() {
        this.db = MySqlDb.getDataBase();
        this.userService = new UserService();
        this.accountService = new AccountService();
        this.assetService = new AssetService();
    }

    @SneakyThrows
    public PortfolioSummary getPortfolioSummaryForAccount(long accountId, String userLogin) {
        List<Transaction> transactions = getAllTransactionsForAccount(accountId);
        if (transactions.isEmpty()) {
            PortfolioSummary emptySummary = new PortfolioSummary();
            emptySummary.setAssetPositions(new ArrayList<>());
            emptySummary.setAssetsBySector(new java.util.HashMap<>());
            emptySummary.setTotalValue(BigDecimal.ZERO.setScale(MONETARY_SCALE));
            emptySummary.setTotalProfitOrLoss(BigDecimal.ZERO.setScale(MONETARY_SCALE));
            return emptySummary;
        }

        userService.getUserIdByLogin(userLogin).orElseThrow(() -> new SecurityException("User not found"));
        accountService.getAccountById(accountId, userLogin).orElseThrow(() -> new SecurityException("Account access denied"));

        List<Asset> assetsInvolved = assetService.getAllAssets();
        Map<Long, Asset> assetMap = assetsInvolved.stream().collect(Collectors.toMap(Asset::getId, asset -> asset));

        List<AssetPosition> positions = calculatePositions(transactions, assetMap);

        return buildSummary(positions, assetMap);
    }


    @SneakyThrows
    private List<Transaction> getAllTransactionsForAccount(long accountId) {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM Transactions WHERE account_id = ?";
        try (PreparedStatement st = db.dbConnection().prepareStatement(sql)) {
            st.setLong(1, accountId);
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                Transaction t = new Transaction();
                t.setId(rs.getLong("id"));
                t.setAccountId(rs.getLong("account_id"));
                t.setAssetId(rs.getLong("asset_id"));
                t.setType(Transaction.TransactionType.valueOf(rs.getString("type")));
                t.setQuantity(rs.getBigDecimal("quantity"));
                t.setPricePerUnit(rs.getBigDecimal("price_per_unit"));
                t.setTransactionDate(rs.getTimestamp("transaction_date").toLocalDateTime());
                transactions.add(t);
            }
        }
        return transactions;
    }

    private List<AssetPosition> calculatePositions(List<Transaction> transactions, Map<Long, Asset> assetMap) {
        Map<Long, List<Transaction>> groupedByAsset = transactions.stream().collect(Collectors.groupingBy(Transaction::getAssetId));
        List<AssetPosition> positions = new ArrayList<>();

        for (Map.Entry<Long, List<Transaction>> entry : groupedByAsset.entrySet()) {
            long assetId = entry.getKey();
            List<Transaction> assetTransactions = entry.getValue();
            Asset asset = assetMap.get(assetId);
            if (asset == null) continue;

            BigDecimal totalQuantity = BigDecimal.ZERO;
            BigDecimal totalBuyCost = BigDecimal.ZERO;
            BigDecimal totalBuyQuantity = BigDecimal.ZERO;

            for (Transaction t : assetTransactions) {
                if (t.getType() == Transaction.TransactionType.BUY) {
                    totalQuantity = totalQuantity.add(t.getQuantity());
                    totalBuyQuantity = totalBuyQuantity.add(t.getQuantity());
                    totalBuyCost = totalBuyCost.add(t.getQuantity().multiply(t.getPricePerUnit()));
                } else {
                    totalQuantity = totalQuantity.subtract(t.getQuantity());
                }
            }

            // Only create a position if the quantity is greater than zero
            if (totalQuantity.compareTo(BigDecimal.ZERO) > 0) {
                AssetPosition pos = new AssetPosition();
                pos.setAssetId(assetId);
                pos.setName(asset.getName());
                pos.setTicker(asset.getTicker());
                pos.setTotalQuantity(totalQuantity);

                BigDecimal avgBuyPrice = totalBuyCost.divide(totalBuyQuantity, 8, ROUNDING_MODE);
                pos.setAverageBuyPrice(avgBuyPrice);

                BigDecimal currentPrice = asset.getCurrentPrice() != null ? asset.getCurrentPrice() : BigDecimal.ZERO;
                pos.setCurrentMarketPrice(currentPrice);

                BigDecimal totalMarketValue = totalQuantity.multiply(currentPrice);
                pos.setTotalMarketValue(totalMarketValue);

                BigDecimal currentCostOfInvestment = totalQuantity.multiply(avgBuyPrice);
                BigDecimal profitOrLoss = totalMarketValue.subtract(currentCostOfInvestment);
                pos.setProfitOrLoss(profitOrLoss);

                if (currentCostOfInvestment.compareTo(BigDecimal.ZERO) > 0) {
                    BigDecimal profitPercentage = profitOrLoss.divide(currentCostOfInvestment, 4, ROUNDING_MODE).multiply(new BigDecimal("100"));
                    pos.setProfitOrLossPercentage(profitPercentage);
                } else {
                    pos.setProfitOrLossPercentage(BigDecimal.ZERO);
                }

                positions.add(pos);
            }
        }
        return positions;
    }

    private PortfolioSummary buildSummary(List<AssetPosition> positions, Map<Long, Asset> assetMap) {
        PortfolioSummary summary = new PortfolioSummary();
        summary.setAssetPositions(positions);

        BigDecimal totalValue = positions.stream()
                .map(AssetPosition::getTotalMarketValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(MONETARY_SCALE, ROUNDING_MODE);
        summary.setTotalValue(totalValue);

        BigDecimal totalProfit = positions.stream()
                .map(AssetPosition::getProfitOrLoss)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(MONETARY_SCALE, ROUNDING_MODE);
        summary.setTotalProfitOrLoss(totalProfit);

        Map<String, BigDecimal> assetsBySector = positions.stream()
                .collect(Collectors.groupingBy(
                        p -> assetMap.get(p.getAssetId()).getSector(),
                        Collectors.mapping(p -> p.getTotalMarketValue().setScale(MONETARY_SCALE, ROUNDING_MODE),
                                Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))
                ));
        summary.setAssetsBySector(assetsBySector);

        return summary;
    }
}