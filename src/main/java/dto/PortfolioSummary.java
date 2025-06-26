package dto;

import lombok.Data;
import models.Account;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
public class PortfolioSummary {
    private Account account;
    private BigDecimal totalValue;
    private BigDecimal totalProfitOrLoss;
    private BigDecimal totalProfitOrLossPercentage;
    private List<AssetPosition> assetPositions;
    private Map<String, BigDecimal> assetsBySector;
}