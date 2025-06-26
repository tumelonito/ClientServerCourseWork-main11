package dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class AssetPosition {
    private long assetId;
    private String name;
    private String ticker;
    private BigDecimal totalQuantity;
    private BigDecimal averageBuyPrice;
    private BigDecimal currentMarketPrice;
    private BigDecimal totalMarketValue;
    private BigDecimal profitOrLoss;
    private BigDecimal profitOrLossPercentage;
}