package services;

import dto.PortfolioSummary;
import models.Asset;
import models.Transaction;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("DashboardService Integration Tests")
class DashboardServiceIntegrationTest extends BaseIntegrationTest {

    private DashboardService dashboardService;
    private AccountService accountService;
    private AssetService assetService;
    private TransactionService transactionService;
    private LoginService loginService;

    private long accountId;
    private final String testUser = "investor";

    @BeforeEach
    void setUp() throws SQLException {
        cleanDatabase();
        this.dashboardService = new DashboardService();
        this.accountService = new AccountService();
        this.assetService = new AssetService();
        this.transactionService = new TransactionService();
        this.loginService = new LoginService();

        loginService.signUp(new controllers.auth.LoginRequest(testUser, "pass"));
        this.accountId = accountService.createAccount(new models.Account("Main Portfolio", "", "USD", 0), testUser);
    }

    @Test
    @DisplayName("Should return an empty summary for an account with no transactions")
    void shouldReturnEmptySummaryForNoTransactions() {
        System.out.println("--- Running test: shouldReturnEmptySummaryForNoTransactions ---");
        PortfolioSummary summary = dashboardService.getPortfolioSummaryForAccount(accountId, testUser);
        assertThat(summary).isNotNull();
        assertThat(summary.getAssetPositions()).isEmpty();
        assertThat(summary.getTotalValue()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("Should correctly calculate summary for multiple buys of the same asset")
    void shouldCalculateCorrectlyForMultipleBuys() {
        System.out.println("--- Running test: shouldCalculateCorrectlyForMultipleBuys ---");
        long btcId = createBtcAsset("65000.00");

        transactionService.createTransaction(createTx(btcId, Transaction.TransactionType.BUY, "1.0", "50000.00"), testUser);
        transactionService.createTransaction(createTx(btcId, Transaction.TransactionType.BUY, "1.0", "70000.00"), testUser);

        PortfolioSummary summary = dashboardService.getPortfolioSummaryForAccount(accountId, testUser);

        var btcPosition = summary.getAssetPositions().get(0);
        assertThat(btcPosition.getTotalQuantity()).isEqualByComparingTo("2.0");
        // Average price should be (50000*1 + 70000*1) / (1+1) = 60000
        assertThat(btcPosition.getAverageBuyPrice()).isEqualByComparingTo("60000.00");
        // Total value: 2.0 * 65000 = 130000
        assertThat(btcPosition.getTotalMarketValue()).isEqualByComparingTo("130000.00");
        // P/L: 130000 - (2.0 * 60000) = 10000
        assertThat(btcPosition.getProfitOrLoss()).isEqualByComparingTo("10000.00");
    }

    @Test
    @DisplayName("Should not show an asset if it's fully sold")
    void shouldNotShowFullySoldAsset() {
        System.out.println("--- Running test: shouldNotShowFullySoldAsset ---");
        long btcId = createBtcAsset("65000.00");

        transactionService.createTransaction(createTx(btcId, Transaction.TransactionType.BUY, "1.0", "60000.00"), testUser);
        transactionService.createTransaction(createTx(btcId, Transaction.TransactionType.SELL, "1.0", "62000.00"), testUser);

        PortfolioSummary summary = dashboardService.getPortfolioSummaryForAccount(accountId, testUser);

        assertThat(summary.getAssetPositions()).isEmpty();
    }

    @Test
    @DisplayName("Should correctly calculate totals for a mixed portfolio")
    void shouldCalculateMixedPortfolio() {
        System.out.println("--- Running test: shouldCalculateMixedPortfolio ---");
        long btcId = createBtcAsset("65000.00");
        long ethId = createEthAsset("3500.00");

        // Transactions
        // BTC: buy 0.5 @ 60k -> P/L = (0.5*65k) - (0.5*60k) = 32.5k - 30k = +2500
        transactionService.createTransaction(createTx(btcId, Transaction.TransactionType.BUY, "0.5", "60000.00"), testUser);
        // ETH: buy 10 @ 3k, sell 2 @ 4k. Left 8. P/L = (8*3.5k) - (8*3k) = 28k - 24k = +4000
        transactionService.createTransaction(createTx(ethId, Transaction.TransactionType.BUY, "10", "3000.00"), testUser);
        transactionService.createTransaction(createTx(ethId, Transaction.TransactionType.SELL, "2", "4000.00"), testUser);

        PortfolioSummary summary = dashboardService.getPortfolioSummaryForAccount(accountId, testUser);

        assertThat(summary.getAssetPositions()).hasSize(2);
        // Total value = 32500 (BTC) + 28000 (ETH) = 60500
        assertThat(summary.getTotalValue()).isEqualByComparingTo("60500.00");
        // Total P/L = 2500 (BTC) + 4000 (ETH) = 6500
        assertThat(summary.getTotalProfitOrLoss()).isEqualByComparingTo("6500.00");
        // Sector distribution
        assertThat(summary.getAssetsBySector()).containsEntry("Crypto", new BigDecimal("60500.00"));
    }

    // Helper methods to create assets and transactions
    private long createBtcAsset(String price) {
        Asset btc = new Asset("Bitcoin", "BTC", "", "Crypto");
        btc.setCurrentPrice(new BigDecimal(price));
        return assetService.createAsset(btc);
    }

    private long createEthAsset(String price) {
        Asset eth = new Asset("Ethereum", "ETH", "", "Crypto");
        eth.setCurrentPrice(new BigDecimal(price));
        return assetService.createAsset(eth);
    }

    private Transaction createTx(long assetId, Transaction.TransactionType type, String qty, String price) {
        Transaction t = new Transaction();
        t.setAccountId(this.accountId);
        t.setAssetId(assetId);
        t.setType(type);
        t.setQuantity(new BigDecimal(qty));
        t.setPricePerUnit(new BigDecimal(price));
        return t;
    }
}