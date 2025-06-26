package services;

import models.Asset;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("AssetService Integration Tests")
class AssetServiceIntegrationTest extends BaseIntegrationTest {

    private AssetService assetService;

    @BeforeEach
    void setUp() throws SQLException {
        cleanDatabase();
        this.assetService = new AssetService();
    }

    @Test
    @DisplayName("Should create and retrieve a new asset")
    void shouldCreateAndRetrieveAsset() {
        System.out.println("--- Running test: shouldCreateAndRetrieveAsset ---");
        Asset asset = new Asset("Bitcoin", "BTC", "Decentralized digital currency", "Crypto");
        asset.setCurrentPrice(new BigDecimal("68000.50"));

        long newId = assetService.createAsset(asset);
        Optional<Asset> savedAsset = assetService.getAssetById(newId);

        assertThat(newId).isGreaterThan(0);
        assertThat(savedAsset).isPresent();
        assertThat(savedAsset.get().getName()).isEqualTo("Bitcoin");
        assertThat(savedAsset.get().getTicker()).isEqualTo("BTC");
        assertThat(savedAsset.get().getCurrentPrice()).isEqualByComparingTo("68000.50");
    }

    @Test
    @DisplayName("Should retrieve all created assets")
    void shouldRetrieveAllAssets() {
        System.out.println("--- Running test: shouldRetrieveAllAssets ---");
        assetService.createAsset(new Asset("Ethereum", "ETH", "", "Crypto"));
        assetService.createAsset(new Asset("Apple Inc.", "AAPL", "", "Technology"));

        List<Asset> assets = assetService.getAllAssets();

        assertThat(assets).hasSize(2);
    }

    @Test
    @DisplayName("Should update an existing asset")
    void shouldUpdateAsset() {
        System.out.println("--- Running test: shouldUpdateAsset ---");
        Asset originalAsset = new Asset("Cardano", "ADA", "", "Crypto");
        originalAsset.setCurrentPrice(new BigDecimal("0.40"));
        long assetId = assetService.createAsset(originalAsset);

        Asset updatedInfo = new Asset("Cardano", "ADA", "A proof-of-stake blockchain platform", "Crypto");
        updatedInfo.setCurrentPrice(new BigDecimal("0.45"));

        int updatedRows = assetService.updateAsset(assetId, updatedInfo);
        Optional<Asset> fetchedAsset = assetService.getAssetById(assetId);

        assertThat(updatedRows).isEqualTo(1);
        assertThat(fetchedAsset).isPresent();
        assertThat(fetchedAsset.get().getDescription()).isEqualTo("A proof-of-stake blockchain platform");
        assertThat(fetchedAsset.get().getCurrentPrice()).isEqualByComparingTo("0.45");
    }
}